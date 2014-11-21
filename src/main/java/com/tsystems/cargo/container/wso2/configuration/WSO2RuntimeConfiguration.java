package com.tsystems.cargo.container.wso2.configuration;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;

public class WSO2RuntimeConfiguration extends AbstractRuntimeConfiguration {

    public WSO2RuntimeConfiguration() {
        super();
        // default properties
        this.setProperty(GeneralPropertySet.HOSTNAME, "127.0.0.1");
        this.setProperty(GeneralPropertySet.PROTOCOL, "http");
        this.setProperty(RemotePropertySet.USERNAME, "admin");
        this.setProperty(RemotePropertySet.PASSWORD, "admin");
        this.setProperty(ServletPropertySet.PORT, "9763");
    }

    public ConfigurationCapability getCapability() {
        return new WSO2RuntimeConfigurationCapability();
    }

}
