/*
 * ========================================================================
 *
 * Copyright 2007-2008 OW2. Code from this file
 * was originally imported from the OW2 JOnAS project.
 *
 * Codehaus CARGO, copyright 2004-2011 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package com.tsystems.cargo.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;

/**
 * JMX remoting (JSR 160) implementation to get a remote MBeanServerConnection.
 *
 */
public class JSR160MBeanServerConnectionFactory {
    /**
     * The default JMX remote URL to use with WSO2 server.
     */
    private static final String DEFAULT_URI = "service:jmx:rmi://localhost/jndi/rmi://localhost:9999/jmxrmi";

    /**
     * JMX Connector to use with WSO2 server.
     */
    private JMXConnector connector;

    /**
     * Shutdown the connector.
     */
    public void destroy() {
        if (connector != null) {
            try {
                connector.close();
            } catch (IOException e) {
                e.getMessage();
            }
            connector = null;
        }

    }

    /**
     * Creates a new connector and returns a MBeanServerConnection.
     *
     * @param configuration
     *            Configuration to read URI, username and password from.
     * @return MBeanServerConnection fresh connection
     * @throws IOException
     */
    public MBeanServerConnection getServerConnection(Configuration configuration) throws IOException {
        String username = configuration.getPropertyValue(RemotePropertySet.USERNAME);
        String password = configuration.getPropertyValue(RemotePropertySet.PASSWORD);

        String jmxRemoteURL = configuration.getPropertyValue(RemotePropertySet.URI);

        if (jmxRemoteURL == null || jmxRemoteURL.trim().length() == 0) {
            jmxRemoteURL = DEFAULT_URI;

            String port = configuration.getPropertyValue(GeneralPropertySet.RMI_PORT);
            if (port != null) {
                jmxRemoteURL = jmxRemoteURL.replace("9999", port);
            }

            String hostname = configuration.getPropertyValue(GeneralPropertySet.HOSTNAME);
            if (hostname != null) {
                jmxRemoteURL = jmxRemoteURL.replace("localhost", hostname);
            }

        }

        Map<String, Object> environment = new HashMap<String, Object>();

        Object credentials = new String[] { username, password };
        environment.put(JMXConnector.CREDENTIALS, credentials);

        if (!environment.containsKey(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER)) {
            environment.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, this.getClass().getClassLoader());
        }

        JMXServiceURL address = new JMXServiceURL(jmxRemoteURL);
        connector = JMXConnectorFactory.connect(address, environment);

        MBeanServerConnection mbsc = connector.getMBeanServerConnection();

        return mbsc;
    }
}
