package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.wso2.carbon.module.mgt.stub.ModuleAdminServiceStub;
import org.wso2.carbon.module.mgt.xsd.ModuleMetaData;
import org.wso2.carbon.module.mgt.xsd.ModuleUploadData;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2Axis2ModuleAdminService;

public class WSO2Carbon4xAxis2ModuleAdminService extends AbstractWSO2Carbon4xAdminService implements
        WSO2Axis2ModuleAdminService {

    public WSO2Carbon4xAxis2ModuleAdminService(URL url, String username, String password) {
        super(url, username, password);
    }

    public void deploy(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(getUrl()
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());

            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            ModuleUploadData moduleUploadData = new ModuleUploadData();
            moduleUploadData.setFileName(new File(deployable.getFile()).getName());
            moduleUploadData.setDataHandler(dh);
            moduleAdminServiceStub.uploadModule(new ModuleUploadData[] { moduleUploadData });
            getLogger().warn("WSO2 Carbon server restart required to get Axis2 module effective",
                    getClass().getSimpleName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading axis2 module", e);
        }
    }

    public boolean exists(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(getUrl()
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());

            ModuleMetaData[] moduleMetaData = moduleAdminServiceStub.listModules();

            if (moduleMetaData != null) {
                for (ModuleMetaData module : moduleMetaData) {
                    if (module.getModulename().equals(deployable.getApplicationName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error checking axis2 module", e);
        }
        return false;
    }

    public void start(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(getUrl()
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());
            moduleAdminServiceStub.globallyEngageModule(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error starting axis2 module", e);
        }
    }

    public void stop(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(getUrl()
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());
            moduleAdminServiceStub.globallyDisengageModule(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error stopping axis2 module", e);
        }
    }

    public void undeploy(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(getUrl()
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());
            moduleAdminServiceStub.removeModule(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing axis2 module", e);
        }
    }

}
