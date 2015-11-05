package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.File;
import java.net.URL;

import javax.activation.DataHandler;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.bam.toolbox.deployer.service.BAMToolboxDepolyerServiceStub;
import org.wso2.carbon.bam.toolbox.deployer.util.xsd.ToolBoxStatusDTO;

import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xBAMToolboxAdminService extends
    AbstractWSO2Carbon4xAdminService<BAMToolbox>
{

    private static final String SERVICES_BAM_TOOLBOX_DEPOLYER_SERVICE =
        "/services/BAMToolboxDepolyerService";

    public WSO2Carbon4xBAMToolboxAdminService(Configuration configuration)
    {
        super(configuration);
    }

    public void deploy(BAMToolbox deployable) throws WSO2AdminServicesException
    {
        logUpload(deployable.getFile());
        try
        {
            authenticate();
            BAMToolboxDepolyerServiceStub bamToolboxDepolyerServiceStub =
                new BAMToolboxDepolyerServiceStub(new URL(getUrl()
                    + SERVICES_BAM_TOOLBOX_DEPOLYER_SERVICE).toString());
            prepareStub(bamToolboxDepolyerServiceStub);

            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            bamToolboxDepolyerServiceStub.uploadBAMToolBox(dh,
                new File(deployable.getFile()).getName());
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error uploading bam toolbox", e);
        }
    }

    public boolean exists(BAMToolbox deployable) throws WSO2AdminServicesException
    {
        logExists(deployable.getApplicationName());
        try
        {
            authenticate();
            BAMToolboxDepolyerServiceStub bamToolboxDepolyerServiceStub =
                new BAMToolboxDepolyerServiceStub(new URL(getUrl()
                    + SERVICES_BAM_TOOLBOX_DEPOLYER_SERVICE).toString());
            prepareStub(bamToolboxDepolyerServiceStub);

            ToolBoxStatusDTO toolBoxStatusDTO =
                bamToolboxDepolyerServiceStub.getDeployedToolBoxes("1", "");
            String[] deployedTools = toolBoxStatusDTO.getDeployedTools();
            if (null != deployedTools)
            {
                for (String deployedTool : deployedTools)
                {
                    if (deployedTool.equals(deployable.getApplicationName()))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error checking bam toolbox", e);
        }
        return false;
    }

    public void undeploy(BAMToolbox deployable) throws WSO2AdminServicesException
    {
        logRemove(deployable.getApplicationName());
        try
        {
            authenticate();
            BAMToolboxDepolyerServiceStub bamToolboxDepolyerServiceStub =
                new BAMToolboxDepolyerServiceStub(new URL(getUrl()
                    + SERVICES_BAM_TOOLBOX_DEPOLYER_SERVICE).toString());
            prepareStub(bamToolboxDepolyerServiceStub);

            bamToolboxDepolyerServiceStub.undeployToolBox(new String[] {deployable
                .getApplicationName()});
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("error removing bam toolbox", e);
        }
    }

    public void start(BAMToolbox deployable) throws WSO2AdminServicesException
    {
        throw new WSO2AdminServicesException("Not supported");
    }

    public void stop(BAMToolbox deployable) throws WSO2AdminServicesException
    {
        throw new WSO2AdminServicesException("Not supported");
    }

}
