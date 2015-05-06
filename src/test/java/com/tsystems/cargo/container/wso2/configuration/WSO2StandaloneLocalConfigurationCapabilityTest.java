package com.tsystems.cargo.container.wso2.configuration;

import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.property.DatasourcePropertySet;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.junit.Assert;
import org.junit.Test;

public class WSO2StandaloneLocalConfigurationCapabilityTest
{

    @Test
    public void testSupportsProperty()
    {
        ConfigurationCapability config = new WSO2StandaloneLocalConfigurationCapability();

        Assert.assertTrue(config.supportsProperty(DatasourcePropertySet.DATASOURCE));
        Assert.assertTrue(config.supportsProperty(DatasourcePropertySet.CONNECTION_TYPE));
        Assert.assertTrue(config.supportsProperty(GeneralPropertySet.RMI_PORT));
        Assert.assertTrue(config.supportsProperty(WSO2CarbonPropertySet.CARBON_USERNAME));
        Assert.assertTrue(config.supportsProperty(WSO2CarbonPropertySet.CARBON_PASSWORD));
        Assert.assertTrue(config.supportsProperty(WSO2CarbonPropertySet.CARBON_CONTEXT_ROOT));
        Assert.assertTrue(config.supportsProperty(WSO2CarbonPropertySet.CARBON_SERVER_ROLES));
        Assert.assertFalse(config.supportsProperty(ServletPropertySet.USERS));
        Assert.assertFalse(config.supportsProperty(GeneralPropertySet.LOGGING));
    }

}
