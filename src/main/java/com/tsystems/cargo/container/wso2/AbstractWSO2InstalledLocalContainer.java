package com.tsystems.cargo.container.wso2;

import java.io.File;
import java.net.URL;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.codehaus.cargo.container.ContainerCapability;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.configuration.ExistingLocalConfiguration;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.URLDeployableMonitor;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;
import org.codehaus.cargo.container.spi.deployer.DeployerWatchdog;
import org.codehaus.cargo.container.spi.jvm.JvmLauncher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.tsystems.cargo.jmx.JSR160MBeanServerConnectionFactory;

public abstract class AbstractWSO2InstalledLocalContainer extends AbstractInstalledLocalContainer
{

    public AbstractWSO2InstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    protected abstract void addBootstrapJarToClasspath(JvmLauncher java);

    @Override
    protected void addMemoryArguments(JvmLauncher java)
    {
        // if the jvmArgs don't already contain memory settings add the default
        String jvmArgs = getConfiguration().getPropertyValue(GeneralPropertySet.JVMARGS);
        if (jvmArgs == null || !jvmArgs.contains("-Xms"))
        {
            java.addJvmArguments("-Xms256m");
        }
        if (jvmArgs == null || !jvmArgs.contains("-Xmx"))
        {
            java.addJvmArguments("-Xmx1024m");
        }
        if (jvmArgs == null || !jvmArgs.contains("-XX:MaxPermSize"))
        {
            java.addJvmArguments("-XX:MaxPermSize=256m");
        }
    }

    @Override
    public void doStart(JvmLauncher java) throws Exception
    {
        invokeContainer("start", java);
    }

    @Override
    public void doStop(JvmLauncher java) throws Exception
    {
        JSR160MBeanServerConnectionFactory mbscf = new JSR160MBeanServerConnectionFactory();
        try
        {
            MBeanServerConnection mbsc = mbscf.getServerConnection(getConfiguration());
            mbsc.invoke(new ObjectName("org.wso2.carbon:type=ServerAdmin"), "shutdownGracefully",
                null, null);
        }
        finally
        {
            mbscf.destroy();
        }
    }

    public ContainerCapability getCapability()
    {
        return new WSO2ContainerCapability();
    }

