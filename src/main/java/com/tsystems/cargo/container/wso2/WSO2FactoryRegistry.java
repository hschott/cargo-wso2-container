package com.tsystems.cargo.container.wso2;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.container.deployer.DeployerType;
import org.codehaus.cargo.generic.AbstractFactoryRegistry;
import org.codehaus.cargo.generic.ContainerCapabilityFactory;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationCapabilityFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.deployable.DeployableFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;
import org.codehaus.cargo.generic.packager.PackagerFactory;

import com.tsystems.cargo.container.wso2.configuration.WSO2ExistingLocalConfiguration;
import com.tsystems.cargo.container.wso2.configuration.WSO2ExistingLocalConfigurationCapability;
import com.tsystems.cargo.container.wso2.configuration.WSO2RuntimeConfiguration;
import com.tsystems.cargo.container.wso2.configuration.WSO2RuntimeConfigurationCapability;
import com.tsystems.cargo.container.wso2.configuration.WSO2StandaloneLocalConfiguration;
import com.tsystems.cargo.container.wso2.configuration.WSO2StandaloneLocalConfigurationCapability;
import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;
import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;
import com.tsystems.cargo.container.wso2.deployer.WSO2Carbon4xInstalledLocalDeployer;
import com.tsystems.cargo.container.wso2.deployer.WSO2Carbon4xRemoteDeployer;

/**
 * Registers WSO2 support into default factories.
 */
public class WSO2FactoryRegistry extends AbstractFactoryRegistry
{

    /**
     * Register configuration capabilities.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ConfigurationCapabilityFactory factory)
    {
        factory.registerConfigurationCapability(WSO2Carbon4xContainer.ID, ContainerType.REMOTE,
            ConfigurationType.RUNTIME, WSO2RuntimeConfigurationCapability.class);
        factory.registerConfigurationCapability(WSO2Carbon4xContainer.ID,
            ContainerType.INSTALLED, ConfigurationType.EXISTING,
            WSO2ExistingLocalConfigurationCapability.class);
        factory.registerConfigurationCapability(WSO2Carbon4xContainer.ID,
            ContainerType.INSTALLED, ConfigurationType.STANDALONE,
            WSO2StandaloneLocalConfigurationCapability.class);
    }

    /**
     * Register configuration factories.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ConfigurationFactory factory)
    {
        factory.registerConfiguration(WSO2Carbon4xContainer.ID, ContainerType.REMOTE,
            ConfigurationType.RUNTIME, WSO2RuntimeConfiguration.class);
        factory.registerConfiguration(WSO2Carbon4xContainer.ID, ContainerType.INSTALLED,
            ConfigurationType.EXISTING, WSO2ExistingLocalConfiguration.class);
        factory.registerConfiguration(WSO2Carbon4xContainer.ID, ContainerType.INSTALLED,
            ConfigurationType.STANDALONE, WSO2StandaloneLocalConfiguration.class);
    }

    /**
     * Register container capabilities.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ContainerCapabilityFactory factory)
    {
        factory.registerContainerCapability(WSO2Carbon4xContainer.ID,
            WSO2ContainerCapability.class);
    }

    /**
     * Register container.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final ContainerFactory factory)
    {
        factory.registerContainer(WSO2Carbon4xContainer.ID, ContainerType.REMOTE,
            WSO2Carbon4xRemoteContainer.class);
        factory.registerContainer(WSO2Carbon4xContainer.ID, ContainerType.INSTALLED,
            WSO2Carbon4xInstalledLocalContainer.class);
    }

    /**
     * Register deployable factory.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final DeployableFactory factory)
    {
        factory.registerDeployable(WSO2Carbon4xContainer.ID, CarbonApplication.TYPE,
            CarbonApplication.class);
        factory.registerDeployable(WSO2Carbon4xContainer.ID, WSO2CarbonApplication.TYPE,
            WSO2CarbonApplication.class);
        factory.registerDeployable(WSO2Carbon4xContainer.ID, DeployableType.WAR, WSO2WAR.class);
        factory.registerDeployable(WSO2Carbon4xContainer.ID, Axis2Module.TYPE, Axis2Module.class);
        factory.registerDeployable(WSO2Carbon4xContainer.ID, Axis2Service.TYPE,
            Axis2Service.class);
        factory.registerDeployable(WSO2Carbon4xContainer.ID, WSO2Axis2Service.TYPE,
            WSO2Axis2Service.class);
        factory.registerDeployable(WSO2Carbon4xContainer.ID, WSO2Connector.TYPE,
            WSO2Connector.class);
        factory.registerDeployable(WSO2Carbon4xContainer.ID, BAMToolbox.TYPE, BAMToolbox.class);
    }

    /**
     * Register deployer.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final DeployerFactory factory)
    {
        factory.registerDeployer(WSO2Carbon4xContainer.ID, DeployerType.REMOTE,
            WSO2Carbon4xRemoteDeployer.class);
        factory.registerDeployer(WSO2Carbon4xContainer.ID, DeployerType.INSTALLED,
            WSO2Carbon4xInstalledLocalDeployer.class);
    }

    /**
     * Register packager. Doesn't register anything.
     *
     * @param factory Factory on which to register.
     */
    @Override
    protected void register(final PackagerFactory factory)
    {
        //
    }

}
