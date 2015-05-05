package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import javax.activation.DataHandler;

import org.wso2.carbon.application.mgt.ApplicationAdminStub;
import org.wso2.carbon.application.upload.CarbonAppUploaderStub;
import org.wso2.carbon.application.upload.xsd.UploadedFileItem;

import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2CarbonApplicationAdminService;

public class WSO2Carbon4xCarbonApplicationAdminService extends AbstractWSO2Carbon4xAdminService implements
        WSO2CarbonApplicationAdminService {

    public WSO2Carbon4xCarbonApplicationAdminService(URL url, String wso2username, String wso2password, String httpUsername, String httpPassword) {
        super(url, wso2username, wso2password, httpUsername, httpPassword);
    }

    public void deploy(CarbonApplication deployable) throws WSO2AdminServicesException {
        logUpload(deployable);
        try {
            CarbonAppUploaderStub carbonAppUploaderStub = new CarbonAppUploaderStub(new URL(getUrl()
                    + "/services/CarbonAppUploader").toString());
            authenticate();
            prepareServiceClient(carbonAppUploaderStub._getServiceClient());
            UploadedFileItem[] carbonAppArray = new UploadedFileItem[1];
            UploadedFileItem carbonApp = new UploadedFileItem();
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            carbonApp.setFileName(new File(deployable.getFile()).getName());
            carbonApp.setDataHandler(dh);
            carbonApp.setFileType("jar");
            carbonAppArray[0] = carbonApp;
            carbonAppUploaderStub.uploadApp(carbonAppArray);
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading carbon application", e);
        }
    }

    public boolean exists(CarbonApplication deployable) throws WSO2AdminServicesException {
        logExists(deployable);
        try {
            ApplicationAdminStub applicationAdminStub = new ApplicationAdminStub(new URL(getUrl()
                    + "/services/ApplicationAdmin").toString());
            authenticate();
            prepareServiceClient(applicationAdminStub._getServiceClient());
            String[] existingApplications = applicationAdminStub.listAllApplications();
            if (existingApplications != null
                    && Arrays.asList(existingApplications).contains(deployable.getApplicationName())) {
                return true;
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error checking carbon application", e);
        }

        return false;
    }

    public void undeploy(CarbonApplication deployable) throws WSO2AdminServicesException {
        logRemove(deployable);
        try {
            ApplicationAdminStub applicationAdminStub = new ApplicationAdminStub(new URL(getUrl()
                    + "/services/ApplicationAdmin").toString());
            authenticate();
            prepareServiceClient(applicationAdminStub._getServiceClient());
            applicationAdminStub.deleteApplication(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing carbon application", e);
        }
    }

}
