package com.tsystems.cargo.container.wso2.deployer;

import org.codehaus.cargo.container.deployer.Deployer;

import com.tsystems.cargo.container.wso2.deployable.WSO2Deployable;

public interface WSO2RemoteDeployer extends Deployer
{

    public boolean exists(WSO2Deployable deployable);

}
