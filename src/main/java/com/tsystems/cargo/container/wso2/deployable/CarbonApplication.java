package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

/**
 * A WSO2 application deployable. Matches
 * https://docs.wso2.com/display/Carbon420/C-App+Deployment+Process packaging
 * type.
 */
public class CarbonApplication extends AbstractDeployable {

    public static final DeployableType TYPE = DeployableType.toType("carbon/application");

    public CarbonApplication(final String file) {
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