package com.tsystems.cargo.container.wso2.configuration;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfigurationCapability;

public class WSO2RuntimeConfigurationCapability extends AbstractRuntimeConfigurationCapability
{

    private Map<String, Boolean> supportsMap;

    public WSO2RuntimeConfigurationCapability()
    {
        super();

        this.supportsMap = new HashMap<String, Boolean>();

        this.supportsMap.put(GeneralPropertySet.PROTOCOL, Boolean.TRUE);
        this.supportsMap.put(ServletPropertySet.PORT, Boolean.TRUE);
        this.supportsMap.put(RemotePropertySet.URI, Boolean.TRUE);

        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_USERNAME, Boolean.TRUE);
        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_PASSWORD, Boolean.TRUE);

    }

    @Override
    protected Map<String, Boolean> getPropertySupportMap()
    {
        return this.supportsMap;
    }

}
