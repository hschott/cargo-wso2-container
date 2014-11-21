package com.tsystems.cargo.container.wso2.deployer.internal;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import javax.activation.DataHandler;

import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.cargo.util.log.LoggedObject;
import org.wso2.carbon.aarservices.mgt.stub.ServiceAdminStub;
import org.wso2.carbon.aarservices.mgt.stub.ServiceGroupAdminStub;
import org.wso2.carbon.aarservices.uploader.stub.ServiceUploaderStub;
import org.wso2.carbon.aarservices.xsd.AARServiceData;
import org.wso2.carbon.application.mgt.stub.ApplicationAdminStub;
import org.wso2.carbon.application.upload.stub.CarbonAppUploaderStub;
import org.wso2.carbon.application.upload.xsd.UploadedFileItem;
import org.wso2.carbon.core.services.authentication.stub.AuthenticationAdminStub;
import org.wso2.carbon.module.mgt.stub.ModuleAdminServiceStub;
import org.wso2.carbon.module.mgt.xsd.ModuleMetaData;
import org.wso2.carbon.module.mgt.xsd.ModuleUploadData;
import org.wso2.carbon.service.mgt.xsd.ServiceGroupMetaData;
import org.wso2.carbon.service.mgt.xsd.ServiceMetaData;
import org.wso2.carbon.webapp.mgt.stub.WebappAdminStub;
import org.wso2.carbon.webapp.mgt.xsd.WebappMetadata;
import org.wso2.carbon.webapp.mgt.xsd.WebappUploadData;

import com.tsystems.cargo.container.wso2.deployable.Axis2Module;
import com.tsystems.cargo.container.wso2.deployable.Axis2Service;
import com.tsystems.cargo.container.wso2.deployable.CarbonApplication;
import com.tsystems.cargo.container.wso2.deployable.WSO2WAR;

public class WSO2Carbon4xAdminServices extends LoggedObject implements WSO2AdminServices {

    private long timeout = 30 * 60 * 1000;

    private URL url;

    private String sessionCookie;

    private String username;

    private String password;

    public WSO2Carbon4xAdminServices(URL url, String username, String password) {
        super();
        this.url = url;
        this.username = username;
        this.password = password;

        easySSL();

    }

    private void authenticate() throws WSO2AdminServicesException {
        if (sessionCookie != null) {
            return;
        }
        try {
            AuthenticationAdminStub authenticationStub = new AuthenticationAdminStub(new URL(this.url
                    + "/services/AuthenticationAdmin").toString());
            authenticationStub._getServiceClient().getOptions().setManageSession(true);
            if (authenticationStub.login(username, password, url.getHost())) {
                ServiceContext serviceContext = authenticationStub._getServiceClient().getLastOperationContext()
                        .getServiceContext();
                sessionCookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
                getLogger().info("Authentication to " + this.url + " successful.", getClass().getSimpleName());
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error authenticating user", e);
        }
    }

    public void deploy(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(this.url
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());

            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            ModuleUploadData moduleUploadData = new ModuleUploadData();
            moduleUploadData.setFileName(new File(deployable.getFile()).getName());
            moduleUploadData.setDataHandler(dh);
            moduleAdminServiceStub.uploadModule(new ModuleUploadData[] { moduleUploadData });
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading axis2 module", e);
        }
    }

    public void deploy(Axis2Service deployable) throws WSO2AdminServicesException {
        try {
            ServiceUploaderStub serviceUploaderStub = new ServiceUploaderStub(new URL(this.url
                    + "/services/ServiceUploader").toString());
            authenticate();
            prepareServiceClient(serviceUploaderStub._getServiceClient());

            AARServiceData aarServiceData = new AARServiceData();
            aarServiceData.setFileName(new File(deployable.getFile()).getName());
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            aarServiceData.setDataHandler(dh);
            aarServiceData.setServiceHierarchy("");
            serviceUploaderStub.uploadService(new AARServiceData[] { aarServiceData });
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading axis2 service", e);
        }
    }

    public void deploy(CarbonApplication deployable) throws WSO2AdminServicesException {
        try {
            CarbonAppUploaderStub carbonAppUploaderStub = new CarbonAppUploaderStub(new URL(this.url
                    + "/services/CarbonAppUploader").toString());
            authenticate();
            prepareServiceClient(carbonAppUploaderStub._getServiceClient());
            UploadedFileItem[] carbonAppArray = new UploadedFileItem[1];
            UploadedFileItem carbonApp = new UploadedFileItem();
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            carbonApp.setFileName(new File(deployable.getFile()).getName());
            carbonApp.setDataHandler(dh);
            carbonApp.setFileType("jar");
            carbonAppArray[0] = carbonApp;
            carbonAppUploaderStub.uploadApp(carbonAppArray);
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading carbon application", e);
        }
    }

