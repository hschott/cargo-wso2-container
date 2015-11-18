package com.tsystems.cargo.container.wso2.deployer;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;
import org.codehaus.cargo.container.spi.deployer.AbstractRemoteDeployer;
import org.codehaus.cargo.container.spi.deployer.DeployerWatchdog;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;
import com.tsystems.cargo.container.wso2.deployable.WSO2Deployable;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xLoggingViewService;

public abstract class AbstractWSO2RemoteDeployer extends AbstractRemoteDeployer implements
    WSO2RemoteDeployer
{

    private RemoteContainer container;

    private Map<Class< ? extends WSO2Deployable>, WSO2AdminService<Deployable>> adminServices =
        new HashMap<Class< ? extends WSO2Deployable>, WSO2AdminService<Deployable>>();

    private WSO2Carbon4xLoggingViewService loggingViewService;

    public AbstractWSO2RemoteDeployer(RemoteContainer container)
    {
        super(container);
        this.container = container;

        Configuration configuration = getContainer().getConfiguration();

        loggingViewService = new WSO2Carbon4xLoggingViewService(configuration);
    }

    private void canBeDeployed(Deployable deployable)
    {
        if (deployable instanceof WSO2Deployable)
        {
            WSO2Deployable wso2Deployable = (WSO2Deployable) deployable;
            if (found(wso2Deployable, true))
                throw new ContainerException("Failed to deploy ["
                    + deployable.getFile()
                    + "]. Deployable with the same name or context is already deployed. Undeloy it first!");
            if (!((AbstractDeployable) deployable).getFileHandler().exists(deployable.getFile()))
            {
                throw new ContainerException("Failed to deploy [" + deployable.getFile()
                    + "]. Deployable file does not exists.");
            }
        }
        else
            throw new ContainerException("Failed to deploy [" + deployable.getFile()
                + "]. Deployable is not a WSO2 deployable.");
    }

    @Override
    public void deploy(Deployable deployable)
    {
        supportsDeployable(deployable);
        canBeDeployed(deployable);
        preDeployment(deployable);

        getAdminService(deployable.getClass()).deploy(deployable);

        postDeployment(deployable);
    }

    public boolean exists(WSO2Deployable deployable, boolean handleFaultyAsExistent)
    {
        supportsDeployable(deployable);
        return getAdminService(deployable.getClass()).exists(deployable, handleFaultyAsExistent);
    }

    private boolean found(Deployable deployable, boolean handleFaultyAsExistent)
    {
        if (deployable instanceof WSO2Deployable)
        {
            WSO2Deployable wso2Deployable = (WSO2Deployable) deployable;
            return exists(wso2Deployable, handleFaultyAsExistent);
        }
        else
            return false;
    }

    public RemoteContainer getContainer()
    {
        return container;
    }

    private void postDeployment(Deployable deployable)
    {
        try
        {
            watchForDeployable(deployable, true);
        }
        catch (ContainerException e)
        {
            loggingViewService.remoteLog();
            throw e;
        }

        if (deployable instanceof WSO2Connector)
        {
            getAdminService(deployable.getClass()).start(deployable);
        }
    }

    private void preDeployment(Deployable deployable)
    {
        if (deployable instanceof WSO2Connector)
        {
            WSO2Connector wso2deployable = (WSO2Connector) deployable;
            if (wso2deployable.getDeployTimeout() < 30000)
            {
                wso2deployable.setDeployTimeout("30000");
                getLogger()
                    .info("Setting deploy timeout to 30000 ms", getClass().getSimpleName());
            }
        }
    }

    private void preUndeployment(Deployable deployable)
    {
        if (deployable instanceof Axis2Module)
        {
            getAdminService(deployable.getClass()).stop(deployable);
        }
    }

    private void postUndeployment(Deployable deployable)
    {
        try
        {
            watchForDeployable(deployable, false);
        }
        catch (ContainerException e)
        {
            loggingViewService.remoteLog();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    protected void addAdminService(Class< ? extends WSO2Deployable> deployable,
        @SuppressWarnings("rawtypes") WSO2AdminService adminService)
    {
        adminServices.put(deployable, adminService);
    }

    protected WSO2AdminService<Deployable> getAdminService(Class< ? extends Deployable> deployable)
    {
        WSO2AdminService<Deployable> adminService = adminServices.get(deployable);
        if (adminService == null)
        {
            throw new WSO2AdminServicesException("No WSO2 admin service registred for deployable "
                + deployable);
        }
        return adminService;
    }

    @Override
    public void start(Deployable deployable)
    {
        supportsDeployable(deployable);

        if (found(deployable, false))
        {
            getAdminService(deployable.getClass()).start(deployable);
        }
        else
        {
            getLogger().info("Deployable is not deployed.", getClass().getSimpleName());
        }
    }

    @Override
    public void stop(Deployable deployable)
    {
        supportsDeployable(deployable);

        if (found(deployable, false))
        {
            getAdminService(deployable.getClass()).stop(deployable);
        }
        else
        {
            getLogger().info("Deployable is not deployed.", getClass().getSimpleName());
        }
    }

    // CARGO-1296: fix prior 1.4.12
    @Override
    public void stop(Deployable deployable, DeployableMonitor monitor)
    {
        stop(deployable);

        // Wait for the Deployable to be stopped
        DeployerWatchdog watchdog = new DeployerWatchdog(monitor);
        watchdog.setLogger(getLogger());
        watchdog.watchForUnavailability();
    }

    protected boolean supportsDeployable(Deployable deployable)
    {
        // Check that the container supports the deployable type to deploy
        if (!getContainer().getCapability().supportsDeployableType(deployable.getType()))
        {
            throw new ContainerException(deployable.getType().getType().toUpperCase()
                + " archives are not supported for deployment in [" + getContainer().getId()
                + "]. Got [" + deployable.getFile() + "]");
        }
        return true;
    }

    @Override
    public void undeploy(Deployable deployable)
    {
        supportsDeployable(deployable);

        if (found(deployable, true))
        {
            preUndeployment(deployable);

            getAdminService(deployable.getClass()).undeploy(deployable);

            postUndeployment(deployable);
        }
        else
        {
            getLogger().info("Deployable is not deployed.", getClass().getSimpleName());
        }
    }

    private void watchForDeployable(Deployable deployable, boolean availability)
    {
        if (deployable instanceof WSO2Deployable)
        {
            getLogger().info(
                "Watch for deployable to become" + (availability ? " " : " not ") + "effective.",
                getClass().getSimpleName());
            WSO2Deployable wso2Deployable = (WSO2Deployable) deployable;

            if (wso2Deployable.getDeployTimeout() > 0)
            {
                WSO2RemoteDeployerMonitor monitor =
                    new WSO2RemoteDeployerMonitor(wso2Deployable, this, availability);
                monitor.setLogger(getLogger());
                DeployerWatchdog watchdog = new DeployerWatchdog(monitor);
                watchdog.setLogger(getLogger());
                if (availability)
                {
                    watchdog.watchForAvailability();
                    getLogger().info("Deployable is effective.", getClass().getSimpleName());
                }
                else
                {
                    watchdog.watchForUnavailability();

                    // server immediately reports none existent deployable for faulty applications
                    // but the deployer thread has to run one cycle, wait for default deployment
                    // interval
                    try
                    {
                        Thread.sleep(15000);
                    }
                    catch (InterruptedException e)
                    {
                        //
                    }
                    getLogger().info("Deployable is not effective.", getClass().getSimpleName());
                }
            }
        }
    }

}
