package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.aarservices.xsd.AARServiceData;
import org.wso2.carbon.service.mgt.ServiceAdminStub;
import org.wso2.carbon.service.mgt.xsd.FaultyService;
import org.wso2.carbon.service.mgt.xsd.FaultyServicesWrapper;
import org.wso2.carbon.service.mgt.xsd.ServiceMetaData;
import org.wso2.carbon.service.mgt.xsd.ServiceMetaDataWrapper;
import org.wso2.carbon.service.upload.ServiceUploaderStub;

import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xAxis2ServiceAdminService extends
    AbstractWSO2Carbon4xAdminService<Axis2Service>
{

    private static final String SERVICES_SERVICE_UPLOADER = "/services/ServiceUploader";

    private static final String SERVICES_SERVICE_ADMIN = "/services/ServiceAdmin";

    private ServiceAdminStub serviceStub;

    public WSO2Carbon4xAxis2ServiceAdminService(Configuration configuration)
    {
        super(configuration);
    }

    protected ServiceAdminStub getServiceStub() throws IOException
    {
        if (serviceStub == null)
        {
            ServiceAdminStub serviceAdminStub =
                new ServiceAdminStub(new URL(getUrl() + SERVICES_SERVICE_ADMIN).toString());
            prepareStub(serviceAdminStub);

            serviceStub = serviceAdminStub;
        }
        return serviceStub;
    }

    public void deploy(Axis2Service deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
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

    public boolean exists(Axis2Service deployable, boolean handleFaultyAsExistent)
        throws WSO2AdminServicesException
    {
        logExists(deployable.getApplicationName());
        try
        {
            ServiceAdminStub serviceAdminStub = getServiceStub();

            FaultyServicesWrapper faultyServicesWrapper =
                serviceAdminStub.getFaultyServiceArchives(0);

            if (faultyServicesWrapper != null)
            {
                FaultyService[] faultyServices = faultyServicesWrapper.getFaultyServices();

                if (faultyServices != null)
                {
                    String artifactName =
                        deployable.getFileHandler().getName(deployable.getFile());

                    for (FaultyService faultyService : faultyServices)
                    {
                        boolean deployed =
                            deployable.getFileHandler().getName(faultyService.getArtifact())
                                .equals(artifactName);

                        if (deployed)
                        {
                            return handleFaultyAsExistent ? true : false;
                        }
                    }
                }
            }

            ServiceMetaDataWrapper serviceMetaDataWrapper =
                serviceAdminStub.listServices("ALL", null, 0);

            if (serviceMetaDataWrapper != null)
            {
                ServiceMetaData[] serviceMetaDataList = serviceMetaDataWrapper.getServices();

                if (serviceMetaDataList != null)
                {
                    for (ServiceMetaData serviceMetaData : serviceMetaDataList)
                    {
                        if (serviceMetaData != null
                            && serviceMetaData.getName().equals(deployable.getApplicationName()))
                        {
                            return true;
                        }
                    }
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
            String name = deployable.getApplicationName();

            logStart(name);

            ServiceAdminStub serviceAdminStub = getServiceStub();

            serviceAdminStub.startService(name);

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
            String name = deployable.getApplicationName();

            logStop(name);

            ServiceAdminStub serviceAdminStub = getServiceStub();

            serviceAdminStub.stopService(name);

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

            ServiceAdminStub serviceAdminStub = getServiceStub();

            FaultyServicesWrapper faultyServicesWrapper =
                serviceAdminStub.getFaultyServiceArchives(0);

            {
                FaultyService[] faultyServices = faultyServicesWrapper.getFaultyServices();

                if (faultyServices != null)
                {
                    String artifactName =
                        deployable.getFileHandler().getName(deployable.getFile());

                    for (FaultyService faultyService : faultyServices)
                    {
                        boolean deployed =
                            deployable.getFileHandler().getName(faultyService.getArtifact())
                                .equals(artifactName);

                        if (deployed)
                        {
                            serviceAdminStub
                                .deleteFaultyServiceGroups(new String[] {faultyService
                                    .getArtifact()});
                        }
                    }
                }
            }

            ServiceMetaDataWrapper serviceMetaDataWrapper =
                serviceStub.listServices("ALL", null, 0);

            ServiceMetaData[] serviceMetaDataList = serviceMetaDataWrapper.getServices();

            if (serviceMetaDataList != null)
            {
                for (ServiceMetaData serviceMetaData : serviceMetaDataList)
                {
                    if (serviceMetaData != null
                        && serviceMetaData.getName().equals(deployable.getApplicationName()))
                    {
                        serviceStub.deleteServiceGroups(new String[] {serviceMetaData
                            .getServiceGroupName()});
                    }
                }
            }

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing axis2 service", e);
        }
    }

}
