package com.tsystems.cargo.container.wso2.configuration;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.property.DatasourcePropertySet;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability;

public class WSO2StandaloneLocalConfigurationCapability extends AbstractStandaloneLocalConfigurationCapability {

    private Map<String, Boolean> supportsMap;

    public WSO2StandaloneLocalConfigurationCapability() {
        super();

        this.supportsMap = new HashMap<String, Boolean>();

        this.supportsMap.put(DatasourcePropertySet.DATASOURCE, Boolean.TRUE);
        this.supportsMap.put(DatasourcePropertySet.CONNECTION_TYPE, Boolean.TRUE);
        this.supportsMap.put(GeneralPropertySet.RMI_PORT, Boolean.TRUE);
        this.supportsMap.put(ServletPropertySet.USERS, Boolean.FALSE);
        this.supportsMap.put(GeneralPropertySet.LOGGING, Boolean.FALSE);
        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_USERNAME, Boolean.TRUE);
        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_PASSWORD, Boolean.TRUE);
        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_CONTEXT_ROOT, Boolean.TRUE);
        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_SERVER_ROLES, Boolean.TRUE);
    }

    @Override
    protected Map<String, Boolean> getPropertySupportMap() {
        return this.supportsMap;
    }

}
