package com.tsystems.cargo.container.wso2;

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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.tsystems.cargo.container.wso2.configuration.WSO2ExistingLocalConfiguration;
import com.tsystems.cargo.container.wso2.configuration.WSO2RuntimeConfiguration;
import com.tsystems.cargo.container.wso2.configuration.WSO2StandaloneLocalConfiguration;
import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;
import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;
import com.tsystems.cargo.container.wso2.deployer.WSO2Carbon4xInstalledLocalDeployer;
import com.tsystems.cargo.container.wso2.deployer.WSO2Carbon4xRemoteDeployer;

public class WSO2FactoryRegistryTest {

    private static final ContainerFactory CONTAINER_FACTORY = new DefaultContainerFactory();
    private static final ConfigurationFactory CONFIGURATION_FACTORY = new DefaultConfigurationFactory();
    private static final DeployerFactory DEPLOYER_FACTORY = new DefaultDeployerFactory();
    private static final DeployableFactory DEPLOYABLE_FACTORY = new DefaultDeployableFactory();

    @Test
    public void testRemoteConfigurationInstance() {
	Configuration configuration = CONFIGURATION_FACTORY
		.createConfiguration(WSO2Carbon4xContainer.ID,
			ContainerType.REMOTE, ConfigurationType.RUNTIME);

	Assert.assertThat(configuration,
		CoreMatchers.instanceOf(WSO2RuntimeConfiguration.class));

	Container container = CONTAINER_FACTORY.createContainer(
		WSO2Carbon4xContainer.ID, ContainerType.REMOTE, configuration);

	Assert.assertThat(container,
		CoreMatchers.instanceOf(WSO2Carbon4xRemoteContainer.class));

	Deployer deployer = DEPLOYER_FACTORY.createDeployer(container);

	Assert.assertThat(deployer,
		CoreMatchers.instanceOf(WSO2Carbon4xRemoteDeployer.class));
    }

    @Test
    public void testExistingConfigurationInstance() {
	Configuration configuration = CONFIGURATION_FACTORY
		.createConfiguration(WSO2Carbon4xContainer.ID,
			ContainerType.INSTALLED, ConfigurationType.EXISTING,
			System.getProperty("java.io.tmpdir"));

	Assert.assertThat(configuration,
		CoreMatchers.instanceOf(WSO2ExistingLocalConfiguration.class));

	Container container = CONTAINER_FACTORY.createContainer(
		WSO2Carbon4xContainer.ID, ContainerType.INSTALLED,
		configuration);

	Assert.assertThat(container, CoreMatchers
		.instanceOf(WSO2Carbon4xInstalledLocalContainer.class));

	Deployer deployer = DEPLOYER_FACTORY.createDeployer(container);

	Assert.assertThat(deployer, CoreMatchers
		.instanceOf(WSO2Carbon4xInstalledLocalDeployer.class));
    }

    @Test
    public void testStandaloneConfigurationInstance() {
	Configuration configuration = CONFIGURATION_FACTORY
		.createConfiguration(WSO2Carbon4xContainer.ID,
			ContainerType.INSTALLED, ConfigurationType.STANDALONE,
			System.getProperty("java.io.tmpdir"));

	Assert.assertThat(configuration,
		CoreMatchers.instanceOf(WSO2StandaloneLocalConfiguration.class));

	Container container = CONTAINER_FACTORY.createContainer(
		WSO2Carbon4xContainer.ID, ContainerType.INSTALLED,
		configuration);

	Assert.assertThat(container, CoreMatchers
		.instanceOf(WSO2Carbon4xInstalledLocalContainer.class));

	Deployer deployer = DEPLOYER_FACTORY.createDeployer(container);

	Assert.assertThat(deployer, CoreMatchers
		.instanceOf(WSO2Carbon4xInstalledLocalDeployer.class));
    }

    @Test
    public void testDeployableInstances() {
	Deployable deployable;
	
	deployable = DEPLOYABLE_FACTORY.createDeployable(
		WSO2Carbon4xContainer.ID, "test.war", DeployableType.WAR);
	Assert.assertThat(deployable, CoreMatchers.instanceOf(WSO2WAR.class));

	deployable = DEPLOYABLE_FACTORY.createDeployable(
		WSO2Carbon4xContainer.ID, "test.car", CarbonApplication.TYPE);
	Assert.assertThat(deployable, CoreMatchers.instanceOf(CarbonApplication.class));

	deployable = DEPLOYABLE_FACTORY.createDeployable(
		WSO2Carbon4xContainer.ID, "test.mar", Axis2Module.TYPE);
	Assert.assertThat(deployable, CoreMatchers.instanceOf(Axis2Module.class));
	
	deployable = DEPLOYABLE_FACTORY.createDeployable(
		WSO2Carbon4xContainer.ID, "test.aar", Axis2Service.TYPE);
	Assert.assertThat(deployable, CoreMatchers.instanceOf(Axis2Service.class));
	
	deployable = DEPLOYABLE_FACTORY.createDeployable(
		WSO2Carbon4xContainer.ID, "test.aar", WSO2Axis2Service.TYPE);
	Assert.assertThat(deployable, CoreMatchers.instanceOf(WSO2Axis2Service.class));
	
	deployable = DEPLOYABLE_FACTORY.createDeployable(
		WSO2Carbon4xContainer.ID, "test.aar", WSO2Connector.TYPE);
	Assert.assertThat(deployable, CoreMatchers.instanceOf(WSO2Connector.class));
	
	deployable = DEPLOYABLE_FACTORY.createDeployable(
		WSO2Carbon4xContainer.ID, "test.aar", BAMToolbox.TYPE);
	Assert.assertThat(deployable, CoreMatchers.instanceOf(BAMToolbox.class));
	
    }

}
