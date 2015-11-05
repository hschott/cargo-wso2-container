package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.module.mgt.ModuleAdminServiceStub;
import org.wso2.carbon.module.mgt.xsd.ModuleMetaData;
import org.wso2.carbon.module.mgt.xsd.ModuleUploadData;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xAxis2ModuleAdminService extends
    AbstractWSO2Carbon4xAdminService<Axis2Module>
{
    private static final String SERVICES_MODULE_ADMIN_SERVICE = "/services/ModuleAdminService";

    public WSO2Carbon4xAxis2ModuleAdminService(Configuration configuration)
    {
        super(configuration);
    }

    public void deploy(Axis2Module deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
            authenticate();
            ModuleAdminServiceStub moduleAdminServiceStub =
                new ModuleAdminServiceStub(new URL(getUrl() + SERVICES_MODULE_ADMIN_SERVICE).toString());
            prepareStub(moduleAdminServiceStub);

            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            ModuleUploadData moduleUploadData = new ModuleUploadData();
            moduleUploadData.setFileName(new File(deployable.getFile()).getName());
            moduleUploadData.setDataHandler(dh);
            moduleAdminServiceStub.uploadModule(new ModuleUploadData[] {moduleUploadData});
            getLogger().warn("WSO2 Carbon server restart required to get Axis2 module effective",
                getClass().getSimpleName());
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error uploading axis2 module", e);
        }
    }

    public boolean exists(Axis2Module deployable) throws WSO2AdminServicesException
    {
        logExists(deployable.getApplicationName());
        try
        {
            authenticate();
            ModuleAdminServiceStub moduleAdminServiceStub =
                new ModuleAdminServiceStub(new URL(getUrl() + SERVICES_MODULE_ADMIN_SERVICE).toString());
            prepareStub(moduleAdminServiceStub);

            ModuleMetaData[] moduleMetaData = moduleAdminServiceStub.listModules();

            if (moduleMetaData != null)
            {
                for (ModuleMetaData module : moduleMetaData)
                {
                    if (module.getModulename().equals(deployable.getApplicationName()))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error checking axis2 module", e);
        }
        return false;
    }

    public void start(Axis2Module deployable) throws WSO2AdminServicesException
    {
        logStart(deployable.getApplicationName());
        try
        {
            authenticate();
            ModuleAdminServiceStub moduleAdminServiceStub =
                new ModuleAdminServiceStub(new URL(getUrl() + SERVICES_MODULE_ADMIN_SERVICE).toString());
            prepareStub(moduleAdminServiceStub);

            moduleAdminServiceStub.globallyEngageModule(deployable.getApplicationName());
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error starting axis2 module", e);
        }
    }

    public void stop(Axis2Module deployable) throws WSO2AdminServicesException
    {
        logStop(deployable.getApplicationName());
        try
        {
            authenticate();
            ModuleAdminServiceStub moduleAdminServiceStub =
                new ModuleAdminServiceStub(new URL(getUrl() + SERVICES_MODULE_ADMIN_SERVICE).toString());
            prepareStub(moduleAdminServiceStub);

            moduleAdminServiceStub.globallyDisengageModule(deployable.getApplicationName());
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error stopping axis2 module", e);
        }
    }

    public void undeploy(Axis2Module deployable) throws WSO2AdminServicesException
    {
        logRemove(deployable.getApplicationName());
        try
        {
            authenticate();
            ModuleAdminServiceStub moduleAdminServiceStub =
                new ModuleAdminServiceStub(new URL(getUrl() + SERVICES_MODULE_ADMIN_SERVICE).toString());
            prepareStub(moduleAdminServiceStub);

            moduleAdminServiceStub.removeModule(deployable.getApplicationName());
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing axis2 module", e);
        }
    }

}
