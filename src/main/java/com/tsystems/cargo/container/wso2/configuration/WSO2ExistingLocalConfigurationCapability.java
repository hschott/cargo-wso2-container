package com.tsystems.cargo.container.wso2.configuration;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractExistingLocalConfigurationCapability;

public class WSO2ExistingLocalConfigurationCapability extends
		AbstractExistingLocalConfigurationCapability {

	private Map<String, Boolean> supportsMap;

	public WSO2ExistingLocalConfigurationCapability() {
		super();

		this.supportsMap = new HashMap<String, Boolean>();

		this.supportsMap.put(GeneralPropertySet.RMI_PORT, Boolean.TRUE);
	        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_USERNAME, Boolean.TRUE);
	        this.supportsMap.put(WSO2CarbonPropertySet.CARBON_PASSWORD, Boolean.TRUE);
		this.supportsMap.put(GeneralPropertySet.JVMARGS, Boolean.TRUE);
		this.supportsMap.put(GeneralPropertySet.RUNTIME_ARGS, Boolean.TRUE);

	}

	@Override
	protected Map<String, Boolean> getPropertySupportMap() {
		return this.supportsMap;
	}

}
