package com.tsystems.cargo.container.wso2.deployer.internal;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;

public interface WSO2Axis2ModuleAdminService extends WSO2BaseAdminService
{

    public abstract void deploy(Axis2Module deployable) throws WSO2AdminServicesException;

    public abstract boolean exists(Axis2Module deployable) throws WSO2AdminServicesException;

    public abstract void start(Axis2Module deployable) throws WSO2AdminServicesException;

    public abstract void stop(Axis2Module deployable) throws WSO2AdminServicesException;

    public abstract void undeploy(Axis2Module deployable) throws WSO2AdminServicesException;

}
