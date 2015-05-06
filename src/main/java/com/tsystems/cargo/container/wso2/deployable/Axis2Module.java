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
 * A Axis2 application deployable. Matches https://axis.apache.org/axis2/java/core/docs/modules.html
 * packaging type.
 */
public class Axis2Module extends AbstractWSO2Deployable implements WSO2Deployable
{

    public static final DeployableType TYPE = DeployableType.toType("mar");

    private String applicationName;

    public Axis2Module(final String file)
    {
        super(file);
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName()
    {
        if (applicationName == null || applicationName.length() == 0)
        {
            parseApplicationName();
        }

        return applicationName;
    }

    public DeployableType getType()
    {
        return TYPE;
    }

    public final void parseApplicationName()
    {
        try
        {
            JarArchive jarArchive = JarArchiveIo.open(new File(getFile()));
            InputStream in = jarArchive.getResource("META-INF/module.xml");

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            XPathExpression xPathName =
                XPathFactory.newInstance().newXPath().compile("//module/@name");
            Node nodeName = (Node) xPathName.evaluate(doc, XPathConstants.NODE);
            String nameString = nodeName.getNodeValue();

            applicationName = nameString;

        }
        catch (Exception e)
        {
            throw new DeployableException("can not parse module name", e);
        }
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }
}
