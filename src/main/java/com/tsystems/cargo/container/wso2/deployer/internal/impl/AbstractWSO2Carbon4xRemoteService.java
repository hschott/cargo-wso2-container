package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.util.log.LoggedObject;
import org.wso2.carbon.core.services.authentication.AuthenticationAdminStub;

import com.tsystems.cargo.container.wso2.configuration.WSO2CarbonPropertySet;
import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public abstract class AbstractWSO2Carbon4xRemoteService extends LoggedObject
{

    private static final String SERVICES_AUTHENTICATION_ADMIN = "/services/AuthenticationAdmin";

    private static final long DEFAULT_TIMEOUT = 10 * 60 * 1000;

    private long timeout = DEFAULT_TIMEOUT;

    protected URL url;

    private String sessionCookie;

    protected String httpUsername;

    protected String httpPassword;

    protected String wso2username;

    protected String wso2password;

    public AbstractWSO2Carbon4xRemoteService(Configuration configuration)
    {
        super();

        this.url = getCarbonBaseURL(configuration);
        this.wso2username = configuration.getPropertyValue(WSO2CarbonPropertySet.CARBON_USERNAME);
        this.wso2password = configuration.getPropertyValue(WSO2CarbonPropertySet.CARBON_PASSWORD);
        this.httpUsername = configuration.getPropertyValue(RemotePropertySet.USERNAME);
        this.httpPassword = configuration.getPropertyValue(RemotePropertySet.PASSWORD);

        setLogger(configuration.getLogger());
        easySSL();
    }

    protected URL getCarbonBaseURL(Configuration configuration)
    {
        URL carbonUrl;

        String managerURL = configuration.getPropertyValue(RemotePropertySet.URI);

        // If not defined by the user use a default URL
        if (managerURL == null)
        {
            managerURL =
                configuration.getPropertyValue(GeneralPropertySet.PROTOCOL) + "://"
                    + configuration.getPropertyValue(GeneralPropertySet.HOSTNAME) + ":"
                    + configuration.getPropertyValue(ServletPropertySet.PORT);

            getLogger().info("Setting WSO2 Carbon URL to " + managerURL,
                getClass().getSimpleName());
        }

        getLogger().debug("WSO2 Carbon URL is " + managerURL, getClass().getSimpleName());

        try
        {
            carbonUrl = new URL(managerURL);
        }
        catch (MalformedURLException e)
        {
            throw new ContainerException("Invalid WSO2 Carbon URL [" + managerURL + "]", e);
        }

        return carbonUrl;
    }

    protected void authenticate() throws WSO2AdminServicesException
    {
        if (sessionCookie == null)
        {
            try
            {
                if (wso2username != null && wso2username.length() >= 0 && wso2password != null)
                {
                    getLogger().info(
                        "Authenticate on " + getUrl() + " with '" + wso2username + "'",
                        getClass().getSimpleName());
                    AuthenticationAdminStub authenticationStub =
                        new AuthenticationAdminStub(new URL(getUrl()
                            + SERVICES_AUTHENTICATION_ADMIN).toString());
                    prepareStubInternal(authenticationStub);
                    if (authenticationStub.login(wso2username, wso2password, url.getHost()))
                    {
                        ServiceContext serviceContext =
                            authenticationStub._getServiceClient().getLastOperationContext()
                                .getServiceContext();
                        sessionCookie =
                            (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
                        getLogger()
                            .info("Authentication successful.", getClass().getSimpleName());
                    }
                    else
                    {
                        throw new WSO2AdminServicesException("error authenticating user "
                            + wso2username);
                    }
                }
            }
            catch (Exception e)
            {
                throw new WSO2AdminServicesException("exception authenticating user "
                    + wso2username, e);
            }
        }
    }

    protected void easySSL()
    {
        ProtocolSocketFactory easySSLProtocolSocketFactory = new EasySSLProtocolSocketFactory();
        Protocol.unregisterProtocol("https");
        Protocol.registerProtocol("https", new Protocol("https",
            easySSLProtocolSocketFactory,
            getUrl().getPort() == -1 ? 443 : getUrl().getPort()));
    }

    protected URL getUrl()
    {
        return url;
    }

    protected void prepareStub(Stub stub)
    {
        authenticate();

        prepareStubInternal(stub);
    }

    private void prepareStubInternal(Stub stub)
    {
        Options options = stub._getServiceClient().getOptions();
        options.setExceptionToBeThrownOnSOAPFault(true);
        options.setTimeOutInMilliSeconds(timeout);
        options.setManageSession(true);
        if (sessionCookie != null)
        {
            options.setProperty(HTTPConstants.COOKIE_STRING, sessionCookie);
        }
        options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);

        if (httpUsername != null && httpUsername.length() >= 0 && httpPassword != null)
        {
            HttpTransportProperties.Authenticator authenticator =
                new HttpTransportProperties.Authenticator();
            authenticator.setUsername(httpUsername);
            authenticator.setPassword(httpPassword);
            options.setProperty(HTTPConstants.AUTHENTICATE, authenticator);
        }
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

}
