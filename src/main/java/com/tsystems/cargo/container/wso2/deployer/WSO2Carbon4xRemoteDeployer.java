package com.tsystems.cargo.container.wso2.deployer;

import java.net.URL;

import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.RemotePropertySet;

import com.tsystems.cargo.container.wso2.deployer.internal.WSO2Axis2ModuleAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2Axis2ServiceAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2CarbonApplicationAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2MediationLibraryAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2WarAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xAxis2ModuleAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xAxis2ServiceAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xCarbonApplicationAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xMediationLibraryAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xWarAdminService;

public class WSO2Carbon4xRemoteDeployer extends AbstractWSO2RemoteDeployer {

    public WSO2Carbon4xRemoteDeployer(RemoteContainer container) {
        super(container);
    }

    @Override
    protected void createWso2AdminServices() {
        Configuration configuration = getContainer().getConfiguration();

        URL managerURL = getCarbonBaseURL(configuration);

        String username = configuration.getPropertyValue(RemotePropertySet.USERNAME);
        String password = configuration.getPropertyValue(RemotePropertySet.PASSWORD);

        WSO2Axis2ModuleAdminService axis2ModuleAdminService = new WSO2Carbon4xAxis2ModuleAdminService(managerURL,
                username, password);
        axis2ModuleAdminService.setLogger(getLogger());
        setAxis2ModuleAdminService(axis2ModuleAdminService);

        WSO2Axis2ServiceAdminService axis2ServiceAdminService = new WSO2Carbon4xAxis2ServiceAdminService(managerURL,
                username, password);
        axis2ServiceAdminService.setLogger(getLogger());
        setAxis2ServiceAdminService(axis2ServiceAdminService);

        WSO2CarbonApplicationAdminService carbonApplicationAdminService = new WSO2Carbon4xCarbonApplicationAdminService(
                managerURL, username, password);
        carbonApplicationAdminService.setLogger(getLogger());
        setCarbonApplicationAdminService(carbonApplicationAdminService);

        WSO2WarAdminService warAdminService = new WSO2Carbon4xWarAdminService(managerURL, username, password);
        warAdminService.setLogger(getLogger());
        setWarAdminService(warAdminService);

        WSO2MediationLibraryAdminService mediationLibraryAdminService = new WSO2Carbon4xMediationLibraryAdminService(
                managerURL, username, password);
        mediationLibraryAdminService.setLogger(getLogger());
        setMediationLibraryAdminService(mediationLibraryAdminService);

    }

}