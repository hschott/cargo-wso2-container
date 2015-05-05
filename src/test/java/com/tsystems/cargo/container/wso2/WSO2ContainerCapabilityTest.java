package com.tsystems.cargo.container.wso2;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.junit.Assert;
import org.junit.Test;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;

public class WSO2ContainerCapabilityTest {

    @Test
    public void testSupportsDeployableType() {
	ContainerCapability capability = new WSO2ContainerCapability();

	Assert.assertTrue(capability
		.supportsDeployableType(CarbonApplication.TYPE));
	Assert.assertTrue(capability.supportsDeployableType(DeployableType.WAR));
	Assert.assertTrue(capability.supportsDeployableType(Axis2Module.TYPE));
	Assert.assertTrue(capability.supportsDeployableType(Axis2Service.TYPE));
	Assert.assertTrue(capability
		.supportsDeployableType(WSO2Axis2Service.TYPE));
	Assert.assertTrue(capability.supportsDeployableType(WSO2Connector.TYPE));
	Assert.assertTrue(capability.supportsDeployableType(BAMToolbox.TYPE));
    }
}
