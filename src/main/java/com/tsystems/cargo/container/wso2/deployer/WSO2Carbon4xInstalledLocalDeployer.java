package com.tsystems.cargo.container.wso2.deployer;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.BAMToolbox;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.WSO2Connector;

public class WSO2Carbon4xInstalledLocalDeployer extends AbstractWSO2InstalledLocalDeployer {

    public WSO2Carbon4xInstalledLocalDeployer(InstalledLocalContainer container) {
        super(container);
        setShouldDeployExpanded(CarbonApplication.TYPE, false);
        setShouldDeployExpanded(WSO2Axis2Service.TYPE, false);
        setShouldDeployExpanded(Axis2Service.TYPE, false);
        setShouldDeployExpanded(Axis2Module.TYPE, false);
        setShouldDeployExpanded(WSO2Connector.TYPE, false);
        setShouldDeployExpanded(BAMToolbox.TYPE, false);
    }

    @Override
    public String getDeployableDir(Deployable deployable) {
        String deployableDir = System.getProperty("java.io.tmpdir");
        String home = ((InstalledLocalContainer) getContainer()).getConfiguration().getHome();

        if (CarbonApplication.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(home, "repository/deployment/server/carbonapps");
        } else if (DeployableType.WAR.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(home, "repository/deployment/server/webapps");
        } else if (Axis2Service.TYPE.equals(deployable.getType()) || WSO2Axis2Service.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(home, "repository/deployment/server/axis2services");
        } else if (Axis2Module.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(home, "repository/deployment/server/axis2modules");
        } else if (WSO2Connector.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(home, "repository/deployment/server/synapse-libs");
        } else if (BAMToolbox.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(home, "repository/deployment/server/bam-toolbox");
        }

        if (!getFileHandler().exists(deployableDir)) {
            getFileHandler().mkdirs(deployableDir);
        }

        return deployableDir;
    }
}
