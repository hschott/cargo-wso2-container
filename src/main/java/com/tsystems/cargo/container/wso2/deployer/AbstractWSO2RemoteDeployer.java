package com.tsystems.cargo.container.wso2.deployer;

import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.deployer.AbstractRemoteDeployer;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServices;

public abstract class AbstractWSO2RemoteDeployer extends AbstractRemoteDeployer {

    private RemoteContainer container;
    protected WSO2AdminServices adminServices;

    public AbstractWSO2RemoteDeployer(RemoteContainer container) {
        super(container);
        this.container = container;
    }

    protected void canBeDeployed(Deployable deployable) {
        if (exists(deployable))
            throw new ContainerException("Failed to deploy [" + deployable.getFile() + "] to ["
                    + adminServices.getUrl() + "]. Deployable with the same context is already deploed.");
    }

    protected abstract void createWso2AdminServices(Configuration configuration);

    @Override
    public void deploy(Deployable deployable) {
        canBeDeployed(deployable);
        supportsDeployable(deployable);
        getLogger().info("Uploading [" + deployable.getFile() + "] to " + adminServices.getUrl(),
                getClass().getSimpleName());
        if (deployable instanceof Axis2Service) {
            adminServices.deploy((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            adminServices.deploy((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            adminServices.deploy((WSO2WAR) deployable);
        } else if (deployable instanceof CarbonApplication) {
            adminServices.deploy((CarbonApplication) deployable);
        } else {
            super.deploy(deployable);
        }
    }

    public boolean exists(Deployable deployable) {
        supportsDeployable(deployable);
        getLogger().info("Check deploy status of [" + deployable.getFile() + "] from " + adminServices.getUrl(),
                getClass().getSimpleName());
        if (deployable instanceof Axis2Service) {
            adminServices.exists((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            adminServices.exists((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            adminServices.exists((WSO2WAR) deployable);
        } else if (deployable instanceof CarbonApplication) {
            adminServices.exists((CarbonApplication) deployable);
        }
        throw new ContainerException("Not supported");
    }

    public WSO2AdminServices getAdminServices() {
        return adminServices;
    }

    protected URL getCarbonBaseURL(Configuration configuration) {
        URL url;

        String managerURL = configuration.getPropertyValue(RemotePropertySet.URI);

        // If not defined by the user use a default URL
        if (managerURL == null) {
            managerURL = configuration.getPropertyValue(GeneralPropertySet.PROTOCOL) + "://"
                    + configuration.getPropertyValue(GeneralPropertySet.HOSTNAME) + ":"
                    + configuration.getPropertyValue(ServletPropertySet.PORT);

            getLogger().info("Setting WSO2 Carbon URL to " + managerURL, getClass().getSimpleName());
        }

        getLogger().debug("WSO2 Carbon URL is " + managerURL, getClass().getSimpleName());

        try {
            url = new URL(managerURL);
        } catch (MalformedURLException e) {
            throw new ContainerException("Invalid WSO2 Carbon URL [" + managerURL + "]", e);
        }

        return url;
    }

    public RemoteContainer getContainer() {
        return container;
    }

    @Override
    public void start(Deployable deployable) {
        supportsDeployable(deployable);
        getLogger().info("Starting [" + deployable.getFile() + "] from " + adminServices.getUrl(),
                getClass().getSimpleName());
        if (deployable instanceof Axis2Service) {
            adminServices.start((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            adminServices.start((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            adminServices.start((WSO2WAR) deployable);
        } else {
            super.start(deployable);
        }
    }

    @Override
    public void stop(Deployable deployable) {
        supportsDeployable(deployable);
        getLogger().info("Stopping [" + deployable.getFile() + "] from " + adminServices.getUrl(),
                getClass().getSimpleName());
        if (deployable instanceof Axis2Service) {
            adminServices.stop((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            adminServices.stop((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            adminServices.stop((WSO2WAR) deployable);
        } else {
            super.start(deployable);
        }
    }

    protected boolean supportsDeployable(Deployable deployable) {
        // Check that the container supports the deployable type to deploy
        if (!getContainer().getCapability().supportsDeployableType(deployable.getType())) {
            throw new ContainerException(deployable.getType().getType().toUpperCase()
                    + " archives are not supported for deployment in [" + getContainer().getId() + "]. Got ["
                    + deployable.getFile() + "]");
        }
        return true;
    }

    @Override
    public void undeploy(Deployable deployable) {
        supportsDeployable(deployable);
        if (exists(deployable)) {
            getLogger().info("Undeploying [" + deployable.getFile() + "] from " + adminServices.getUrl(),
                    getClass().getSimpleName());
            if (deployable instanceof Axis2Service) {
                adminServices.undeploy((Axis2Service) deployable);
            } else if (deployable instanceof Axis2Module) {
                adminServices.undeploy((Axis2Module) deployable);
            } else if (deployable instanceof WSO2WAR) {
                adminServices.undeploy((WSO2WAR) deployable);
            } else if (deployable instanceof CarbonApplication) {
                adminServices.undeploy((CarbonApplication) deployable);
            } else {
                super.deploy(deployable);
            }
        }
    }

}