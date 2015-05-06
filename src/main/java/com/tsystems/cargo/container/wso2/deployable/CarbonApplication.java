package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.cargo.container.deployable.DeployableException;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.JarArchiveIo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A WSO2 application deployable. Matches
 * https://docs.wso2.com/display/Carbon420/C-App+Deployment+Process packaging type.
 */
public class CarbonApplication extends AbstractWSO2Deployable implements WSO2Deployable
{

    public static final DeployableType TYPE = DeployableType.toType("carbon/application");

    private String applicationName;

    public CarbonApplication(final String file)
    {
        super(file);
    }

    public final String getApplicationName()
    {
        if (applicationName == null || applicationName.length() == 0)
        {
            parseApplicationName();
        }
        return applicationName;
    }

    public final void parseApplicationName()
    {
        try
        {
            JarArchive jarArchive = JarArchiveIo.open(new File(getFile()));
            InputStream in = jarArchive.getResource("artifacts.xml");

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            XPath xPath = XPathFactory.newInstance().newXPath();

            XPathExpression xPathName =
                xPath.compile("//artifacts/artifact[1][@type='carbon/application']/@name");
            Node nodeName = (Node) xPathName.evaluate(doc, XPathConstants.NODE);
            String nameString = nodeName.getNodeValue();

            XPathExpression xPathVersion =
                xPath.compile("//artifacts/artifact[1][@type='carbon/application']/@version");
            Node nodeVersion = (Node) xPathVersion.evaluate(doc, XPathConstants.NODE);
            String versionString = nodeVersion.getNodeValue();

            applicationName = nameString + "_" + versionString;

        }
        catch (Exception e)
        {
            throw new DeployableException("can not parse carbon application name", e);
        }
    }

    public DeployableType getType()
    {
        return TYPE;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }
}
