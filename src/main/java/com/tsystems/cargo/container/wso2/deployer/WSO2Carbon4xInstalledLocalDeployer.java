package com.tsystems.cargo.container.wso2.deployer;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.DeployableType;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2Axis2Service;

public class WSO2Carbon4xInstalledLocalDeployer extends AbstractWSO2InstalledLocalDeployer {

    public WSO2Carbon4xInstalledLocalDeployer(InstalledLocalContainer container) {
        super(container);
        setShouldDeployExpanded(CarbonApplication.TYPE, false);
        setShouldDeployExpanded(WSO2Axis2Service.TYPE, false);
        setShouldDeployExpanded(Axis2Service.TYPE, false);
        setShouldDeployExpanded(Axis2Module.TYPE, false);
    }

    @Override
    public String getDeployableDir(Deployable deployable) {
        String deployableDir = getFileHandler().append(getContainer().getConfiguration().getHome(), "tmp");

        if (CarbonApplication.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(((InstalledLocalContainer) getContainer()).getHome(),
                    "repository/deployment/server/carbonapps");
        } else if (DeployableType.WAR.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(((InstalledLocalContainer) getContainer()).getHome(),
                    "repository/deployment/server/webapps");
        } else if (Axis2Service.TYPE.equals(deployable.getType()) || WSO2Axis2Service.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(((InstalledLocalContainer) getContainer()).getHome(),
                    "repository/deployment/server/axis2services");
        } else if (Axis2Module.TYPE.equals(deployable.getType())) {
            deployableDir = getFileHandler().append(((InstalledLocalContainer) getContainer()).getHome(),
                    "repository/deployment/server/axis2modules");
        }

        if (!getFileHandler().exists(deployableDir)) {
            getFileHandler().mkdirs(deployableDir);
        }

        return deployableDir;
    }
}
