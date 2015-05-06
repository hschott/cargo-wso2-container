package com.tsystems.cargo.container.wso2.deployer.internal;

import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;

public interface WSO2BAMToolboxAdminService extends WSO2BaseAdminService
{

    public abstract void deploy(BAMToolbox deployable) throws WSO2AdminServicesException;

    public abstract boolean exists(BAMToolbox deployable) throws WSO2AdminServicesException;

    public abstract void undeploy(BAMToolbox deployable) throws WSO2AdminServicesException;

}
