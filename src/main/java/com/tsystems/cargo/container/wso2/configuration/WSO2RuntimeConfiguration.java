package com.tsystems.cargo.container.wso2.configuration;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;

public class WSO2RuntimeConfiguration extends AbstractRuntimeConfiguration
{

    public WSO2RuntimeConfiguration()
    {
        super();
        // default properties
        this.setProperty(GeneralPropertySet.HOSTNAME, "localhost");
        this.setProperty(GeneralPropertySet.PROTOCOL, "https");
        this.setProperty(WSO2CarbonPropertySet.CARBON_USERNAME, "admin");
        this.setProperty(WSO2CarbonPropertySet.CARBON_PASSWORD, "admin");
        this.setProperty(ServletPropertySet.PORT, "9443");
    }

    public ConfigurationCapability getCapability()
    {
        return new WSO2RuntimeConfigurationCapability();
    }

}
