package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

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

    private static final String CAR_EXTENSION = ".car";

    public static final DeployableType TYPE = DeployableType.toType("car");

    private String applicationName;

    private String application;

    private String version;

    private boolean ignoreVersion = false;

    public CarbonApplication(final String file)
    {
        super(file);
    }

    public final String getApplicationName()
    {
        initApplicationName();
        return ignoreVersion ? application : applicationName;
    }

    private void initApplicationName()
    {
        parseApplicationName();
        applicationName = application + "_" + version;
    }

    public DeployableType getType()
    {
        return TYPE;
    }

    public boolean getIgnoreVersion()
    {
        return ignoreVersion;
    }

    public boolean matchesApplication(String candidate)
    {
        if (candidate == null || candidate.length() == 0)
        {
            return false;
        }

        initApplicationName();

        if (candidate.toLowerCase(Locale.ENGLISH).endsWith(CAR_EXTENSION))
        {
            return candidate.equals(getFileHandler().getName(getFile()));
        }

        if (ignoreVersion)
        {
            return candidate.startsWith(application);
        }
        else
        {
            return candidate.equals(applicationName);
        }

    }

    private void parseApplicationName()
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
            application = nodeName.getNodeValue();

            XPathExpression xPathVersion =
                xPath.compile("//artifacts/artifact[1][@type='carbon/application']/@version");
            Node nodeVersion = (Node) xPathVersion.evaluate(doc, XPathConstants.NODE);
            version = nodeVersion.getNodeValue();

        }
        catch (Exception e)
        {
            throw new DeployableException("can not parse carbon application name", e);
        }
    }

    public void setApplicationName(String applicationName)
    {
        throw new DeployableException("Deployable applicationName can not be overwritten by user.");
    }

    public void setIgnoreVersion(String ignoreVersion)
    {
        this.ignoreVersion = Boolean.parseBoolean(ignoreVersion);
    }
}