    public URL getCarbonURL(LocalConfiguration configuration)
    {
        try
        {

            String carbon =
                getFileHandler().append(configuration.getHome(), "repository/conf/carbon.xml");

            Document doc =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(carbon));

            XPathExpression xPathExpr =
                XPathFactory.newInstance().newXPath().compile("//Server/WebContextRoot");
            Node node = (Node) xPathExpr.evaluate(doc, XPathConstants.NODE);
            String context = node.getFirstChild().getNodeValue();

            String hostname = configuration.getPropertyValue(GeneralPropertySet.HOSTNAME);
            if ("0.0.0.0".equals(hostname) || "::0".equals(hostname))
            {
                hostname = "localhost";
            }
            return new URL(configuration.getPropertyValue(GeneralPropertySet.PROTOCOL) + "://"
                + hostname + ":" + configuration.getPropertyValue(ServletPropertySet.PORT)
                + context + "/carbon/");
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to compute Carbon URL", e);
        }
    }

    public abstract String getCommonName();

    public String getName()
    {
        LocalConfiguration configuration = getConfiguration();

        String prefix = "";

        if (configuration instanceof ExistingLocalConfiguration)
        {
            prefix = "Existing ";
        }
        if (configuration instanceof StandaloneLocalConfiguration)
        {
            prefix = "Standalone ";
        }

        String carbon =
            getFileHandler().append(configuration.getHome(), "repository/conf/carbon.xml");
        Document doc;
        try
        {
            doc =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(carbon));
            XPathExpression xPathExpr =
                XPathFactory.newInstance().newXPath().compile("//Server/Name");
            Node node = (Node) xPathExpr.evaluate(doc, XPathConstants.NODE);
            String serverName = node.getFirstChild().getNodeValue();

            return prefix + getCommonName() + " " + serverName;
        }
        catch (Exception e)
        {
            return prefix + getCommonName();
        }
    }

    protected void invokeContainer(String action, JvmLauncher java) throws Exception
    {
        String containerHome = getFileHandler().getAbsolutePath(getHome());
        String configurationHome =
            getFileHandler().getAbsolutePath(
                getFileHandler().append(getConfiguration().getHome(), "repository/conf"));

        java.setSystemProperty("carbon.home", containerHome);
        java.setSystemProperty("catalina.base",
            getFileHandler().append(containerHome, "lib/tomcat"));
        java.setSystemProperty("wso2.server.standalone", "true");

        java.setSystemProperty("java.endorsed.dirs",
            getFileHandler().append(containerHome, "lib/endorsed"));
        java.setSystemProperty("components.repo",
            getFileHandler().append(containerHome, "repository/components/plugins"));
        java.setSystemProperty("carbon.config.dir.path", configurationHome);
        java.setSystemProperty("conf.location", configurationHome);
        java.setSystemProperty("carbon.registry.root", "/");
        java.setSystemProperty("com.atomikos.icatch.file",
            getFileHandler().append(containerHome, "lib/transactions.properties"));
        java.setSystemProperty("com.atomikos.icatch.hide_init_file_path", "true");
        java.setSystemProperty("org.terracotta.quartz.skipUpdateCheck", "true");
        java.setSystemProperty("file.encoding", "UTF8");

        java.setSystemProperty("com.sun.management.jmxremote", null);

        String jvmArgs = getConfiguration().getPropertyValue(GeneralPropertySet.JVMARGS);

        if (jvmArgs == null || !jvmArgs.contains("wso2.carbon.xml"))
        {
            java.setSystemProperty("wso2.carbon.xml",
                getFileHandler().append(configurationHome, "carbon.xml"));
        }
        if (jvmArgs == null || !jvmArgs.contains("wso2.registry.xml"))
        {
            java.setSystemProperty("wso2.registry.xml",
                getFileHandler().append(configurationHome, "registry.xml"));
        }
        if (jvmArgs == null || !jvmArgs.contains("wso2.user.mgt.xml"))
        {
            java.setSystemProperty("wso2.user.mgt.xml",
                getFileHandler().append(configurationHome, "user-mgt.xml"));
        }
        if (jvmArgs == null || !jvmArgs.contains("wso2.transports.xml"))
        {
            java.setSystemProperty("wso2.transports.xml",
                getFileHandler().append(configurationHome, "mgt-transports.xml"));
        }
        if (jvmArgs == null || !jvmArgs.contains("java.io.tmpdir"))
        {
            java.setSystemProperty("java.io.tmpdir", getFileHandler()
                .append(containerHome, "tmp"));
        }
        if (jvmArgs == null || !jvmArgs.contains("java.util.logging.config.file"))
        {
            java.setSystemProperty("java.util.logging.config.file",
                getFileHandler().append(configurationHome, "etc/logging-bridge.properties"));
        }

        if (jvmArgs == null || !jvmArgs.contains("-Xbootclasspath/a:"))
        {

            String xboot = getFileHandler().append(containerHome, "repository/lib/xboot");
            if (getFileHandler().isDirectory(xboot))
            {
                String[] bootClasspathElements = getFileHandler().getChildren(xboot);

                StringBuffer bootClasspath = new StringBuffer();
                for (String element : bootClasspathElements)
                {
                    if (element.endsWith(".jar"))
                    {
                        bootClasspath.append(element).append(':');
                    }
                }
                java.addJvmArguments("-Xbootclasspath/a:" + bootClasspath);
            }
        }

        addBootstrapJarToClasspath(java);
        addToolsJarToClasspath(java);
        java.setWorkingDirectory(new File(getFileHandler().getAbsolutePath(getHome())));
        java.setMainClass("org.wso2.carbon.bootstrap.Bootstrap");
        java.addAppArguments(action);
        java.start();
    }

    @Override
    protected void waitForCompletion(boolean waitForStarting) throws InterruptedException
    {
        LocalConfiguration config = getConfiguration();

        if (waitForStarting)
        {
            DeployableMonitor monitor =
                new URLDeployableMonitor(getCarbonURL(config), getTimeout());
            monitor.setLogger(getLogger());
            DeployerWatchdog watchdog = new DeployerWatchdog(monitor);
            watchdog.setLogger(getLogger());

            watchdog.watch(waitForStarting);
        }
        else
        {
            super.waitForCompletion(waitForStarting);
        }
    }

}
