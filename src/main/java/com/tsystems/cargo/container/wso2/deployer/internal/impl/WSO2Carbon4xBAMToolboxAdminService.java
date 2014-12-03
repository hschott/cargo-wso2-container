package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.wso2.carbon.bam.toolbox.deployer.service.BAMToolboxDepolyerServiceStub;
import org.wso2.carbon.bam.toolbox.deployer.util.xsd.ToolBoxStatusDTO;

import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2BAMToolboxAdminService;

public class WSO2Carbon4xBAMToolboxAdminService extends AbstractWSO2Carbon4xAdminService implements
        WSO2BAMToolboxAdminService {

    public WSO2Carbon4xBAMToolboxAdminService(URL url, String username, String password) {
        super(url, username, password);
    }

    public void deploy(BAMToolbox deployable) throws WSO2AdminServicesException {
        logUpload(deployable);
        try {
            BAMToolboxDepolyerServiceStub bamToolboxDepolyerServiceStub = new BAMToolboxDepolyerServiceStub(new URL(
                    getUrl() + "/services/BAMToolboxDepolyerService").toString());
            authenticate();
            prepareServiceClient(bamToolboxDepolyerServiceStub._getServiceClient());
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            bamToolboxDepolyerServiceStub.uploadBAMToolBox(dh, new File(deployable.getFile()).getName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading bam toolbox", e);
        }
    }

    public boolean exists(BAMToolbox deployable) throws WSO2AdminServicesException {
        logExists(deployable);
        try {
            BAMToolboxDepolyerServiceStub bamToolboxDepolyerServiceStub = new BAMToolboxDepolyerServiceStub(new URL(
                    getUrl() + "/services/BAMToolboxDepolyerService").toString());
            authenticate();
            prepareServiceClient(bamToolboxDepolyerServiceStub._getServiceClient());
            ToolBoxStatusDTO toolBoxStatusDTO = bamToolboxDepolyerServiceStub.getDeployedToolBoxes("1", "");
            String[] deployedTools = toolBoxStatusDTO.getDeployedTools();
            if (null != deployedTools) {
                for (String deployedTool : deployedTools) {
                    if (deployedTool.equals(deployable.getApplicationName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error checking bam toolbox", e);
        }
        return false;
    }

    public void undeploy(BAMToolbox deployable) throws WSO2AdminServicesException {
        logRemove(deployable);
        try {
            BAMToolboxDepolyerServiceStub bamToolboxDepolyerServiceStub = new BAMToolboxDepolyerServiceStub(new URL(
                    getUrl() + "/services/BAMToolboxDepolyerService").toString());
            authenticate();
            prepareServiceClient(bamToolboxDepolyerServiceStub._getServiceClient());
            bamToolboxDepolyerServiceStub.undeployToolBox(new String[] { deployable.getApplicationName() });
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing bam toolbox", e);
        }
    }

}
