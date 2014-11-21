package com.tsystems.cargo.container.wso2;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.cargo.container.configuration.ExistingLocalConfiguration;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration;
import org.codehaus.cargo.container.spi.jvm.JvmLauncher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class WSO2Carbon4xInstalledLocalContainer extends AbstractWSO2InstalledLocalContainer implements
        WSO2Carbon4xContainer {

    public static final String NAME = "WSO2 Carbon 4.x";

    public WSO2Carbon4xInstalledLocalContainer(LocalConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected void addBootstrapJarToClasspath(JvmLauncher java) {
        String base = getFileHandler().getAbsolutePath(getHome());

        String bin = getFileHandler().append(base, "bin");
        String[] bootstrap = new File(bin).list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("org.wso2.carbon.bootstrap-4.") && name.endsWith(".jar");
            }
        });

        for (String jar : bootstrap) {
            java.addClasspathEntries(getFileHandler().append(bin, jar));
        }
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        LocalConfiguration configuration = getConfiguration();

        String prefix = "";
        if (configuration instanceof ExistingLocalConfiguration) {
            prefix = "Existing";
        }
        if (configuration instanceof StandaloneLocalConfiguration) {
            prefix = "Standalone";
        }

        String carbon = getFileHandler().append(configuration.getHome(), "carbon.xml");

        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(carbon));
            XPathExpression xPathExpr = XPathFactory.newInstance().newXPath().compile("//Server/Name");
            Node node = (Node) xPathExpr.evaluate(doc, XPathConstants.NODE);
            String serverName = node.getFirstChild().getNodeValue();

            return prefix + " " + NAME + " " + serverName;
        } catch (Exception e) {
            return prefix + " " + NAME;
        }
    }
}
