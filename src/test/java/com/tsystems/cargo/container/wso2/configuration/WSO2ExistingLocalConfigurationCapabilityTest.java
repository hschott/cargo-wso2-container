package com.tsystems.cargo.container.wso2.configuration;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.junit.Assert;
import org.junit.Test;

public class WSO2ExistingLocalConfigurationCapabilityTest
{

    @Test
    public void testSupportsProperty()
    {
        ConfigurationCapability config = new WSO2StandaloneLocalConfigurationCapability();

        Assert.assertTrue(config.supportsProperty(GeneralPropertySet.RMI_PORT));
        Assert.assertTrue(config.supportsProperty(WSO2CarbonPropertySet.CARBON_USERNAME));
        Assert.assertTrue(config.supportsProperty(WSO2CarbonPropertySet.CARBON_PASSWORD));
        Assert.assertTrue(config.supportsProperty(GeneralPropertySet.JVMARGS));
        Assert.assertTrue(config.supportsProperty(GeneralPropertySet.RUNTIME_ARGS));
    }

}
