package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.deployable.WAR;

public class WSO2WAR extends WAR {

	private String version;

	public WSO2WAR(String war) {
		super(war);
	}

	public String getName() {
		if (version != null && version.length() > 0) {
			return getContext() + "#" + version + (isExpanded() ? "" : ".war");
		} else {
			return getContext() + (isExpanded() ? "" : ".war");
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
