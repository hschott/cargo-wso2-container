package com.tsystems.cargo.container.wso2.deployer;

import java.net.URL;

import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.RemotePropertySet;

import com.tsystems.cargo.container.wso2.deployer.internal.WSO2Carbon4xAdminServices;

public class WSO2Carbon4xRemoteDeployer extends AbstractWSO2RemoteDeployer {

    public WSO2Carbon4xRemoteDeployer(RemoteContainer container) {
        super(container);
        createWso2AdminServices(container.getConfiguration());
    }

    @Override
    protected void createWso2AdminServices(Configuration configuration) {
        WSO2Carbon4xAdminServices manager;

        URL managerURL = getCarbonBaseURL(configuration);

        String username = configuration.getPropertyValue(RemotePropertySet.USERNAME);
        String password = configuration.getPropertyValue(RemotePropertySet.PASSWORD);

        manager = new WSO2Carbon4xAdminServices(managerURL, username, password);
        manager.setLogger(getLogger());

        adminServices = manager;
    }

}