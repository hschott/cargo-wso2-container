package com.tsystems.cargo.container.wso2.configuration;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractExistingLocalConfiguration;
import org.codehaus.cargo.container.spi.deployer.AbstractLocalDeployer;
import org.codehaus.cargo.util.CargoException;

import com.tsystems.cargo.container.wso2.deployer.WSO2Carbon4xInstalledLocalDeployer;

public class WSO2ExistingLocalConfiguration extends AbstractExistingLocalConfiguration {

    public WSO2ExistingLocalConfiguration(String dir) {
        super(dir);
        // default properties
        this.setProperty(GeneralPropertySet.HOSTNAME, "127.0.0.1");
        this.setProperty(WSO2CarbonPropertySet.CARBON_USERNAME, "admin");
        this.setProperty(WSO2CarbonPropertySet.CARBON_PASSWORD, "admin");
        this.setProperty(GeneralPropertySet.RMI_PORT, "9999");
        this.setProperty(ServletPropertySet.PORT, "9763");
    }

    @Override
    protected void doConfigure(LocalContainer c) throws Exception {

        if (!(c instanceof InstalledLocalContainer)) {
            throw new CargoException("Only InstalledLocalContainers are supported, got " + c.getClass().getName());
        }

        InstalledLocalContainer container = (InstalledLocalContainer) c;

        AbstractLocalDeployer deployer = new WSO2Carbon4xInstalledLocalDeployer(container);
        deployer.deploy(getDeployables());

    }

    public ConfigurationCapability getCapability() {
        return new WSO2ExistingLocalConfigurationCapability();
    }

}
