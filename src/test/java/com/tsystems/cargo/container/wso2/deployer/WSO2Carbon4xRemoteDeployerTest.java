package com.tsystems.cargo.container.wso2.deployer;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.generic.deployable.DeployableFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;
import org.hamcrest.CoreMatchers;
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
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xAxis2ModuleAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xAxis2ServiceAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xBAMToolboxAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xCarbonApplicationAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xMediationLibraryAdminService;
import com.tsystems.cargo.container.wso2.deployer.internal.impl.WSO2Carbon4xWarAdminService;

public class WSO2Carbon4xRemoteDeployerTest
{
    private static final ConfigurationFactory CONFIGURATION_FACTORY =
        new DefaultConfigurationFactory();

    private static final ContainerFactory CONTAINER_FACTORY = new DefaultContainerFactory();

    private static final DeployerFactory DEPLOYER_FACTORY = new DefaultDeployerFactory();

    private static final DeployableFactory DEPLOYABLE_FACTORY = new DefaultDeployableFactory();

    private Configuration configuration;

    private Container container;

    private WSO2Carbon4xRemoteDeployer deployer;

    @Before
    public void setUp() throws Exception
    {
        configuration =
            CONFIGURATION_FACTORY.createConfiguration(WSO2Carbon4xContainer.ID,
                ContainerType.REMOTE, ConfigurationType.RUNTIME);

        container =
            CONTAINER_FACTORY.createContainer(WSO2Carbon4xContainer.ID, ContainerType.REMOTE,
                configuration);

        deployer = (WSO2Carbon4xRemoteDeployer) DEPLOYER_FACTORY.createDeployer(container);
    }

    @Test
    public void testGetWSO2Carbon4xAxis2ModuleAdminService()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.mar",
                Axis2Module.TYPE);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xAxis2ModuleAdminService.class));
    }

    @Test
    public void testGetWSO2Carbon4xAxis2ServiceAdminService()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.aar",
                Axis2Service.TYPE);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xAxis2ServiceAdminService.class));

        deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.aar",
                WSO2Axis2Service.TYPE);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xAxis2ServiceAdminService.class));
    }

    @Test
    public void testGetWSO2Carbon4xCarbonApplicationAdminService()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.car",
                CarbonApplication.TYPE);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xCarbonApplicationAdminService.class));

        deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.car",
                WSO2CarbonApplication.TYPE);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xCarbonApplicationAdminService.class));
    }

    @Test
    public void testGetWSO2Carbon4xWarAdminService()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.war",
                DeployableType.WAR);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xWarAdminService.class));
    }

    @Test
    public void testGetWSO2Carbon4xMediationLibraryAdminService()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.zip",
                WSO2Connector.TYPE);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xMediationLibraryAdminService.class));
    }

    @Test
    public void testGetWSO2Carbon4xBAMToolboxAdminService()
    {
        Deployable deployable =
            DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.tbox",
                BAMToolbox.TYPE);

        Assert.assertThat(deployer.getAdminService(deployable.getClass()),
            CoreMatchers.instanceOf(WSO2Carbon4xBAMToolboxAdminService.class));
    }

}
