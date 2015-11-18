package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.io.IOException;
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

    private ModuleAdminServiceStub serviceStub;

    public WSO2Carbon4xAxis2ModuleAdminService(Configuration configuration)
    {
        super(configuration);
    }

    protected ModuleAdminServiceStub getServiceStub() throws IOException
    {
        if (serviceStub == null)
        {
            ModuleAdminServiceStub moduleAdminServiceStub =
                new ModuleAdminServiceStub(new URL(getUrl() + SERVICES_MODULE_ADMIN_SERVICE).toString());
            prepareStub(moduleAdminServiceStub);

            serviceStub = moduleAdminServiceStub;
        }
        return serviceStub;
    }

    public void deploy(Axis2Module deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
            ModuleAdminServiceStub moduleAdminServiceStub = getServiceStub();

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

    public boolean exists(Axis2Module deployable, boolean handleFaultyAsExistent)
        throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logExists(name);

            ModuleAdminServiceStub moduleAdminServiceStub = getServiceStub();

            ModuleMetaData[] moduleMetaData = moduleAdminServiceStub.listModules();

            if (moduleMetaData != null)
            {
                for (ModuleMetaData module : moduleMetaData)
                {
                    if (module.getModulename().equals(name))
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
            ModuleAdminServiceStub moduleAdminServiceStub = getServiceStub();

            moduleAdminServiceStub.globallyEngageModule(deployable.getApplicationName());
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error starting axis2 module", e);
        }
    }

    public void stop(Axis2Module deployable) throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logStop(name);

            ModuleAdminServiceStub moduleAdminServiceStub = getServiceStub();

            moduleAdminServiceStub.globallyDisengageModule(name);
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error stopping axis2 module", e);
        }
    }

    public void undeploy(Axis2Module deployable) throws WSO2AdminServicesException
    {
        try
        {
            String name = deployable.getApplicationName();

            logRemove(name);

            ModuleAdminServiceStub moduleAdminServiceStub = getServiceStub();

            moduleAdminServiceStub.removeModule(name);
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing axis2 module", e);
        }
    }

}
