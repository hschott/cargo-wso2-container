package com.tsystems.cargo.container.wso2;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.deployable.DeployableType;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;

/**
 * {@link ContainerCapability} supporting {@link CarbonApplication}
 */
public class WSO2ContainerCapability implements ContainerCapability
{

    public boolean supportsDeployableType(final DeployableType type)
    {
        return CarbonApplication.TYPE.equals(type) || DeployableType.WAR.equals(type)
            || Axis2Module.TYPE.equals(type) || Axis2Service.TYPE.equals(type)
            || WSO2Axis2Service.TYPE.equals(type) || WSO2Connector.TYPE.equals(type)
            || BAMToolbox.TYPE.equals(type);
    }

}
