package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.aarservices.xsd.AARServiceData;
import org.wso2.carbon.service.mgt.ServiceAdminStub;
import org.wso2.carbon.service.mgt.xsd.ServiceGroupMetaData;
import org.wso2.carbon.service.mgt.xsd.ServiceMetaData;
import org.wso2.carbon.service.upload.ServiceUploaderStub;

import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xAxis2ServiceAdminService extends
    AbstractWSO2Carbon4xAdminService<Axis2Service>
{

    private static final String SERVICES_SERVICE_UPLOADER = "/services/ServiceUploader";

    private static final String SERVICES_SERVICE_ADMIN = "/services/ServiceAdmin";

    private static final String SERVICES_SERVICE_GROUP_ADMIN = "/services/ServiceGroupAdmin";

    public WSO2Carbon4xAxis2ServiceAdminService(Configuration configuration)
    {
        super(configuration);
    }

    public void deploy(Axis2Service deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
            authenticate();
            ServiceUploaderStub serviceUploaderStub =
                new ServiceUploaderStub(new URL(getUrl() + SERVICES_SERVICE_UPLOADER).toString());
            prepareStub(serviceUploaderStub);

            AARServiceData aarServiceData = new AARServiceData();
            aarServiceData.setFileName(new File(deployable.getFile()).getName());
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            aarServiceData.setDataHandler(dh);
            aarServiceData.setServiceHierarchy("");
            serviceUploaderStub.uploadService(new AARServiceData[] {aarServiceData});
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error uploading axis2 service", e);
        }
    }

    public boolean exists(Axis2Service deployable) throws WSO2AdminServicesException
    {
        logExists(deployable.getApplicationName());
        try
        {
            authenticate();
            ServiceAdminStub serviceAdminStub =
                new ServiceAdminStub(new URL(getUrl() + SERVICES_SERVICE_GROUP_ADMIN).toString());
            prepareStub(serviceAdminStub);

            ServiceGroupMetaData serviceGroupMetaData = null;
            try
            {
                serviceGroupMetaData =
                    serviceAdminStub.listServiceGroup(deployable.getApplicationName());
            }
            catch (Exception e)
            {
                return false;
            }

            if (serviceGroupMetaData != null)
            {
                ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
                if (serviceMetaDataList == null || serviceMetaDataList.length == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error checking axis2 service", e);
        }
        return false;
    }

    public void start(Axis2Service deployable) throws WSO2AdminServicesException
    {
        try
        {
            authenticate();
            ServiceAdminStub serviceGroupAdminStub =
                new ServiceAdminStub(new URL(getUrl() + SERVICES_SERVICE_GROUP_ADMIN).toString());
            ServiceAdminStub serviceAdminStub =
                new ServiceAdminStub(new URL(getUrl() + SERVICES_SERVICE_ADMIN).toString());
            prepareStub(serviceGroupAdminStub);
            prepareStub(serviceAdminStub);

            ServiceGroupMetaData serviceGroupMetaData =
                serviceGroupAdminStub.listServiceGroup(deployable.getApplicationName());

            ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
            if (serviceMetaDataList != null)
            {
                for (ServiceMetaData serviceMetaData : serviceMetaDataList)
                {
                    logStart(serviceMetaData.getName());
                    serviceAdminStub.startService(serviceMetaData.getName());
                }
            }

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error starting axis2 service", e);
        }
    }

    public void stop(Axis2Service deployable) throws WSO2AdminServicesException
    {
        try
        {
            authenticate();
            ServiceAdminStub serviceGroupAdminStub =
                new ServiceAdminStub(new URL(getUrl() + SERVICES_SERVICE_GROUP_ADMIN).toString());
            ServiceAdminStub serviceAdminStub =
                new ServiceAdminStub(new URL(getUrl() + SERVICES_SERVICE_ADMIN).toString());
            prepareStub(serviceGroupAdminStub);
            prepareStub(serviceAdminStub);

            ServiceGroupMetaData serviceGroupMetaData =
                serviceGroupAdminStub.listServiceGroup(deployable.getApplicationName());

            ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
            if (serviceMetaDataList != null)
            {
                for (ServiceMetaData serviceMetaData : serviceMetaDataList)
                {
                    logStop(serviceMetaData.getName());
                    serviceAdminStub.stopService(serviceMetaData.getName());
                }
            }
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error stopping axis2 service", e);
        }
    }

    public void undeploy(Axis2Service deployable) throws WSO2AdminServicesException
    {
        logRemove(deployable.getApplicationName());
        try
        {
            authenticate();
            ServiceAdminStub serviceAdminStub =
                new ServiceAdminStub(new URL(getUrl() + SERVICES_SERVICE_ADMIN).toString());
            prepareStub(serviceAdminStub);

            serviceAdminStub.deleteServiceGroups(new String[] {deployable.getApplicationName()});

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing axis2 service", e);
        }
    }

}
