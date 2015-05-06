package com.tsystems.cargo.container.wso2.deployer.internal;

import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;

public interface WSO2MediationLibraryAdminService extends WSO2BaseAdminService
{

    public abstract void deploy(WSO2Connector deployable) throws WSO2AdminServicesException;

    public abstract boolean exists(WSO2Connector deployable) throws WSO2AdminServicesException;

    public abstract void start(WSO2Connector deployable) throws WSO2AdminServicesException;

    public abstract void stop(WSO2Connector deployable) throws WSO2AdminServicesException;

    public abstract void undeploy(WSO2Connector deployable) throws WSO2AdminServicesException;

}
