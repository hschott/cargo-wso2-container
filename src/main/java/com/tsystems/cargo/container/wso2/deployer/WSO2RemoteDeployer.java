package com.tsystems.cargo.container.wso2.deployer;

import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployer.Deployer;

public interface WSO2RemoteDeployer extends Deployer
{

    public boolean exists(Deployable deployable, boolean handleFaultyAsExistent);

}
