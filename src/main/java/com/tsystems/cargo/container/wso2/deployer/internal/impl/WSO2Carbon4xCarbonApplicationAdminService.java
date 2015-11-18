package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.application.mgt.ApplicationAdminStub;
import org.wso2.carbon.application.upload.CarbonAppUploaderStub;
import org.wso2.carbon.application.upload.xsd.UploadedFileItem;

import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xCarbonApplicationAdminService extends
    AbstractWSO2Carbon4xAdminService<CarbonApplication>
{

    private static final String SERVICES_CARBON_APP_UPLOADER = "/services/CarbonAppUploader";

    private static final String SERVICES_APPLICATION_ADMIN = "/services/ApplicationAdmin";

    private ApplicationAdminStub serviceStub;

    public WSO2Carbon4xCarbonApplicationAdminService(Configuration configuration)
    {
        super(configuration);
    }

    protected ApplicationAdminStub getServiceStub() throws IOException
    {
        if (serviceStub == null)
        {
            ApplicationAdminStub applicationAdminStub =
                new ApplicationAdminStub(new URL(getUrl() + SERVICES_APPLICATION_ADMIN).toString());
            prepareStub(applicationAdminStub);

            serviceStub = applicationAdminStub;
        }
        return serviceStub;
    }

    public void deploy(CarbonApplication deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
            CarbonAppUploaderStub carbonAppUploaderStub =
                new CarbonAppUploaderStub(new URL(getUrl() + SERVICES_CARBON_APP_UPLOADER).toString());
            prepareStub(carbonAppUploaderStub);

            UploadedFileItem[] carbonAppArray = new UploadedFileItem[1];
            UploadedFileItem carbonApp = new UploadedFileItem();
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            carbonApp.setFileName(new File(deployable.getFile()).getName());
            carbonApp.setDataHandler(dh);
            carbonApp.setFileType("jar");
            carbonAppArray[0] = carbonApp;
            carbonAppUploaderStub.uploadApp(carbonAppArray);
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error uploading carbon application", e);
        }
    }

    public boolean exists(CarbonApplication deployable, boolean handleFaultyAsExistent)
        throws WSO2AdminServicesException
    {
        logExists(deployable.getApplicationName());
        try
        {
            ApplicationAdminStub applicationAdminStub = getServiceStub();

            String[] faultyApplications = applicationAdminStub.listAllFaultyApplications();
            if (faultyApplications != null)
            {
                String artifactName = deployable.getFileHandler().getName(deployable.getFile());

                for (String faultyApplication : faultyApplications)
                {
                    boolean deployed =
                        deployable.getFileHandler().getName(faultyApplication)
                            .equals(artifactName);

                    if (deployed)
                    {
                        return handleFaultyAsExistent ? true : false;
                    }
                }
            }

            String[] existingApplications = applicationAdminStub.listAllApplications();
            if (existingApplications != null)
            {
                for (String existingApplication : existingApplications)
                {
                    if (deployable.matchesApplication(existingApplication))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error checking carbon application", e);
        }

        return false;
    }

    public void undeploy(CarbonApplication deployable) throws WSO2AdminServicesException
    {
        try
        {
            ApplicationAdminStub applicationAdminStub = getServiceStub();

            String[] faultyApplications = applicationAdminStub.listAllFaultyApplications();
            if (faultyApplications != null)
            {
                String artifactName = deployable.getFileHandler().getName(deployable.getFile());

                for (String faultyApplication : faultyApplications)
                {
                    boolean deployed =
                        deployable.getFileHandler().getName(faultyApplication)
                            .equals(artifactName);

                    if (deployed)
                    {
                        logRemove(artifactName);
                        applicationAdminStub
                            .deleteFaultyApplication(new String[] {faultyApplication});
                    }
                }
            }

            String[] existingApplications = applicationAdminStub.listAllApplications();
            if (existingApplications != null)
            {
                for (String application : existingApplications)
                {
                    if (deployable.matchesApplication(application))
                    {
                        logRemove(application);
                        applicationAdminStub.deleteApplication(application);
                    }
                }
            }

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing carbon application", e);
        }
    }

    public void start(CarbonApplication deployable) throws WSO2AdminServicesException
    {
        throw new WSO2AdminServicesException("Not supported");
    }

    public void stop(CarbonApplication deployable) throws WSO2AdminServicesException
    {
        throw new WSO2AdminServicesException("Not supported");
    }

}
