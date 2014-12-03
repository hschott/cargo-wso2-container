package com.tsystems.cargo.container.wso2;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.configuration.RuntimeConfiguration;
import org.codehaus.cargo.container.spi.AbstractRemoteContainer;

public class WSO2Carbon4xRemoteContainer extends AbstractRemoteContainer implements WSO2Carbon4xContainer {

    public static final String NAME = "Remote WSO2 Carbon 4.x";

    public WSO2Carbon4xRemoteContainer(RuntimeConfiguration configuration) {
        super(configuration);
    }

    public ContainerCapability getCapability() {
        return new WSO2ContainerCapability();
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return NAME;
    }

}