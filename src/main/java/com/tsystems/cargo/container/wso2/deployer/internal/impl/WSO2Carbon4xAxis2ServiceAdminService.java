package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.wso2.carbon.aarservices.xsd.AARServiceData;
import org.wso2.carbon.service.mgt.ServiceAdminStub;
import org.wso2.carbon.service.mgt.xsd.ServiceGroupMetaData;
import org.wso2.carbon.service.mgt.xsd.ServiceMetaData;
import org.wso2.carbon.service.upload.ServiceUploaderStub;

import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2Axis2ServiceAdminService;

public class WSO2Carbon4xAxis2ServiceAdminService extends AbstractWSO2Carbon4xAdminService implements
        WSO2Axis2ServiceAdminService {

    public WSO2Carbon4xAxis2ServiceAdminService(URL url, String username, String password) {
        super(url, username, password);
    }

    public void deploy(Axis2Service deployable) throws WSO2AdminServicesException {
        logUpload(deployable);
        try {
            ServiceUploaderStub serviceUploaderStub = new ServiceUploaderStub(new URL(getUrl()
                    + "/services/ServiceUploader").toString());
            authenticate();
            prepareServiceClient(serviceUploaderStub._getServiceClient());

            AARServiceData aarServiceData = new AARServiceData();
            aarServiceData.setFileName(new File(deployable.getFile()).getName());
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            aarServiceData.setDataHandler(dh);
            aarServiceData.setServiceHierarchy("");
            serviceUploaderStub.uploadService(new AARServiceData[] { aarServiceData });
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading axis2 service", e);
        }
    }

    public boolean exists(Axis2Service deployable) throws WSO2AdminServicesException {
        logExists(deployable);
        try {
            ServiceAdminStub serviceAdminStub = new ServiceAdminStub(
                    new URL(getUrl() + "/services/ServiceGroupAdmin").toString());
            authenticate();
            prepareServiceClient(serviceAdminStub._getServiceClient());

            ServiceGroupMetaData serviceGroupMetaData = null;
            try {
                serviceGroupMetaData = serviceAdminStub.listServiceGroup(deployable.getApplicationName());
            } catch (Exception e) {
                return false;
            }

            if (serviceGroupMetaData != null) {
                ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
                if (serviceMetaDataList == null || serviceMetaDataList.length == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error checking axis2 service", e);
        }
        return false;
    }

    public void start(Axis2Service deployable) throws WSO2AdminServicesException {
        logStart(deployable);
        try {
            ServiceAdminStub serviceGroupAdminStub = new ServiceAdminStub(new URL(getUrl()
                    + "/services/ServiceGroupAdmin").toString());
            ServiceAdminStub serviceAdminStub = new ServiceAdminStub(
                    new URL(getUrl() + "/services/ServiceAdmin").toString());
            authenticate();
            prepareServiceClient(serviceGroupAdminStub._getServiceClient());
            prepareServiceClient(serviceAdminStub._getServiceClient());

            ServiceGroupMetaData serviceGroupMetaData = serviceGroupAdminStub.listServiceGroup(deployable
                    .getApplicationName());

            ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
            if (serviceMetaDataList != null) {
                for (ServiceMetaData serviceMetaData : serviceMetaDataList) {
                    serviceAdminStub.startService(serviceMetaData.getName());
                }
            }

        } catch (Exception e) {
            throw new WSO2AdminServicesException("error starting axis2 service", e);
        }
    }

    public void stop(Axis2Service deployable) throws WSO2AdminServicesException {
        logStop(deployable);
        try {
            ServiceAdminStub serviceGroupAdminStub = new ServiceAdminStub(new URL(getUrl()
                    + "/services/ServiceGroupAdmin").toString());
            ServiceAdminStub serviceAdminStub = new ServiceAdminStub(
                    new URL(getUrl() + "/services/ServiceAdmin").toString());
            authenticate();
            prepareServiceClient(serviceGroupAdminStub._getServiceClient());
            prepareServiceClient(serviceAdminStub._getServiceClient());

            ServiceGroupMetaData serviceGroupMetaData = serviceGroupAdminStub.listServiceGroup(deployable
                    .getApplicationName());

            ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
            if (serviceMetaDataList != null) {
                for (ServiceMetaData serviceMetaData : serviceMetaDataList) {
                    serviceAdminStub.stopService(serviceMetaData.getName());
                }
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error stopping axis2 service", e);
        }
    }

    public void undeploy(Axis2Service deployable) throws WSO2AdminServicesException {
        logRemove(deployable);
        try {
            ServiceAdminStub serviceAdminStub = new ServiceAdminStub(
                    new URL(getUrl() + "/services/ServiceAdmin").toString());
            authenticate();
            prepareServiceClient(serviceAdminStub._getServiceClient());

            serviceAdminStub.deleteServiceGroups(new String[] { deployable.getApplicationName() });

        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing axis2 service", e);
        }
    }

}
