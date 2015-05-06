package com.tsystems.cargo.container.wso2.deployer.internal;

import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;

public interface WSO2CarbonApplicationAdminService extends WSO2BaseAdminService
{

    public abstract void deploy(CarbonApplication deployable) throws WSO2AdminServicesException;

    public abstract boolean exists(CarbonApplication deployable)
        throws WSO2AdminServicesException;

    public abstract void undeploy(CarbonApplication deployable) throws WSO2AdminServicesException;

}
