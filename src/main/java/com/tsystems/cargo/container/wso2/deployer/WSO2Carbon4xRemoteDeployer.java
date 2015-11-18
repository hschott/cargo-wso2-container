package com.tsystems.cargo.container.wso2.deployer;

import org.codehaus.cargo.container.RemoteContainer;
import org.codehaus.cargo.container.configuration.Configuration;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;
import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xAxis2ModuleAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xAxis2ServiceAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xBAMToolboxAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xCarbonApplicationAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xMediationLibraryAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xWarAdminService;

public class WSO2Carbon4xRemoteDeployer extends AbstractWSO2RemoteDeployer
{

    public WSO2Carbon4xRemoteDeployer(RemoteContainer container)
    {
        super(container);

        Configuration configuration = getContainer().getConfiguration();
        createWso2AdminServices(configuration);
    }

    protected void createWso2AdminServices(Configuration configuration)
    {

        WSO2AdminService<Axis2Module> axis2ModuleAdminService =
            new WSO2Carbon4xAxis2ModuleAdminService(configuration);
        addAdminService(Axis2Module.class, axis2ModuleAdminService);

        WSO2AdminService<Axis2Service> axis2ServiceAdminService =
            new WSO2Carbon4xAxis2ServiceAdminService(configuration);
        addAdminService(Axis2Service.class, axis2ServiceAdminService);
        addAdminService(WSO2Axis2Service.class, axis2ServiceAdminService);

        WSO2AdminService<CarbonApplication> carbonApplicationAdminService =
            new WSO2Carbon4xCarbonApplicationAdminService(configuration);
        addAdminService(CarbonApplication.class, carbonApplicationAdminService);
        addAdminService(WSO2CarbonApplication.class, carbonApplicationAdminService);

        WSO2AdminService<WSO2WAR> warAdminService =
            new WSO2Carbon4xWarAdminService(configuration);
        addAdminService(WSO2WAR.class, warAdminService);

        WSO2AdminService<WSO2Connector> mediationLibraryAdminService =
            new WSO2Carbon4xMediationLibraryAdminService(configuration);
        addAdminService(WSO2Connector.class, mediationLibraryAdminService);

        WSO2AdminService<BAMToolbox> bamToolboxAdminService =
            new WSO2Carbon4xBAMToolboxAdminService(configuration);
        addAdminService(BAMToolbox.class, bamToolboxAdminService);

    }

}
