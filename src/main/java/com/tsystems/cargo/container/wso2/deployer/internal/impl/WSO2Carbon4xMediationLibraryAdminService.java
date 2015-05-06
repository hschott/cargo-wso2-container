package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.wso2.carbon.mediation.library.service.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.service.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.mediation.library.service.upload.xsd.LibraryFileItem;

import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2MediationLibraryAdminService;

public class WSO2Carbon4xMediationLibraryAdminService extends AbstractWSO2Carbon4xAdminService
    implements WSO2MediationLibraryAdminService
{

    public WSO2Carbon4xMediationLibraryAdminService(URL url, String wso2username,
        String wso2password, String httpUsername, String httpPassword)
    {
        super(url, wso2username, wso2password, httpUsername, httpPassword);
    }

    public void deploy(WSO2Connector deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable);
        try
        {
            MediationLibraryUploaderStub mediationLibraryUploaderStub =
                new MediationLibraryUploaderStub(new URL(getUrl()
                    + "/services/MediationLibraryUploader").toString());

            authenticate();
            prepareServiceClient(mediationLibraryUploaderStub._getServiceClient());

            LibraryFileItem[] libraryFileItems = new LibraryFileItem[1];
            LibraryFileItem libraryFileItem = new LibraryFileItem();
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());

            libraryFileItem.setFileName(new File(deployable.getFile()).getName());
            libraryFileItem.setDataHandler(dh);
            libraryFileItem.setFileType("zip");
            libraryFileItems[0] = libraryFileItem;

            mediationLibraryUploaderStub.uploadLibrary(libraryFileItems);
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error uploading connector", e);
        }
    }

    public boolean exists(WSO2Connector deployable) throws WSO2AdminServicesException
    {
        logExists(deployable);
        try
        {
            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub =
                new MediationLibraryAdminServiceStub(new URL(getUrl()
                    + "/services/MediationLibraryAdminService").toString());
            authenticate();
            prepareServiceClient(mediationLibraryAdminServiceStub._getServiceClient());
            String[] libraries = mediationLibraryAdminServiceStub.getAllLibraries();
            if (libraries == null)
            {
                return false;
            }
            for (String library : libraries)
            {
                if (library.equals(deployable.getApplicationName()))
                    return true;
            }
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error checking connector", e);
        }
        return false;
    }

    public void start(WSO2Connector deployable) throws WSO2AdminServicesException
    {
        logStart(deployable);
        try
        {
            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub =
                new MediationLibraryAdminServiceStub(new URL(getUrl()
                    + "/services/MediationLibraryAdminService").toString());
            authenticate();
            prepareServiceClient(mediationLibraryAdminServiceStub._getServiceClient());

            mediationLibraryAdminServiceStub.updateStatus(deployable.getApplicationName(),
                deployable.getLibName(), deployable.getPackageName(), "enabled");

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error starting connector", e);
        }
    }

    public void stop(WSO2Connector deployable) throws WSO2AdminServicesException
    {
        logStop(deployable);
        try
        {
            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub =
                new MediationLibraryAdminServiceStub(new URL(getUrl()
                    + "/services/MediationLibraryAdminService").toString());
            authenticate();
            prepareServiceClient(mediationLibraryAdminServiceStub._getServiceClient());

            mediationLibraryAdminServiceStub.updateStatus(deployable.getApplicationName(), null,
                null, "disabled");

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error stopping connector", e);
        }
    }

    public void undeploy(WSO2Connector deployable) throws WSO2AdminServicesException
    {
        logRemove(deployable);
        try
        {
            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub =
                new MediationLibraryAdminServiceStub(new URL(getUrl()
                    + "/services/MediationLibraryAdminService").toString());
            authenticate();
            prepareServiceClient(mediationLibraryAdminServiceStub._getServiceClient());
            mediationLibraryAdminServiceStub.deleteLibrary(deployable.getApplicationName());
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing connector", e);
        }
    }

}
