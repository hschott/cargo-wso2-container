package com.tsystems.cargo.container.wso2.deployer;

import java.io.File;
import java.net.URL;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.generic.deployable.DeployableFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tsystems.cargo.container.wso2.WSO2Carbon4xContainer;
import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;

public class WSO2Carbon4xInstalledLocalDeployerTest
{

    private static final ContainerFactory CONTAINER_FACTORY = new DefaultContainerFactory();

    private static final ConfigurationFactory CONFIGURATION_FACTORY =
        new DefaultConfigurationFactory();

    private static final DeployerFactory DEPLOYER_FACTORY = new DefaultDeployerFactory();

    private static final DeployableFactory DEPLOYABLE_FACTORY = new DefaultDeployableFactory();

    private static final String DEPLOYABLE_DIR_PREFIX = "/repository/deployment/server";

    private Configuration configuration;

    private Container container;

    private Deployer deployer;

    private String home;

    @Before
    public void setUp() throws Exception
    {
        URL carbon = ClassLoader.getSystemResource("repository/conf/carbon.xml");
        home =
            new File(carbon.getFile()).getParentFile().getParentFile().getParentFile().getPath();

        configuration =
            CONFIGURATION_FACTORY.createConfiguration(WSO2Carbon4xContainer.ID,
                ContainerType.INSTALLED, ConfigurationType.EXISTING, home);

        container =
            CONTAINER_FACTORY.createContainer(WSO2Carbon4xContainer.ID, ContainerType.INSTALLED,
                configuration);

        deployer = DEPLOYER_FACTORY.createDeployer(container);
    }

    @Test
    public void testWarDeployableDir()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.war",
                DeployableType.WAR);

        String dir = ((WSO2Carbon4xInstalledLocalDeployer) deployer).getDeployableDir(deployable);

        Assert.assertEquals(home + DEPLOYABLE_DIR_PREFIX + "/webapps", dir);
    }

    @Test
    public void testCarbonApplicationDeployableDir()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.car",
                CarbonApplication.TYPE);

        String dir = ((WSO2Carbon4xInstalledLocalDeployer) deployer).getDeployableDir(deployable);

        Assert.assertEquals(home + DEPLOYABLE_DIR_PREFIX + "/carbonapps", dir);
    }

    @Test
    public void testWSO2Axis2ServiceDeployableDir()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.aar",
                WSO2Axis2Service.TYPE);

        String dir = ((WSO2Carbon4xInstalledLocalDeployer) deployer).getDeployableDir(deployable);

        Assert.assertEquals(home + DEPLOYABLE_DIR_PREFIX + "/axis2services", dir);
    }

    @Test
    public void testAxis2ServiceDeployableDir()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.aar",
                Axis2Service.TYPE);

        String dir = ((WSO2Carbon4xInstalledLocalDeployer) deployer).getDeployableDir(deployable);

        Assert.assertEquals(home + DEPLOYABLE_DIR_PREFIX + "/axis2services", dir);
    }

    @Test
    public void testAxis2ModuleDeployableDir()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.mar",
                Axis2Module.TYPE);

        String dir = ((WSO2Carbon4xInstalledLocalDeployer) deployer).getDeployableDir(deployable);

        Assert.assertEquals(home + DEPLOYABLE_DIR_PREFIX + "/axis2modules", dir);
    }

    @Test
    public void testWSO2ConnectorDeployableDir()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.zip",
                WSO2Connector.TYPE);

        String dir = ((WSO2Carbon4xInstalledLocalDeployer) deployer).getDeployableDir(deployable);

        Assert.assertEquals(home + DEPLOYABLE_DIR_PREFIX + "/synapse-libs", dir);
    }

    @Test
    public void testBAMToolboxDeployableDir()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.tbox",
                BAMToolbox.TYPE);

        String dir = ((WSO2Carbon4xInstalledLocalDeployer) deployer).getDeployableDir(deployable);

        Assert.assertEquals(home + DEPLOYABLE_DIR_PREFIX + "/bam-toolbox", dir);
    }

    @Test
    public void testShouldDeployExpanded()
    {
        WSO2Carbon4xInstalledLocalDeployer installedLocalDeployer =
            (WSO2Carbon4xInstalledLocalDeployer) deployer;

        Assert
            .assertEquals(true, installedLocalDeployer.shouldDeployExpanded(DeployableType.WAR));
        Assert.assertEquals(false,
            installedLocalDeployer.shouldDeployExpanded(CarbonApplication.TYPE));
        Assert.assertEquals(false,
            installedLocalDeployer.shouldDeployExpanded(WSO2CarbonApplication.TYPE));
        Assert
            .assertEquals(false, installedLocalDeployer.shouldDeployExpanded(Axis2Service.TYPE));
        Assert.assertEquals(false,
            installedLocalDeployer.shouldDeployExpanded(WSO2Axis2Service.TYPE));
        Assert.assertEquals(false, installedLocalDeployer.shouldDeployExpanded(Axis2Module.TYPE));
        Assert.assertEquals(false,
            installedLocalDeployer.shouldDeployExpanded(WSO2Connector.TYPE));
        Assert.assertEquals(false, installedLocalDeployer.shouldDeployExpanded(BAMToolbox.TYPE));
    }

}
