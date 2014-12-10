package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.deployable.Deployable;

public interface WSO2Deployable extends Deployable {

    public String getApplicationName();

    public long getDeployTimeout();

    public void setApplicationName(String applicationName);

    public void setDeployTimeout(long deployTimeout);
}
