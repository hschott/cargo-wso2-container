package com.tsystems.cargo.container.wso2.deployer;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.DeployableMonitorListener;
import org.codehaus.cargo.util.log.LoggedObject;

import com.tsystems.cargo.container.wso2.deployable.WSO2Deployable;

public class WSO2RemoteDeployerMonitor extends LoggedObject implements DeployableMonitor
{

    private List<DeployableMonitorListener> listeners;

    private WSO2Deployable deployable;

    private WSO2RemoteDeployer remoteDeployer;

    public WSO2RemoteDeployerMonitor(WSO2Deployable deployable, WSO2RemoteDeployer remoteDeployer)
    {
        super();
        this.listeners = new ArrayList<DeployableMonitorListener>();
        this.deployable = deployable;
        this.remoteDeployer = remoteDeployer;
    }

    public String getDeployableName()
    {
        return deployable.getApplicationName();
    }

    public long getTimeout()
    {
        return deployable.getDeployTimeout();
    }

    public void monitor()
    {
        getLogger().debug(
            "Checking Deployable [" + getDeployableName() + "] for status using a timeout of ["
                + getTimeout() + "] ms...", this.getClass().getSimpleName());

        boolean isDeployed = remoteDeployer.exists(deployable);

        for (DeployableMonitorListener listener : listeners)
        {
            getLogger().debug("Notifying monitor listener [" + listener + "]",
                this.getClass().getSimpleName());

            if (isDeployed)
            {
                listener.deployed();
            }
            else
            {
                listener.undeployed();
            }
        }
    }

    public void registerListener(DeployableMonitorListener listener)
    {
        this.listeners.add(listener);
    }

}