    public void deploy(WSO2WAR deployable) throws WSO2AdminServicesException {
        try {
            WebappAdminStub webappAdminStub = new WebappAdminStub(
                    new URL(this.url + "/services/WebappAdmin").toString());
            authenticate();
            prepareServiceClient(webappAdminStub._getServiceClient());
            WebappUploadData webApp = new WebappUploadData();
            DataHandler dh = new DataHandler(new File(deployable.getFile()).toURI().toURL());
            webApp.setFileName(deployable.getName());
            webApp.setDataHandler(dh);

            webappAdminStub.uploadWebapp(new WebappUploadData[] { webApp });
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error uploading webapp", e);
        }
    }

    private void easySSL() {
        EasySSLProtocolSocketFactory easySSLProtocolSocketFactory;
        easySSLProtocolSocketFactory = new EasySSLProtocolSocketFactory();
        Protocol.unregisterProtocol("https");
        Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory) easySSLProtocolSocketFactory,
                this.url.getPort() == -1 ? 443 : this.url.getPort()));
    }

    public boolean exists(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(this.url
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());

            ModuleMetaData[] moduleMetaData = moduleAdminServiceStub.listModules();

            if (moduleMetaData != null) {
                for (ModuleMetaData module : moduleMetaData) {
                    if (module.getModulename().equals(deployable.getApplicationName())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error checking axis2 module", e);
        }
        return false;
    }

    public boolean exists(Axis2Service deployable) throws WSO2AdminServicesException {
        try {
            ServiceGroupAdminStub serviceGroupAdminStub = new ServiceGroupAdminStub(new URL(this.url
                    + "/services/ServiceGroupAdmin").toString());
            authenticate();
            prepareServiceClient(serviceGroupAdminStub._getServiceClient());

            ServiceGroupMetaData serviceGroupMetaData = serviceGroupAdminStub.listServiceGroup(deployable
                    .getApplicationName());

            if (serviceGroupMetaData != null) {
                ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
                if (serviceMetaDataList == null || serviceMetaDataList.length == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean exists(CarbonApplication deployable) throws WSO2AdminServicesException {
        try {
            ApplicationAdminStub applicationAdminStub = new ApplicationAdminStub(new URL(this.url
                    + "/services/ApplicationAdmin").toString());
            authenticate();
            prepareServiceClient(applicationAdminStub._getServiceClient());
            String[] existingApplications = applicationAdminStub.listAllApplications();
            if (existingApplications != null
                    && Arrays.asList(existingApplications).contains(deployable.getApplicationName())) {
                return true;
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error checking carbon application", e);
        }

        return false;
    }

    public boolean exists(WSO2WAR deployable) throws WSO2AdminServicesException {
        try {
            WebappAdminStub webappAdminStub = new WebappAdminStub(
                    new URL(this.url + "/services/WebappAdmin").toString());
            authenticate();
            prepareServiceClient(webappAdminStub._getServiceClient());

            String name = deployable.getName();

            WebappMetadata webappMetadata = webappAdminStub.getStartedWebapp(name);
            if (webappMetadata != null)
                return true;
            webappMetadata = webappAdminStub.getStoppedWebapp(name);
            if (webappMetadata != null)
                return true;
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error checking webapp", e);
        }
        return false;
    }

    public URL getUrl() {
        return url;
    }

    private void prepareServiceClient(ServiceClient serviceClient) {
        serviceClient.getOptions().setTimeOutInMilliSeconds(timeout);
        serviceClient.getOptions().setManageSession(true);
        serviceClient.getOptions().setProperty(HTTPConstants.COOKIE_STRING, sessionCookie);
    }

    public void start(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(this.url
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());
            moduleAdminServiceStub.globallyEngageModule(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error starting axis2 module", e);
        }
    }

    public void start(Axis2Service deployable) throws WSO2AdminServicesException {
        try {
            ServiceGroupAdminStub serviceGroupAdminStub = new ServiceGroupAdminStub(new URL(this.url
                    + "/services/ServiceGroupAdmin").toString());
            ServiceAdminStub serviceAdminStub = new ServiceAdminStub(
                    new URL(this.url + "/services/ServiceAdmin").toString());
            authenticate();
            prepareServiceClient(serviceGroupAdminStub._getServiceClient());
            prepareServiceClient(serviceAdminStub._getServiceClient());

            ServiceGroupMetaData serviceGroupMetaData = serviceGroupAdminStub.listServiceGroup(deployable
                    .getApplicationName());

            ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
            if (serviceMetaDataList != null) {
                for (ServiceMetaData serviceMetaData : serviceMetaDataList) {
                    serviceAdminStub.startService(serviceMetaData.getName());
                }
            }

        } catch (Exception e) {
            throw new WSO2AdminServicesException("error starting axis2 service", e);
        }
    }

    public void start(WSO2WAR deployable) throws WSO2AdminServicesException {
        try {
            WebappAdminStub webappAdminStub = new WebappAdminStub(
                    new URL(this.url + "/services/WebappAdmin").toString());
            authenticate();
            prepareServiceClient(webappAdminStub._getServiceClient());

            String name = deployable.getName();

            webappAdminStub.startWebapps(new String[] { name });

        } catch (Exception e) {
            throw new WSO2AdminServicesException("error starting webapp", e);
        }
    }

    public void stop(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(this.url
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());
            moduleAdminServiceStub.globallyDisengageModule(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error stopping axis2 module", e);
        }
    }

    public void stop(Axis2Service deployable) throws WSO2AdminServicesException {
        try {
            ServiceGroupAdminStub serviceGroupAdminStub = new ServiceGroupAdminStub(new URL(this.url
                    + "/services/ServiceGroupAdmin").toString());
            ServiceAdminStub serviceAdminStub = new ServiceAdminStub(
                    new URL(this.url + "/services/ServiceAdmin").toString());
            authenticate();
            prepareServiceClient(serviceGroupAdminStub._getServiceClient());
            prepareServiceClient(serviceAdminStub._getServiceClient());

            ServiceGroupMetaData serviceGroupMetaData = serviceGroupAdminStub.listServiceGroup(deployable
                    .getApplicationName());

            ServiceMetaData[] serviceMetaDataList = serviceGroupMetaData.getServices();
            if (serviceMetaDataList != null) {
                for (ServiceMetaData serviceMetaData : serviceMetaDataList) {
                    serviceAdminStub.stopService(serviceMetaData.getName());
                }
            }
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error stopping axis2 service", e);
        }
    }

    public void stop(WSO2WAR deployable) throws WSO2AdminServicesException {
        try {
            WebappAdminStub webappAdminStub = new WebappAdminStub(
                    new URL(this.url + "/services/WebappAdmin").toString());
            authenticate();
            prepareServiceClient(webappAdminStub._getServiceClient());

            String name = deployable.getName();

            webappAdminStub.stopWebapps(new String[] { name });

        } catch (Exception e) {
            throw new WSO2AdminServicesException("error stopping webapp", e);
        }
    }

    public void undeploy(Axis2Module deployable) throws WSO2AdminServicesException {
        try {
            ModuleAdminServiceStub moduleAdminServiceStub = new ModuleAdminServiceStub(new URL(this.url
                    + "/services/ModuleAdminService").toString());
            authenticate();
            prepareServiceClient(moduleAdminServiceStub._getServiceClient());
            moduleAdminServiceStub.removeModule(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing axis2 module", e);
        }
    }

    public void undeploy(Axis2Service deployable) throws WSO2AdminServicesException {
        try {
            ServiceAdminStub serviceAdminStub = new ServiceAdminStub(
                    new URL(this.url + "/services/ServiceAdmin").toString());
            authenticate();
            prepareServiceClient(serviceAdminStub._getServiceClient());

            serviceAdminStub.deleteServiceGroups(new String[] { deployable.getApplicationName() });

        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing axis2 service", e);
        }
    }

    public void undeploy(CarbonApplication deployable) throws WSO2AdminServicesException {
        try {
            ApplicationAdminStub applicationAdminStub = new ApplicationAdminStub(new URL(this.url
                    + "/services/ApplicationAdmin").toString());
            authenticate();
            prepareServiceClient(applicationAdminStub._getServiceClient());
            applicationAdminStub.deleteApplication(deployable.getApplicationName());
        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing carbon application", e);
        }
    }

    public void undeploy(WSO2WAR deployable) throws WSO2AdminServicesException {
        try {
            WebappAdminStub webappAdminStub = new WebappAdminStub(
                    new URL(this.url + "/services/WebappAdmin").toString());
            authenticate();
            prepareServiceClient(webappAdminStub._getServiceClient());

            String name = deployable.getName();

            WebappMetadata webappMetadata = webappAdminStub.getStoppedWebapp(name);
            if (webappMetadata != null) {
                if (webappMetadata.getFaulty()) {
                    webappAdminStub.deleteFaultyWebapps(new String[] { name });
                } else {
                    webappAdminStub.deleteStoppedWebapps(new String[] { name });
                }
            } else {
                webappAdminStub.stopWebapps(new String[] { name });
                webappAdminStub.deleteStoppedWebapps(new String[] { name });
            }

        } catch (Exception e) {
            throw new WSO2AdminServicesException("error removing webapp", e);
        }
    }
}
