package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

/**
 * A Axis2 application deployable. Matches
 * https://axis.apache.org/axis2/java/core/docs/xmlbased-server.html packaging
 * type.
 */
public class Axis2Service extends AbstractDeployable {

    public static final DeployableType TYPE = DeployableType.toType("aar");

    public Axis2Service(final String file) {
        super(file);
    }

    /**
     * @return name of this application extracted from file name
     */
    public final String getApplicationName() {
        final String fileName = getFile();
        return fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("."));
    }

    public DeployableType getType() {
        return TYPE;
    }
}