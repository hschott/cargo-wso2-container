package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;

import org.codehaus.cargo.container.deployable.DeployableType;

/**
 * A Axis2 application deployable. Matches
 * https://axis.apache.org/axis2/java/core/docs/xmlbased-server.html packaging
 * type.
 */
public class Axis2Service extends AbstractWSO2Deployable implements WSO2Deployable {

    public static final DeployableType TYPE = DeployableType.toType("aar");

    private String applicationName;

    public Axis2Service(final String file) {
        super(file);
    }

    public final String getApplicationName() {
        if (applicationName == null || applicationName.length() == 0) {
            applicationName = parseApplication(".aar");
        }
        return applicationName;
    }

    public DeployableType getType() {
        return TYPE;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}