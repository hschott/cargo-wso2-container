package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.net.URL;

import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.util.log.LoggedObject;
import org.wso2.carbon.core.services.authentication.stub.AuthenticationAdminStub;

import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2BaseAdminService;

public abstract class AbstractWSO2Carbon4xAdminService extends LoggedObject implements WSO2BaseAdminService {

    private long timeout = 30 * 60 * 1000;

    private URL url;
    private String sessionCookie;
    private String username;
    private String password;

    public AbstractWSO2Carbon4xAdminService(URL url, String username, String password) {
        super();
        this.url = url;
        this.username = username;
        this.password = password;
        easySSL();
    }

    void authenticate() throws WSO2AdminServicesException {
        if (sessionCookie != null) {
            return;
        }
        try {
            AuthenticationAdminStub authenticationStub = new AuthenticationAdminStub(new URL(getUrl()
                    + "/services/AuthenticationAdmin").toString());
            authenticationStub._getServiceClient().getOptions().setManageSession(true);
            if (authenticationStub.login(username, password, url.getHost())) {
                ServiceContext serviceContext = authenticationStub._getServiceClient().getLastOperationContext()
                        .getServiceContext();
                sessionCookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
                getLogger().info("Authentication to " + getUrl() + " successful.", getClass().getSimpleName());
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error authenticating user", e);
        }
    }

    private void easySSL() {
        EasySSLProtocolSocketFactory easySSLProtocolSocketFactory;
        easySSLProtocolSocketFactory = new EasySSLProtocolSocketFactory();
        Protocol.unregisterProtocol("https");
        Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory) easySSLProtocolSocketFactory,
                getUrl().getPort() == -1 ? 443 : getUrl().getPort()));
    }

    public URL getUrl() {
        return url;
    }

    void logExists(Deployable deployable) {
        getLogger().info("Check deploy status of [" + deployable.getFile() + "] on " + getUrl(),
                getClass().getSimpleName());
    }

    void logRemove(Deployable deployable) {
        getLogger().info("Remove [" + deployable.getFile() + "] from " + getUrl(), getClass().getSimpleName());
    }

    void logStart(Deployable deployable) {
        getLogger().info("Starting [" + deployable.getFile() + "] on " + getUrl(), getClass().getSimpleName());
    }

    void logStop(Deployable deployable) {
        getLogger().info("Stopping [" + deployable.getFile() + "] on " + getUrl(), getClass().getSimpleName());
    }

    void logUpload(Deployable deployable) {
        getLogger().info("Uploading [" + deployable.getFile() + "] to " + getUrl(), getClass().getSimpleName());

    }

    void prepareServiceClient(ServiceClient serviceClient) {
        serviceClient.getOptions().setTimeOutInMilliSeconds(timeout);
        serviceClient.getOptions().setManageSession(true);
        serviceClient.getOptions().setProperty(HTTPConstants.COOKIE_STRING, sessionCookie);
    }
}
