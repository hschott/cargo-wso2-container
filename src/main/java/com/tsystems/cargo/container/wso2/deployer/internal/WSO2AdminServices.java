package com.tsystems.cargo.container.wso2.deployer.internal;

import java.net.URL;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;

public interface WSO2AdminServices {

	public abstract void deploy(Axis2Module deployable)
			throws WSO2AdminServicesException;

	public abstract void deploy(Axis2Service deployable)
			throws WSO2AdminServicesException;

	public abstract void deploy(CarbonApplication deployable)
			throws WSO2AdminServicesException;

	public abstract void deploy(WSO2WAR deployable)
			throws WSO2AdminServicesException;

	public abstract boolean exists(Axis2Module deployable)
			throws WSO2AdminServicesException;

	public abstract boolean exists(Axis2Service deployable)
			throws WSO2AdminServicesException;

	public abstract boolean exists(CarbonApplication deployable)
			throws WSO2AdminServicesException;

	public abstract boolean exists(WSO2WAR deployable)
			throws WSO2AdminServicesException;

	public abstract URL getUrl();

	public abstract void start(Axis2Module deployable)
			throws WSO2AdminServicesException;

	public abstract void start(Axis2Service deployable)
			throws WSO2AdminServicesException;

	public abstract void start(WSO2WAR deployable)
			throws WSO2AdminServicesException;

	public abstract void stop(Axis2Module deployable)
			throws WSO2AdminServicesException;

	public abstract void stop(Axis2Service deployable)
			throws WSO2AdminServicesException;

	public abstract void stop(WSO2WAR deployable)
			throws WSO2AdminServicesException;

	public abstract void undeploy(Axis2Module deployable)
			throws WSO2AdminServicesException;

	public abstract void undeploy(Axis2Service deployable)
			throws WSO2AdminServicesException;

	public abstract void undeploy(CarbonApplication deployable)
			throws WSO2AdminServicesException;

	public abstract void undeploy(WSO2WAR deployable)
			throws WSO2AdminServicesException;

}