package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.JarArchiveIo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A Axis2 application deployable. Matches
 * https://axis.apache.org/axis2/java/core/docs/modules.html packaging type.
 */
public class Axis2Module extends AbstractDeployable implements WSO2Deployable {

    public static final DeployableType TYPE = DeployableType.toType("mar");

    private String applicationName;

    private long deployTimeout = -1;

    public Axis2Module(final String file) {
        super(file);
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        if (applicationName == null || applicationName.length() == 0) {
            parseApplicationName();
        }

        return applicationName;
    }

    public long getDeployTimeout() {
        return deployTimeout;
    }

    public DeployableType getType() {
        return TYPE;
    }

    public final void parseApplicationName() {
        Document doc;
        try {
            JarArchive jarArchive = JarArchiveIo.open(new File(getFile()));
            InputStream in = jarArchive.getResource("META-INF/module.xml");

            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            XPathExpression xPathName = XPathFactory.newInstance().newXPath().compile("//module/@name");
            Node nodeName = (Node) xPathName.evaluate(doc, XPathConstants.NODE);
            String nameString = nodeName.getNodeValue();

            applicationName = nameString;

        } catch (Exception e) {
            getLogger().warn("can not parse module name, fallback to deployable name", getClass().getSimpleName());
            final String fileName = getFile();
            applicationName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("."));
        }

    }

    public void setApplicationName(String applicationName) {
        getLogger().warn("Deployable applicationName overwritten by user", getClass().getSimpleName());
        this.applicationName = applicationName;
    }

    public void setDeployTimeout(long deployTimeout) {
        this.deployTimeout = deployTimeout;
    }
}