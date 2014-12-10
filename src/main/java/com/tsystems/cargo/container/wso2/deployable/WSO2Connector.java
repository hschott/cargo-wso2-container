package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.cargo.container.deployable.DeployableException;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.JarArchiveIo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A WSO2 Connector deployable. Matches
 * https://docs.wso2.com/display/ESB481/Creating+a+Connector packaging type.
 */
public class WSO2Connector extends AbstractDeployable implements WSO2Deployable {

    public static final DeployableType TYPE = DeployableType.toType("zip");

    private String packageName;
    private String libName;

    private long deployTimeout = -1;

    public WSO2Connector(final String file) {
        super(file);
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        if (packageName == null || packageName.length() == 0 || libName == null || libName.length() == 0) {
            parseApplicationName();
        }

        return "{" + packageName + "}" + libName;
    }

    public long getDeployTimeout() {
        return deployTimeout;
    }

    public String getLibName() {
        return libName;
    }

    public String getPackageName() {
        return packageName;
    }

    public DeployableType getType() {
        return TYPE;
    }

    public final void parseApplicationName() {
        Document doc;
        try {
            JarArchive jarArchive = JarArchiveIo.open(new File(getFile()));
            InputStream in = jarArchive.getResource("connector.xml");

            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            XPathExpression xPathPackage = XPathFactory.newInstance().newXPath()
                    .compile("//connector/component/@package");
            Node nodePackage = (Node) xPathPackage.evaluate(doc, XPathConstants.NODE);
            packageName = nodePackage.getNodeValue();

            XPathExpression xPathName = XPathFactory.newInstance().newXPath().compile("//connector/component/@name");
            Node nodeName = (Node) xPathName.evaluate(doc, XPathConstants.NODE);
            libName = nodeName.getNodeValue();

        } catch (Exception e) {
            throw new DeployableException("can not parse connector library name", e);
        }
    }

    public void setApplicationName(String applicationName) {
        getLogger().warn("Deployable applicationName can not be overwritten by user", getClass().getSimpleName());
    }

    public void setDeployTimeout(long deployTimeout) {
        this.deployTimeout = deployTimeout;
    }
}