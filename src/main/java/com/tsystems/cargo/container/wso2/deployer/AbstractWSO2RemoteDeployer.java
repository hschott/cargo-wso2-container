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
import org.codehaus.cargo.container.spi.deployer.DeployerWatchdog;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;
import com.tsystems.cargo.container.wso2.deployable.WSO2Deployable;
import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2Axis2ModuleAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2Axis2ServiceAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2BAMToolboxAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2CarbonApplicationAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2MediationLibraryAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2WarAdminService;

public abstract class AbstractWSO2RemoteDeployer extends AbstractRemoteDeployer implements WSO2RemoteDeployer {

    private RemoteContainer container;

    protected WSO2Axis2ModuleAdminService axis2ModuleAdminService;
    protected WSO2Axis2ServiceAdminService axis2ServiceAdminService;
    protected WSO2CarbonApplicationAdminService carbonApplicationAdminService;
    protected WSO2MediationLibraryAdminService mediationLibraryAdminService;
    protected WSO2WarAdminService warAdminService;
    protected WSO2BAMToolboxAdminService bamToolboxAdminService;

    public AbstractWSO2RemoteDeployer(RemoteContainer container) {
        super(container);
        this.container = container;
        createWso2AdminServices();
    }

    protected void canBeDeployed(Deployable deployable) {
        if (deployable instanceof WSO2Deployable) {
            WSO2Deployable wso2Deployable = (WSO2Deployable) deployable;
            if (exists(wso2Deployable))
                throw new ContainerException("Failed to deploy [" + deployable.getFile()
                        + "]. Deployable with the same name or context is already deploed.");
        } else
            throw new ContainerException("Failed to deploy [" + deployable.getFile()
                    + "]. Deployable is not a WSO2 deployable.");
    }

    protected boolean canBeUndeployed(Deployable deployable) {
        if (deployable instanceof WSO2Deployable) {
            WSO2Deployable wso2Deployable = (WSO2Deployable) deployable;
            return exists(wso2Deployable);
        } else
            return false;
    }

    protected abstract void createWso2AdminServices();

    @Override
    public void deploy(Deployable deployable) {
        canBeDeployed(deployable);
        supportsDeployable(deployable);
        if (deployable instanceof Axis2Service) {
            axis2ServiceAdminService.deploy((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            axis2ModuleAdminService.deploy((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            warAdminService.deploy((WSO2WAR) deployable);
        } else if (deployable instanceof CarbonApplication) {
            carbonApplicationAdminService.deploy((CarbonApplication) deployable);
        } else if (deployable instanceof WSO2Connector) {
            mediationLibraryAdminService.deploy((WSO2Connector) deployable);
        } else if (deployable instanceof BAMToolbox) {
            bamToolboxAdminService.deploy((BAMToolbox) deployable);
        } else {
            super.deploy(deployable);
        }

        watchForDeployable(deployable, true);

        if (deployable instanceof WSO2Connector) {
            mediationLibraryAdminService.start((WSO2Connector) deployable);
        }
    }

    public boolean exists(WSO2Deployable deployable) {
        supportsDeployable(deployable);
        if (deployable instanceof Axis2Service) {
            return axis2ServiceAdminService.exists((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            return axis2ModuleAdminService.exists((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            return warAdminService.exists((WSO2WAR) deployable);
        } else if (deployable instanceof CarbonApplication) {
            return carbonApplicationAdminService.exists((CarbonApplication) deployable);
        } else if (deployable instanceof WSO2Connector) {
            return mediationLibraryAdminService.exists((WSO2Connector) deployable);
        } else if (deployable instanceof BAMToolbox) {
            return bamToolboxAdminService.exists((BAMToolbox) deployable);
        }
        throw new ContainerException("Not supported");
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

    public void setAxis2ModuleAdminService(WSO2Axis2ModuleAdminService axis2ModuleAdminService) {
        this.axis2ModuleAdminService = axis2ModuleAdminService;
    }

    public void setAxis2ServiceAdminService(WSO2Axis2ServiceAdminService axis2ServiceAdminService) {
        this.axis2ServiceAdminService = axis2ServiceAdminService;
    }

    public void setBamToolboxAdminService(WSO2BAMToolboxAdminService bamToolboxAdminService) {
        this.bamToolboxAdminService = bamToolboxAdminService;
    }

    public void setCarbonApplicationAdminService(WSO2CarbonApplicationAdminService carbonApplicationAdminService) {
        this.carbonApplicationAdminService = carbonApplicationAdminService;
    }

    public void setMediationLibraryAdminService(WSO2MediationLibraryAdminService mediationLibraryAdminService) {
        this.mediationLibraryAdminService = mediationLibraryAdminService;
    }

    public void setWarAdminService(WSO2WarAdminService warAdminService) {
        this.warAdminService = warAdminService;
    }

    @Override
    public void start(Deployable deployable) {
        supportsDeployable(deployable);
        if (deployable instanceof Axis2Service) {
            axis2ServiceAdminService.start((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            axis2ModuleAdminService.start((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            warAdminService.start((WSO2WAR) deployable);
        } else if (deployable instanceof WSO2Connector) {
            mediationLibraryAdminService.start((WSO2Connector) deployable);
        } else {
            super.start(deployable);
        }
    }

    @Override
    public void stop(Deployable deployable) {
        supportsDeployable(deployable);
        if (deployable instanceof Axis2Service) {
            axis2ServiceAdminService.stop((Axis2Service) deployable);
        } else if (deployable instanceof Axis2Module) {
            axis2ModuleAdminService.stop((Axis2Module) deployable);
        } else if (deployable instanceof WSO2WAR) {
            warAdminService.stop((WSO2WAR) deployable);
        } else if (deployable instanceof WSO2Connector) {
            mediationLibraryAdminService.stop((WSO2Connector) deployable);
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
        if (canBeUndeployed(deployable)) {
            if (deployable instanceof Axis2Service) {
                axis2ServiceAdminService.undeploy((Axis2Service) deployable);
            } else if (deployable instanceof Axis2Module) {
                axis2ModuleAdminService.stop((Axis2Module) deployable);
                axis2ModuleAdminService.undeploy((Axis2Module) deployable);
            } else if (deployable instanceof WSO2WAR) {
                warAdminService.undeploy((WSO2WAR) deployable);
            } else if (deployable instanceof CarbonApplication) {
                carbonApplicationAdminService.undeploy((CarbonApplication) deployable);
            } else if (deployable instanceof WSO2Connector) {
                mediationLibraryAdminService.undeploy((WSO2Connector) deployable);
            } else if (deployable instanceof BAMToolbox) {
                bamToolboxAdminService.undeploy((BAMToolbox) deployable);
            } else {
                super.deploy(deployable);
            }

            watchForDeployable(deployable, false);
        }
    }

    private void watchForDeployable(Deployable deployable, boolean availability) {
        if (deployable instanceof WSO2Deployable) {
            WSO2Deployable wso2Deployable = (WSO2Deployable) deployable;

            if (wso2Deployable.getDeployTimeout() > 0) {
                WSO2RemoteDeployerMonitor monitor = new WSO2RemoteDeployerMonitor(wso2Deployable, this);
                monitor.setLogger(getLogger());
                DeployerWatchdog watchdog = new DeployerWatchdog(monitor);
                watchdog.setLogger(getLogger());
                if (availability) {
                    watchdog.watchForAvailability();
                } else {
                    watchdog.watchForUnavailability();
                }
            }
        }
    }

}