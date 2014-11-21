package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.deployable.DeployableType;

/**
 * A WSO2 Axis2 application deployable. Matches
 * https://axis.apache.org/axis2/java/core/docs/xmlbased-server.html packaging
 * type.
 */
public class WSO2Axis2Service extends Axis2Service {

    public static final DeployableType TYPE = DeployableType.toType("service/axis2");

    public WSO2Axis2Service(final String file) {
        super(file);
    }

    @Override
    public DeployableType getType() {
        return TYPE;
    }
}