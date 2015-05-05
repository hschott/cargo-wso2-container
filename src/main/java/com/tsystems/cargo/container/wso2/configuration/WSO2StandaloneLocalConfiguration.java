package com.tsystems.cargo.container.wso2.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipFile;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.configuration.builder.ConfigurationBuilder;
import org.codehaus.cargo.container.configuration.entry.DataSource;
import org.codehaus.cargo.container.configuration.entry.Resource;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.configuration.builder.AbstractStandaloneLocalConfigurationWithXMLConfigurationBuilder;
import org.codehaus.cargo.container.spi.deployer.AbstractLocalDeployer;
import org.codehaus.cargo.util.CargoException;

import com.tsystems.cargo.container.wso2.deployer.WSO2Carbon4xInstalledLocalDeployer;

public class WSO2StandaloneLocalConfiguration extends AbstractStandaloneLocalConfigurationWithXMLConfigurationBuilder {

    public WSO2StandaloneLocalConfiguration(String dir) {
        super(dir);
        // default properties
        this.setProperty(GeneralPropertySet.HOSTNAME, "127.0.0.1");
        this.setProperty(WSO2CarbonPropertySet.CARBON_USERNAME, "admin");
        this.setProperty(WSO2CarbonPropertySet.CARBON_PASSWORD, "admin");
        this.setProperty(GeneralPropertySet.RMI_PORT, "9999");
        this.setProperty(ServletPropertySet.PORT, "9763");
        this.setProperty(WSO2CarbonPropertySet.CARBON_CONTEXT_ROOT, "/");
    }

    @Override
    protected ConfigurationBuilder createConfigurationBuilder(LocalContainer container) {
        return new WSO2ConfigurationBuilder();
    }

    @Override
    protected void doConfigure(LocalContainer c) throws Exception {

        if (!(c instanceof InstalledLocalContainer)) {
            throw new CargoException("Only InstalledLocalContainers are supported, got " + c.getClass().getName());
        }

        InstalledLocalContainer container = (InstalledLocalContainer) c;

        String sourceConf = getFileHandler().append(container.getHome(), "repository/conf");
        String targetConf = getFileHandler().append(getHome(), "repository/conf");
        getFileHandler().mkdirs(targetConf);
        getFileHandler().copyDirectory(sourceConf, targetConf);

        getResourceUtils().copyResource("com/tsystems/cargo/container/wso2/datasources/datasources.xml",
                getFileHandler().append(getHome(), "repository/conf/datasources/cargo-datasources.xml"),
                getFileHandler());

        String configurationXmlFile = "repository/conf/carbon.xml";
        addXmlReplacement(configurationXmlFile, "//Server/Ports/Offset", GeneralPropertySet.PORT_OFFSET);
        addXmlReplacement(configurationXmlFile, "//Server/WebContextRoot", WSO2CarbonPropertySet.CARBON_CONTEXT_ROOT);

        String serverRolesCsv = getPropertyValue(WSO2CarbonPropertySet.CARBON_SERVER_ROLES);

        if (serverRolesCsv != null) {
            for (String serverRole : serverRolesCsv.split(",")) {
                writeConfigurationToXpath(getFileHandler().append(getHome(), configurationXmlFile), "<Role>"
                        + serverRole.trim() + "</Role>", "//carbon:Server/carbon:ServerRoles");
            }
        }

        writeConfigurationToXpath(getFileHandler().append(getHome(), "repository/conf/tomcat/catalina-server.xml"),
                "<GlobalNamingResources/>", "//Server");

        List<String> driverClasses = new ArrayList<String>();

        // Create JARs for modules
        Set<String> classpath = new TreeSet<String>();
        if (container.getSharedClasspath() != null && container.getSharedClasspath().length != 0) {
            for (String classpathElement : container.getSharedClasspath()) {
                classpath.add(classpathElement);
            }
        }

        for (DataSource dataSource : getDataSources()) {
            String driverClass = dataSource.getDriverClass().replace('.', '/') + ".class";

            if (!driverClasses.contains(driverClass)) {

                String driverJar = null;

                for (String classpathElement : classpath) {
                    ZipFile zip = new ZipFile(classpathElement);
                    if (zip.getEntry(driverClass) != null) {
                        driverJar = classpathElement;
                    }
                    zip.close();
                }

                if (driverJar == null) {
                    throw new CargoException("Driver class " + dataSource.getDriverClass()
                            + " not found in the classpath");
                }

                driverClasses.add(driverClass);

                String lib = getFileHandler().append(container.getHome(), "repository/components/lib");
                getFileHandler().copyFile(driverJar, getFileHandler().append(lib, getFileHandler().getName(driverJar)),
                        true);
            }
        }

        AbstractLocalDeployer deployer = new WSO2Carbon4xInstalledLocalDeployer(container);
        deployer.deploy(getDeployables());

    }

    public ConfigurationCapability getCapability() {
        return new WSO2StandaloneLocalConfigurationCapability();
    }

    @Override
    protected Map<String, String> getNamespaces() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("carbon", "http://wso2.org/projects/carbon/carbon.xml");
        return map;
    }

    @Override
    protected String getOrCreateDataSourceConfigurationFile(DataSource ds, LocalContainer container) {
        return getFileHandler().append(getHome(), "repository/conf/datasources/cargo-datasources.xml");
    }

    @Override
    protected String getOrCreateResourceConfigurationFile(Resource resource, LocalContainer container) {
        return getFileHandler().append(getHome(), "repository/conf/tomcat/catalina-server.xml");
    }

    @Override
    protected String getXpathForDataSourcesParent() {
        return "//datasources-configuration/datasources";
    }

    @Override
    protected String getXpathForResourcesParent() {
        return "//Server/GlobalNamingResources";
    }

}
