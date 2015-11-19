package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.webapp.mgt.WebappAdminStub;
import org.wso2.carbon.webapp.mgt.xsd.VersionedWebappMetadata;
import org.wso2.carbon.webapp.mgt.xsd.WebappMetadata;
import org.wso2.carbon.webapp.mgt.xsd.WebappUploadData;
import org.wso2.carbon.webapp.mgt.xsd.WebappsWrapper;

import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xWarAdminService extends AbstractWSO2Carbon4xAdminService<WSO2WAR>
{

    private static final String SERVICES_WEBAPP_ADMIN = "/services/WebappAdmin";

    private WebappAdminStub serviceStub;

    public WSO2Carbon4xWarAdminService(Configuration configuration)
    {
        super(configuration);
    }

    protected WebappAdminStub getServiceStub() throws IOException
    {
        if (serviceStub == null)
        {
            WebappAdminStub webappAdminStub =
                new WebappAdminStub(new URL(getUrl() + SERVICES_WEBAPP_ADMIN).toString());
            prepareStub(webappAdminStub);

            serviceStub = webappAdminStub;
        }
        return serviceStub;
    }

    public void deploy(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
            WebappAdminStub webappAdminStub = getServiceStub();

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

    public boolean exists(WSO2WAR deployable, boolean handleFaultyAsExistent)
        throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logExists(name);

            WebappAdminStub webappAdminStub = getServiceStub();

            WebappMetadata startedWebappMetadata = webappAdminStub.getStartedWebapp(name);
            if (startedWebappMetadata != null)
                return true;

            WebappMetadata stoppedWebappMetadata = webappAdminStub.getStoppedWebapp(name);
            if (stoppedWebappMetadata != null)
                return true;

            WebappMetadata faultyWebappMetadata = getFaultyWebappMetadata(webappAdminStub, name);
            if (faultyWebappMetadata != null)
                return handleFaultyAsExistent ? true : false;

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error checking webapp", e);
        }
        return false;
    }

    private WebappMetadata getFaultyWebappMetadata(WebappAdminStub webappAdminStub, String name)
        throws RemoteException
    {
        WebappsWrapper webappsWrapper =
            webappAdminStub.getPagedFaultyWebappsSummary(name, null, 0);

        if (webappsWrapper != null)
        {
            VersionedWebappMetadata[] versionedWebappMetadatas = webappsWrapper.getWebapps();
            if (versionedWebappMetadatas != null)
            {
                for (VersionedWebappMetadata versionedWebappMetadata : versionedWebappMetadatas)
                {
                    if (versionedWebappMetadata != null)
                    {
                        WebappMetadata[] webappMetadatas =
                            versionedWebappMetadata.getVersionGroups();
                        if (webappMetadatas != null)
                        {
                            for (WebappMetadata webappMetadata : webappMetadatas)
                            {
                                if (webappMetadata.getWebappFile().equals(name))
                                {
                                    return webappMetadata;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public void start(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logStart(name);

            WebappAdminStub webappAdminStub = getServiceStub();

            webappAdminStub.startWebapps(new String[] {name});

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error starting webapp", e);
        }
    }

    public void stop(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logStop(name);

            WebappAdminStub webappAdminStub = getServiceStub();

            webappAdminStub.stopWebapps(new String[] {name});

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error stopping webapp", e);
        }
    }

    public void undeploy(WSO2WAR deployable) throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logRemove(name);

            WebappAdminStub webappAdminStub = getServiceStub();

            WebappMetadata faultyWebappMetadata = getFaultyWebappMetadata(webappAdminStub, name);
            if (faultyWebappMetadata != null)
                webappAdminStub.deleteFaultyWebapps(new String[] {name});

            WebappMetadata stoppedWebappMetadata = webappAdminStub.getStoppedWebapp(name);
            if (stoppedWebappMetadata != null)
                webappAdminStub.deleteStoppedWebapps(new String[] {name});

            WebappMetadata startedWebappMetadata = webappAdminStub.getStartedWebapp(name);
            if (startedWebappMetadata != null)
                webappAdminStub.deleteStartedWebapps(new String[] {name});

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing webapp", e);
        }
    }

}
