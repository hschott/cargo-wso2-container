/*
 * ========================================================================
 *
 * Codehaus CARGO, copyright 2004-2011 Vincent Massol.
 *                           2014 Holger Balow-Schott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package com.tsystems.cargo.container.wso2.deployer;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.container.spi.deployer.AbstractInstalledLocalDeployer;
import org.codehaus.cargo.util.CargoException;

import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;

/**
 * Local deployer that deploys deployables to a <code>deployable</code> directory of the given
 * installed container. Note that this deployer supports some expanded deployables by copying the
 * expanded deployable to the <code>deployable</code> directory. In other words it does not support
 * in-place expanded deployables (e.g. expanded WARs located in a different directory).
 */
public abstract class AbstractWSO2InstalledLocalDeployer extends AbstractInstalledLocalDeployer
{
    /**
     * Contains those DeployableTypes that should not be deployed expanded. Default is to allow
     * expanded deployment and the exceptions to that rule are set here.
     */
    private Set<DeployableType> doNotDeployExpanded = new HashSet<DeployableType>();

    /**
     * @param container InstalledLocalContainer
     * @see AbstractInstalledLocalDeployer#AbstractInstalledLocalDeployer(org.codehaus.cargo.container.LocalContainer)
     */
    public AbstractWSO2InstalledLocalDeployer(InstalledLocalContainer container)
    {
        super(container);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.codehaus.cargo.container.deployer.Deployer#deploy(Deployable)
     */
    @Override
    public void deploy(Deployable deployable)
    {

        // Check that the container supports the deployable type to deploy
        if (!getContainer().getCapability().supportsDeployableType(deployable.getType()))
        {
            throw new ContainerException(deployable.getType().getType().toUpperCase()
                + " archives are not supported for deployment in [" + getContainer().getId()
                + "]. Got [" + deployable.getFile() + "]");
        }

        String deployableDir = getDeployableDir(deployable);

        try
        {
            if (deployable.isExpanded())
            {
                if (!shouldDeployExpanded(deployable.getType()))
                {
                    throw new ContainerException("Container " + getContainer().getName()
                        + " cannot deploy expanded " + deployable.getType() + " deployables");
                }

                if (!getFileHandler().isDirectory(deployable.getFile()))
                {
                    throw new ContainerException("The deployable's file " + deployable.getFile()
                        + " is not a directory, hence cannot be deployed as expanded");
                }
            }

            doDeploy(deployableDir, deployable);
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to deploy [" + deployable.getFile() + "] to ["
                + deployableDir + "]", e);
        }
    }

    /**
     * Do the actual deployment. This can be overriden.
     *
     * @param deployableDir Directory in which to deploy.
     * @param deployable Deployable to deploy.
     */
    protected void doDeploy(String deployableDir, Deployable deployable)
    {
        getLogger().info(
            "Deploying [" + deployable.getFile() + "] to [" + deployableDir + "]...",
            this.getClass().getSimpleName());

        if (!getFileHandler().isDirectory(deployableDir))
        {
            throw new CargoException("Target deployable directory does not exist: "
                + deployableDir);
        }

        String target = getFileHandler().append(deployableDir, getDeployableName(deployable));

        if (deployable.isExpanded())
        {
            if (getFileHandler().exists(target) && !getFileHandler().isDirectory(target))
            {
                // We are trying to deploy an expanded deployable but there
                // already exists a file
                // with the same name as the deployable. Delete, else we have
                // bug CARGO-1037.
                getFileHandler().delete(target);
            }

            getFileHandler().copyDirectory(deployable.getFile(), target);
        }
        else
        {
            if (getFileHandler().exists(target) && getFileHandler().isDirectory(target))
            {
                // We are trying to deploy a file deployable but there already
                // exists a directory
                // with the same name as the deployable. Delete, else we have
                // bug CARGO-1037.
                getFileHandler().delete(target);
            }

            getFileHandler().copyFile(deployable.getFile(), target, true);
        }
    }

    /**
     * Do the actual undeployment. This can be overriden.
     *
     * @param deployableDir Directory in which to undeploy.
     * @param deployable Deployable to undeploy.
     */
    protected void doUndeploy(String deployableDir, Deployable deployable)
    {
        String target = getFileHandler().append(deployableDir, getDeployableName(deployable));
        getFileHandler().delete(target);
    }

    /**
     * Specifies the directory {@link org.codehaus.cargo.container.deployable.Deployable}s should be
     * copied to.
     *
     * @param deployable Deployable to deploy
     * @return String directory to deploy to
     */
    public abstract String getDeployableDir(Deployable deployable);

    /**
     * Gets the deployable name for the given <code>deployable</code>.
     *
     * @param deployable Deployable to get the name for.
     * @return Deployable name.
     */
    protected String getDeployableName(Deployable deployable)
    {
        String deployableName;
        if (DeployableType.WAR.equals(deployable.getType()))
        {
            WSO2WAR war = (WSO2WAR) deployable;
            deployableName = war.getApplicationName();
        }
        else
        {
            deployableName = getFileHandler().getName(deployable.getFile());
        }
        return deployableName;
    }

    /**
     * Decide whether some expanded deployables of the specified type should be deployed or not.
     * Some classes using this deployer may not want to deploy some expanded deployables, as they
     * may want to deploy them in-situ by modifying the container's configuration file to point to
     * the location of the expanded deployable. This saves some copying time and make it easier for
     * development round-trips.
     *
     * @param type the deployable type
     * @param flag whether expanded deployment of the specified deployment type should be allowed or
     *            not
     */
    public void setShouldDeployExpanded(DeployableType type, boolean flag)
    {
        if (flag)
        {
            this.doNotDeployExpanded.remove(type);
        }
        else
        {
            this.doNotDeployExpanded.add(type);
        }
    }

    /**
     * @param type the deployable type
     * @return whether expanded deployment of the specified deployment type should be done
     */
    protected boolean shouldDeployExpanded(DeployableType type)
    {
        return !this.doNotDeployExpanded.contains(type);
    }

    /**
     * {@inheritDoc}
     *
     * @see Deployer#undeploy(org.codehaus.cargo.container.deployable.Deployable)
     */
    @Override
    public void undeploy(Deployable deployable)
    {
        String deployableDir = getDeployableDir(deployable);
        try
        {
            doUndeploy(deployableDir, deployable);
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to undeploy [" + deployable.getFile()
                + "] from [" + deployableDir + "]", e);
        }
    }

}
