package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import org.codehaus.cargo.container.configuration.Configuration;

import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminService;

public abstract class AbstractWSO2Carbon4xAdminService<T> extends
    AbstractWSO2Carbon4xRemoteService implements WSO2AdminService<T>
{
    public AbstractWSO2Carbon4xAdminService(Configuration configuration)
    {
        super(configuration);
    }

    protected void logExists(String deployable)
    {
        getLogger().debug("Check deploy status of [" + deployable + "] on " + getUrl(),
            getClass().getSimpleName());
    }

    protected void logRemove(String deployable)
    {
        getLogger().info("Remove [" + deployable + "] from " + getUrl(),
            getClass().getSimpleName());
    }

    protected void logStart(String deployable)
    {
        getLogger().info("Start [" + deployable + "] on " + getUrl(), getClass().getSimpleName());
    }

    protected void logStop(String deployable)
    {
        getLogger().info("Stopp [" + deployable + "] on " + getUrl(), getClass().getSimpleName());
    }

    protected void logUpload(String deployable)
    {
        getLogger()
            .info("Upload [" + deployable + "] to " + getUrl(), getClass().getSimpleName());

    }
}
