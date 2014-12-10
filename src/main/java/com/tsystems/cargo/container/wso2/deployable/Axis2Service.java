package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

/**
 * A Axis2 application deployable. Matches
 * https://axis.apache.org/axis2/java/core/docs/xmlbased-server.html packaging
 * type.
 */
public class Axis2Service extends AbstractDeployable implements WSO2Deployable {

    public static final DeployableType TYPE = DeployableType.toType("aar");

    private long deployTimeout = -1;

    private String applicationName;

    public Axis2Service(final String file) {
        super(file);
    }

    public final String getApplicationName() {
        if (applicationName == null || applicationName.length() == 0) {
            final String fileName = getFile();
            applicationName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("."));
        }
        return applicationName;
    }

    public long getDeployTimeout() {
        return deployTimeout;
    }

    public DeployableType getType() {
        return TYPE;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setDeployTimeout(long deployTimeout) {
        this.deployTimeout = deployTimeout;
    }
}