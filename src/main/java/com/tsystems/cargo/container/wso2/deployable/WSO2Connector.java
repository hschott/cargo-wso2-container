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
 * A WSO2 Connector deployable. Matches
 * https://docs.wso2.com/display/ESB481/Creating+a+Connector packaging type.
 */
public class WSO2Connector extends AbstractDeployable {

    public static final DeployableType TYPE = DeployableType.toType("zip");

    private String applicationName;

    public WSO2Connector(final String file) {
        super(file);
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        if (applicationName == null) {
            setApplicationName();
        }

        if (applicationName == null) {
            final String fileName = getFile();
            return fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("."));
        }

        return applicationName;
    }

    public DeployableType getType() {
        return TYPE;
    }

    public final void setApplicationName() {
        Document doc;
        try {
            JarArchive jarArchive = JarArchiveIo.open(new File(getFile()));
            InputStream in = jarArchive.getResource("connector.xml");

            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            XPathExpression xPathPackage = XPathFactory.newInstance().newXPath()
                    .compile("//connector/component/@package");
            Node nodePackage = (Node) xPathPackage.evaluate(doc, XPathConstants.NODE);
            String packageString = nodePackage.getNodeValue();

            XPathExpression xPathName = XPathFactory.newInstance().newXPath().compile("//connector/component/@name");
            Node nodeName = (Node) xPathName.evaluate(doc, XPathConstants.NODE);
            String nameString = nodeName.getNodeValue();

            applicationName = "{" + packageString + "}" + nameString;

        } catch (Exception e) {
            getLogger().warn("can not parse connector library name, fallback to deployable name",
                    getClass().getSimpleName());
        }

    }
}