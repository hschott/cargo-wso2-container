package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

/**
 * A WSO2 BAM Toolbox deployable. Matches
 * https://docs.wso2.com/display/BAM241/Creating+a+Custom+Toolbox packaging
 * type.
 */
public class BAMToolbox extends AbstractDeployable {

    public static final DeployableType TYPE = DeployableType.toType("tbox");

    public BAMToolbox(final String file) {
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