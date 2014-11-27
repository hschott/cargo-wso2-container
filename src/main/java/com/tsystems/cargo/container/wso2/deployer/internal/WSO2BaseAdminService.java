package com.tsystems.cargo.container.wso2.deployer.internal;

import java.net.URL;

import org.codehaus.cargo.util.log.Loggable;

public interface WSO2BaseAdminService extends Loggable {

    public abstract URL getUrl();

}