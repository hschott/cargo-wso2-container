package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.deployable.WAR;

public class WSO2WAR extends WAR implements WSO2Deployable {

    private String version;

    private long deployTimeout = -1;

    public WSO2WAR(String war) {
        super(war);
    }

    public String getApplicationName() {
        if (version != null && version.length() > 0) {
            return getContext() + "#" + version + (isExpanded() ? "" : ".war");
        } else {
            return getContext() + (isExpanded() ? "" : ".war");
        }
    }

    public long getDeployTimeout() {
        return deployTimeout;
    }

    public String getVersion() {
        return version;
    }

    public void setApplicationName(String applicationName) {
        getLogger().warn("Deployable applicationName can not be set. Use context and version instead.",
                getClass().getSimpleName());
    }

    public void setDeployTimeout(long deployTimeout) {
        this.deployTimeout = deployTimeout;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
