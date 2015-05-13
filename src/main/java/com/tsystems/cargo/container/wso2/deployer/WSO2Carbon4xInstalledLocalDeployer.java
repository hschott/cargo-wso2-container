package com.tsystems.cargo.container.wso2.deployer;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;

public class WSO2Carbon4xInstalledLocalDeployer extends AbstractWSO2InstalledLocalDeployer
{

    public WSO2Carbon4xInstalledLocalDeployer(InstalledLocalContainer container)
    {
        super(container);
        setShouldDeployExpanded(CarbonApplication.TYPE, false);
        setShouldDeployExpanded(WSO2CarbonApplication.TYPE, false);
        setShouldDeployExpanded(WSO2Axis2Service.TYPE, false);
        setShouldDeployExpanded(Axis2Service.TYPE, false);
        setShouldDeployExpanded(Axis2Module.TYPE, false);
        setShouldDeployExpanded(WSO2Connector.TYPE, false);
        setShouldDeployExpanded(BAMToolbox.TYPE, false);
    }

    @Override
    public String getDeployableDir(Deployable deployable)
    {
        String deployableDir = System.getProperty("java.io.tmpdir");
        String home = ((InstalledLocalContainer) getContainer()).getHome();

        if (home == null)
        {
            home = getContainer().getConfiguration().getHome();
        }

        String carbon = getFileHandler().append(home, "repository/conf/carbon.xml");

        Document doc;
        String loc;
        try
        {
            doc =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(carbon));
            XPathExpression xPathExpr =
                XPathFactory.newInstance().newXPath()
                    .compile("//Server/Axis2Config/RepositoryLocation");
            Node node = (Node) xPathExpr.evaluate(doc, XPathConstants.NODE);
            loc = node.getFirstChild().getNodeValue();

            loc = loc.replace("${carbon.home}", home);

            if (!getFileHandler().isDirectory(loc))
                throw new ContainerException("repository location is not a directory: " + loc);

        }
        catch (Exception e)
        {
            getLogger().warn("error obtaining repository: " + e.getMessage(),
                getClass().getSimpleName());
            loc = getFileHandler().append(home, "repository/deployment/server");
        }

        if (CarbonApplication.TYPE.equals(deployable.getType()))
        {
            deployableDir = getFileHandler().append(loc, "carbonapps");
        }
        else if (DeployableType.WAR.equals(deployable.getType()))
        {
            deployableDir = getFileHandler().append(loc, "webapps");
        }
        else if (Axis2Service.TYPE.equals(deployable.getType())
            || WSO2Axis2Service.TYPE.equals(deployable.getType()))
        {
            deployableDir = getFileHandler().append(loc, "axis2services");
        }
        else if (Axis2Module.TYPE.equals(deployable.getType()))
        {
            deployableDir = getFileHandler().append(loc, "axis2modules");
        }
        else if (WSO2Connector.TYPE.equals(deployable.getType()))
        {
            deployableDir = getFileHandler().append(loc, "synapse-libs");
        }
        else if (BAMToolbox.TYPE.equals(deployable.getType()))
        {
            deployableDir = getFileHandler().append(loc, "bam-toolbox");
        }

        if (!getFileHandler().exists(deployableDir))
        {
            getFileHandler().mkdirs(deployableDir);
        }

        return deployableDir;
    }
}
