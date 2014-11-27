package com.tsystems.cargo.container.wso2.deployer.internal;

import com.tsystems.cargo.container.wso2.deployable.Axis2Service;

public interface WSO2Axis2ServiceAdminService extends WSO2BaseAdminService {

    public abstract void deploy(Axis2Service deployable) throws WSO2AdminServicesException;

    public abstract boolean exists(Axis2Service deployable) throws WSO2AdminServicesException;

    public abstract void start(Axis2Service deployable) throws WSO2AdminServicesException;

    public abstract void stop(Axis2Service deployable) throws WSO2AdminServicesException;

    public abstract void undeploy(Axis2Service deployable) throws WSO2AdminServicesException;

}