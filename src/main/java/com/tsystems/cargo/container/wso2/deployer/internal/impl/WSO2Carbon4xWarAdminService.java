package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.webapp.mgt.WebappAdminStub;
import org.wso2.carbon.webapp.mgt.xsd.WebappMetadata;
import org.wso2.carbon.webapp.mgt.xsd.WebappUploadData;

import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xWarAdminService extends AbstractWSO2Carbon4xAdminService<WSO2WAR>
{

    private static final String SERVICES_WEBAPP_ADMIN = "/services/WebappAdmin";

    public WSO2Carbon4xWarAdminService(Configuration configuration)
    {
        super(configuration);
    }

    public void deploy(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable);
        try
        {
            authenticate();
            WebappAdminStub webappAdminStub =
                new WebappAdminStub(new URL(getUrl() + SERVICES_WEBAPP_ADMIN).toString());
            prepareStub(webappAdminStub);

            WebappUploadData webApp = new WebappUploadData();
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            webApp.setFileName(deployable.getApplicationName());
            webApp.setDataHandler(dh);

            webappAdminStub.uploadWebapp(new WebappUploadData[] {webApp});
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error uploading webapp", e);
        }
    }

    public boolean exists(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        logExists(deployable);
        try
        {
            authenticate();
            WebappAdminStub webappAdminStub =
                new WebappAdminStub(new URL(getUrl() + SERVICES_WEBAPP_ADMIN).toString());
            prepareStub(webappAdminStub);

            String name = deployable.getApplicationName();

            WebappMetadata webappMetadata = webappAdminStub.getStartedWebapp(name);
            if (webappMetadata != null)
                return true;
            webappMetadata = webappAdminStub.getStoppedWebapp(name);
            if (webappMetadata != null)
                return true;
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error checking webapp", e);
        }
        return false;
    }

    public void start(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        logStart(deployable);
        try
        {
            authenticate();
            WebappAdminStub webappAdminStub =
                new WebappAdminStub(new URL(getUrl() + SERVICES_WEBAPP_ADMIN).toString());
            prepareStub(webappAdminStub);

            String name = deployable.getApplicationName();

            webappAdminStub.startWebapps(new String[] {name});

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error starting webapp", e);
        }
    }

    public void stop(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        logStop(deployable);
        try
        {
            authenticate();
            WebappAdminStub webappAdminStub =
                new WebappAdminStub(new URL(getUrl() + SERVICES_WEBAPP_ADMIN).toString());
            prepareStub(webappAdminStub);

            String name = deployable.getApplicationName();

            webappAdminStub.stopWebapps(new String[] {name});

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error stopping webapp", e);
        }
    }

    public void undeploy(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        logRemove(deployable);
        try
        {
            authenticate();
            WebappAdminStub webappAdminStub =
                new WebappAdminStub(new URL(getUrl() + SERVICES_WEBAPP_ADMIN).toString());
            prepareStub(webappAdminStub);

            String name = deployable.getApplicationName();

            WebappMetadata webappMetadata = webappAdminStub.getStoppedWebapp(name);
            if (webappMetadata != null)
            {
                if (webappMetadata.getFaulty())
                {
                    webappAdminStub.deleteFaultyWebapps(new String[] {name});
                }
                else
                {
                    webappAdminStub.deleteStoppedWebapps(new String[] {name});
                }
            }
            else
            {
                webappAdminStub.stopWebapps(new String[] {name});
                webappAdminStub.deleteStoppedWebapps(new String[] {name});
            }

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing webapp", e);
        }
    }

}
