package com.tsystems.cargo.container.wso2.deployable;

import java.io.File;
import java.net.URL;
import java.util.UUID;

import org.codehaus.cargo.container.deployable.DeployableException;
import org.codehaus.cargo.container.deployable.DeployableType;
import org.codehaus.cargo.generic.deployable.DefaultDeployableFactory;
import org.codehaus.cargo.generic.deployable.DeployableFactory;
import org.junit.Assert;
import org.junit.Test;

import com.tsystems.cargo.container.wso2.WSO2Carbon4xContainer;

public class WSO2DeployableTest
{

    private static final DeployableFactory DEPLOYABLE_FACTORY = new DefaultDeployableFactory();

    @Test
    public void testWarApplicationName()
    {
        WSO2WAR deployable;

        deployable =
            (WSO2WAR) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.war",
                DeployableType.WAR);

        Assert.assertEquals("test.war", deployable.getApplicationName());

        deployable.setContext("berlin");
        Assert.assertEquals("berlin.war", deployable.getApplicationName());

        deployable.setVersion("4.1-SNAPSHOT");
        Assert.assertEquals("berlin#4.1-SNAPSHOT.war", deployable.getApplicationName());

        // expanded
        String name = UUID.randomUUID().toString();
        File file = new File(new File(System.getProperty("java.io.tmpdir")), name);
        file.deleteOnExit();
        file.mkdir();
        deployable =
            (WSO2WAR) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                file.getPath(), DeployableType.WAR);
        Assert.assertEquals(name, deployable.getApplicationName());

        deployable.setContext("berlin");
        Assert.assertEquals("berlin", deployable.getApplicationName());

        deployable.setVersion("4.1-SNAPSHOT");
        Assert.assertEquals("berlin#4.1-SNAPSHOT", deployable.getApplicationName());
    }

    @Test(expected = DeployableException.class)
    public void testWarSetApplicationName()
    {
        WSO2WAR deployable;

        deployable =
            (WSO2WAR) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID, "test.war",
                DeployableType.WAR);

        deployable.setApplicationName("delhi");
    }

    @Test
    public void testCarbonApplicationName()
    {
        CarbonApplication deployable;

        URL car = ClassLoader.getSystemResource("capp-1.0.0.car");

        deployable =
            (CarbonApplication) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                car.getFile(), CarbonApplication.TYPE);

        deployable.setIgnoreVersion("true");
        Assert.assertTrue(deployable.matchesApplication("Capp_1.0.1"));
        Assert.assertTrue(deployable.matchesApplication("Capp_1.0.0"));

        deployable.setIgnoreVersion("false");
        Assert.assertFalse(deployable.matchesApplication("Capp_1.0.1"));
        Assert.assertTrue(deployable.matchesApplication("Capp_1.0.0"));
    }

    @Test
    public void testAxis2ModuleApplicationName()
    {
        Axis2Module deployable;

        URL mar = ClassLoader.getSystemResource("rampart-1.6.1-wso2v10.mar");

        deployable =
            (Axis2Module) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                mar.getFile(), Axis2Module.TYPE);

        Assert.assertEquals("rampart", deployable.getApplicationName());
    }

    @Test
    public void testAxis2ServiceApplicationName()
    {
        Axis2Service deployable;

        URL aar = ClassLoader.getSystemResource("Version.aar");

        deployable =
            (Axis2Service) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                aar.getFile(), Axis2Service.TYPE);

        Assert.assertEquals("Version", deployable.getApplicationName());
    }

    @Test
    public void testWSO2Axis2ServiceApplicationName()
    {
        WSO2Axis2Service deployable;

        URL aar = ClassLoader.getSystemResource("Version.aar");

        deployable =
            (WSO2Axis2Service) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                aar.getFile(), WSO2Axis2Service.TYPE);

        Assert.assertEquals("Version", deployable.getApplicationName());

    }

    @Test
    public void testBAMToolboxApplicationName()
    {
        BAMToolbox deployable;

        deployable =
            (BAMToolbox) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                "berlin.tbox", BAMToolbox.TYPE);

        Assert.assertEquals("berlin", deployable.getApplicationName());

        deployable.setApplicationName("paris");
        Assert.assertEquals("paris", deployable.getApplicationName());
    }

    @Test
    public void testWSO2ConnectorApplicationName()
    {
        WSO2Connector deployable;

        URL zip = ClassLoader.getSystemResource("twitter-connector-1.0.0.zip");

        deployable =
            (WSO2Connector) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                zip.getFile(), WSO2Connector.TYPE);

        Assert.assertEquals("{org.wso2.carbon.connectors}twitter",
            deployable.getApplicationName());

    }

    @Test(expected = DeployableException.class)
    public void testWSO2ConnectorSetApplicationName()
    {
        WSO2Connector deployable;

        URL zip = ClassLoader.getSystemResource("twitter-connector-1.0.0.zip");

        deployable =
            (WSO2Connector) DEPLOYABLE_FACTORY.createDeployable(WSO2Carbon4xContainer.ID,
                zip.getFile(), WSO2Connector.TYPE);

        deployable.setApplicationName("newyork");
    }

}
