package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.mediation.library.service.MediationLibraryAdminServiceStub;
import org.wso2.carbon.mediation.library.service.upload.MediationLibraryUploaderStub;
import org.wso2.carbon.mediation.library.service.upload.xsd.LibraryFileItem;

import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xMediationLibraryAdminService extends
    AbstractWSO2Carbon4xAdminService<WSO2Connector>
{

    private static final String SERVICES_MEDIATION_LIBRARY_UPLOADER =
        "/services/MediationLibraryUploader";

    private static final String SERVICES_MEDIATION_LIBRARY_ADMIN_SERVICE =
        "/services/MediationLibraryAdminService";

    private MediationLibraryAdminServiceStub serviceStub;

    public WSO2Carbon4xMediationLibraryAdminService(Configuration configuration)
    {
        super(configuration);
    }

    protected MediationLibraryAdminServiceStub getServiceStub() throws IOException
    {
        if (serviceStub == null)
        {
            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub =
                new MediationLibraryAdminServiceStub(new URL(getUrl()
                    + SERVICES_MEDIATION_LIBRARY_ADMIN_SERVICE).toString());
            prepareStub(mediationLibraryAdminServiceStub);

            serviceStub = mediationLibraryAdminServiceStub;
        }
        return serviceStub;
    }

    public void deploy(WSO2Connector deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
            MediationLibraryUploaderStub mediationLibraryUploaderStub =
                new MediationLibraryUploaderStub(new URL(getUrl()
                    + SERVICES_MEDIATION_LIBRARY_UPLOADER).toString());
            prepareStub(mediationLibraryUploaderStub);

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

    public boolean exists(WSO2Connector deployable, boolean handleFaultyAsExistent)
        throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logExists(name);

            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub = getServiceStub();

            String[] libraries = mediationLibraryAdminServiceStub.getAllLibraries();
            if (libraries == null)
            {
                return false;
            }
            for (String library : libraries)
            {
                if (library.equals(name))
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
        logStart(deployable.getApplicationName());
        try
        {
            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub = getServiceStub();

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
        try
        {
            String name = deployable.getApplicationName();

            logStop(name);

            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub = getServiceStub();

            mediationLibraryAdminServiceStub.updateStatus(name, null, null, "disabled");

        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error stopping connector", e);
        }
    }

    public void undeploy(WSO2Connector deployable) throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logRemove(name);

            MediationLibraryAdminServiceStub mediationLibraryAdminServiceStub = getServiceStub();

            mediationLibraryAdminServiceStub.deleteLibrary(name);
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing connector", e);
        }
    }

}
