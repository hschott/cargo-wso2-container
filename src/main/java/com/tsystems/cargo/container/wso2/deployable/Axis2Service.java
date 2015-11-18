package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
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
 * A Axis2 application deployable. Matches
 * https://axis.apache.org/axis2/java/core/docs/xmlbased-server.html packaging type.
 */
public class Axis2Service extends AbstractWSO2Deployable implements WSO2Deployable
{

    public static final DeployableType TYPE = DeployableType.toType("aar");

    private String applicationName;

    public Axis2Service(final String file)
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

    private void parseApplicationName()
    {
        try
        {
            JarArchive jarArchive = JarArchiveIo.open(new File(getFile()));
            InputStream in = jarArchive.getResource("META-INF/services.xml");

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            XPathExpression xPathName =
                XPathFactory.newInstance().newXPath().compile("//service/@name");
            Node nodeName = (Node) xPathName.evaluate(doc, XPathConstants.NODE);
            String nameString = nodeName.getNodeValue();

            applicationName = nameString;

        }
        catch (Exception e)
        {
            throw new DeployableException("can not parse service name", e);
        }
    }

    public DeployableType getType()
    {
        return TYPE;
    }

    public void setApplicationName(String applicationName)
    {
        throw new DeployableException("Deployable applicationName can not be overwritten by user.");
    }
}
