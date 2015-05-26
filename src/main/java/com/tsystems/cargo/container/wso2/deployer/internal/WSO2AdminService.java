package com.tsystems.cargo.container.wso2.deployer.internal;

import org.codehaus.cargo.util.log.Loggable;

public interface WSO2AdminService<T> extends Loggable
{

    public abstract void deploy(T deployable) throws WSO2AdminServicesException;

    public abstract void undeploy(T deployable) throws WSO2AdminServicesException;

    public abstract boolean exists(T deployable) throws WSO2AdminServicesException;

    public abstract void start(T deployable) throws WSO2AdminServicesException;

    public abstract void stop(T deployable) throws WSO2AdminServicesException;

}
