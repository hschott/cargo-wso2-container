package com.tsystems.cargo.container.wso2.deployer.internal;

import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;

public interface WSO2WarAdminService extends WSO2BaseAdminService
{

    public abstract void deploy(WSO2WAR deployable) throws WSO2AdminServicesException;

    public abstract boolean exists(WSO2WAR deployable) throws WSO2AdminServicesException;

    public abstract void start(WSO2WAR deployable) throws WSO2AdminServicesException;

    public abstract void stop(WSO2WAR deployable) throws WSO2AdminServicesException;

    public abstract void undeploy(WSO2WAR deployable) throws WSO2AdminServicesException;

}
