package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.deployable.DeployableType;

/**
 * A WSO2 application deployable. Matches
 * https://docs.wso2.com/display/Carbon420/C-App+Deployment+Process packaging
 * type.
 */
public class CarbonApplication extends AbstractWSO2Deployable implements
	WSO2Deployable {

    public static final DeployableType TYPE = DeployableType
	    .toType("carbon/application");

    private String applicationName;

    public CarbonApplication(final String file) {
	super(file);
    }

    public final String getApplicationName() {
	if (applicationName == null || applicationName.length() == 0) {
	    applicationName = parseApplication(".car");
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