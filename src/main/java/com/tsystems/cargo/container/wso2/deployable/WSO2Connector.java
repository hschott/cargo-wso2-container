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
 * A WSO2 Connector deployable. Matches https://docs.wso2.com/display/ESB481/Creating+a+Connector
 * packaging type.
 */
public class WSO2Connector extends AbstractWSO2Deployable implements WSO2Deployable
{

    public static final DeployableType TYPE = DeployableType.toType("zip");

    private String packageName;

    private String libName;

    public WSO2Connector(final String file)
    {
        super(file);
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName()
    {
        if (packageName == null || packageName.length() == 0 || libName == null
            || libName.length() == 0)
        {
            parseApplicationName();
        }

        return "{" + packageName + "}" + libName;
    }

    public String getLibName()
    {
        return libName;
    }

    public String getPackageName()
    {
        return packageName;
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
            InputStream in = jarArchive.getResource("connector.xml");

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression xPathPackage = xPath.compile("//connector/component/@package");
            Node nodePackage = (Node) xPathPackage.evaluate(doc, XPathConstants.NODE);
            packageName = nodePackage.getNodeValue();

            XPathExpression xPathName = xPath.compile("//connector/component/@name");
            Node nodeName = (Node) xPathName.evaluate(doc, XPathConstants.NODE);
            libName = nodeName.getNodeValue();

        }
        catch (Exception e)
        {
            throw new DeployableException("can not parse connector library name", e);
        }
    }

    public void setApplicationName(String applicationName)
    {
        throw new DeployableException("Deployable applicationName can not be overwritten by user.");
    }
}
