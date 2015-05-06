package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.net.URL;

import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.util.log.LoggedObject;
import org.wso2.carbon.core.services.authentication.AuthenticationAdminStub;

import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2BaseAdminService;

public abstract class AbstractWSO2Carbon4xAdminService extends LoggedObject implements
    WSO2BaseAdminService
{

    private static long DEFAULT_TIMEOUT = 30 * 1000;

    private long timeout = DEFAULT_TIMEOUT;

    private URL url;

    private String sessionCookie;

    private String httpUsername;

    private String httpPassword;

    private String wso2username;

    private String wso2password;

    public AbstractWSO2Carbon4xAdminService(URL url, String wso2username, String wso2password,
        String httpUsername, String httpPassword)
    {
        super();
        this.url = url;
        this.wso2username = wso2username;
        this.wso2password = wso2password;
        this.httpUsername = httpUsername;
        this.httpPassword = httpPassword;
        easySSL();
    }

    void authenticate() throws WSO2AdminServicesException
    {
        if (sessionCookie != null)
        {
            return;
        }
        try
        {
            AuthenticationAdminStub authenticationStub =
                new AuthenticationAdminStub(new URL(getUrl() + "/services/AuthenticationAdmin").toString());
            prepareServiceClient(authenticationStub._getServiceClient());
            if (authenticationStub.login(wso2username, wso2password, url.getHost()))
            {
                ServiceContext serviceContext =
                    authenticationStub._getServiceClient().getLastOperationContext()
                        .getServiceContext();
                sessionCookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
                getLogger().info("Authentication to " + getUrl() + " successful.",
                    getClass().getSimpleName());
                return;
            }
        }
        catch (Exception e)
        {
            throw new WSO2AdminServicesException("exception authenticating user " + wso2username,
                e);
        }
        throw new WSO2AdminServicesException("error authenticating user " + wso2username);
    }

    private void easySSL()
    {
        ProtocolSocketFactory easySSLProtocolSocketFactory = new EasySSLProtocolSocketFactory();
        Protocol.unregisterProtocol("https");
        Protocol.registerProtocol("https", new Protocol("https",
            easySSLProtocolSocketFactory,
            getUrl().getPort() == -1 ? 443 : getUrl().getPort()));
    }

    public URL getUrl()
    {
        return url;
    }

    void logExists(Deployable deployable)
    {
        getLogger().debug("Check deploy status of [" + deployable.getFile() + "] on " + getUrl(),
            getClass().getSimpleName());
    }

    void logRemove(Deployable deployable)
    {
        getLogger().info("Remove [" + deployable.getFile() + "] from " + getUrl(),
            getClass().getSimpleName());
    }

    void logStart(Deployable deployable)
    {
        getLogger().info("Starting [" + deployable.getFile() + "] on " + getUrl(),
            getClass().getSimpleName());
    }

    void logStop(Deployable deployable)
    {
        getLogger().info("Stopping [" + deployable.getFile() + "] on " + getUrl(),
            getClass().getSimpleName());
    }

    void logUpload(Deployable deployable)
    {
        getLogger().info("Uploading [" + deployable.getFile() + "] to " + getUrl(),
            getClass().getSimpleName());

    }

    void prepareServiceClient(ServiceClient serviceClient)
    {
        serviceClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        serviceClient.getOptions().setTimeOutInMilliSeconds(timeout);
        serviceClient.getOptions().setManageSession(true);
        serviceClient.getOptions().setProperty(HTTPConstants.COOKIE_STRING, sessionCookie);

        if (httpUsername != null && httpUsername.length() >= 0 && httpPassword != null)
        {
            HttpTransportProperties.Authenticator authenticator =
                new HttpTransportProperties.Authenticator();
            authenticator.setUsername(httpUsername);
            authenticator.setPassword(httpPassword);
            serviceClient.getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
        }
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
}
