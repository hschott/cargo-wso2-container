package com.tsystems.cargo.container.wso2.deployer.internal.impl;

import java.io.IOException;
import java.net.URL;

import org.codehaus.cargo.container.configuration.Configuration;
import org.wso2.carbon.logging.service.LogViewerStub;
import org.wso2.carbon.logging.service.data.xsd.LogEvent;
import org.wso2.carbon.logging.service.data.xsd.PaginatedLogEvent;

import com.tsystems.cargo.container.wso2.deployer.internal.WSO2AdminServicesException;

public class WSO2Carbon4xLoggingViewService extends AbstractWSO2Carbon4xRemoteService
{

    private static final String SERVICES_LOGVIEWER = "/services/LogViewer";

    private LogViewerStub serviceStub;

    public WSO2Carbon4xLoggingViewService(Configuration configuration)
    {
        super(configuration);
    }

    protected LogViewerStub getServiceStub() throws IOException
    {
        if (serviceStub == null)
        {
            LogViewerStub logViewerStub =
                new LogViewerStub(new URL(getUrl() + SERVICES_LOGVIEWER).toString());
            prepareStub(logViewerStub);

            serviceStub = logViewerStub;
        }
        return serviceStub;
    }

    public void remoteLog() throws WSO2AdminServicesException
    {
        try
        {
            LogViewerStub logViewerStub = getServiceStub();

            PaginatedLogEvent paginatedLogEvent =
                logViewerStub.getPaginatedLogEvents(0, "ERROR", "", "", "");

            if (paginatedLogEvent != null)
            {
                LogEvent[] logEvents = paginatedLogEvent.getLogInfo();
                if (logEvents != null)
                {
                    getLogger().info("-------------- Remote errors begin -------------- ",
                        WSO2Carbon4xLoggingViewService.class.getSimpleName());

                    for (LogEvent logEvent : logEvents)
                    {
                        StringBuffer message = new StringBuffer();
                        message.append(logEvent.getLogTime()).append(' ')
                            .append(logEvent.getMessage());
                        getLogger().warn(message.toString(), logEvent.getLogger());
                    }

                    getLogger().info("-------------- Remote errors end -------------- ",
                        WSO2Carbon4xLoggingViewService.class.getSimpleName());
                }
            }

        }
        catch (Exception e)
        {
            getLogger().warn("error retrieving remote log",
                WSO2Carbon4xLoggingViewService.class.getSimpleName());
        }
    }
}
