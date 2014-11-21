package com.tsystems.cargo.container.wso2.configuration;

import java.util.Iterator;

import org.codehaus.cargo.container.configuration.entry.DataSource;
import org.codehaus.cargo.container.configuration.entry.Resource;
import org.codehaus.cargo.container.spi.configuration.builder.AbstractConfigurationBuilder;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class WSO2ConfigurationBuilder extends AbstractConfigurationBuilder {

    @Override
    public String buildConfigurationEntryForXADataSourceConfiguredDataSource(DataSource ds) {
        Element datasource = DocumentHelper.createDocument().addElement("datasource");
        Element configuration = createConfigurationElement(ds, datasource);

        configuration.addElement("dataSourceClassName").setText(ds.getDriverClass());

        ds.getConnectionProperties().put("url", ds.getUrl());
        ds.getConnectionProperties().put("user", ds.getUsername());
        ds.getConnectionProperties().put("password", ds.getPassword());

        Element props = configuration.addElement("dataSourceProps");
        if (ds.getConnectionProperties() != null && ds.getConnectionProperties().size() != 0) {
            Iterator<Object> i = ds.getConnectionProperties().keySet().iterator();
            while (i.hasNext()) {
                String key = i.next().toString();
                props.addElement("property").addAttribute("name", key)
                        .setText(ds.getConnectionProperties().getProperty(key));
            }

        }
        configureConnectionPooling(configuration);

        return datasource.asXML();
    }

    @Override
    public String buildEntryForDriverConfiguredDataSourceWithLocalTx(DataSource ds) {
        throw new UnsupportedOperationException("WSO2 does not support " + ds.getTransactionSupport()
                + " for DataSource implementations.");
    }

    @Override
    public String buildEntryForDriverConfiguredDataSourceWithNoTx(DataSource ds) {
        Element datasource = DocumentHelper.createDocument().addElement("datasource");
        Element configuration = createConfigurationElement(ds, datasource);

        configuration.addElement("url").setText(ds.getUrl());
        configuration.addElement("username").setText(ds.getUsername());
        configuration.addElement("password").setText(ds.getPassword());

        configuration.addElement("driverClassName").setText(ds.getDriverClass());

        configureConnectionPooling(configuration);

        return datasource.asXML();
    }

    @Override
    public String buildEntryForDriverConfiguredDataSourceWithXaTx(DataSource ds) {
        throw new UnsupportedOperationException("WSO2 does not support " + ds.getTransactionSupport()
                + " for DataSource implementations.");
    }

    private void configureConnectionPooling(Element configuration) {
        configuration.addElement("maxActive").setText("50");
        configuration.addElement("maxWait").setText("30000");
        configuration.addElement("testOnBorrow").setText("true");
        configuration.addElement("validationQuery").setText("SELECT 1");
        configuration.addElement("validationInterval").setText("30000");
    }

    private Element createConfigurationElement(DataSource ds, Element datasource) {
        datasource.addElement("name").setText(ds.getId());
        datasource.addElement("description").setText("Configured by Cargo");
        if (ds.getJndiLocation() != null && ds.getJndiLocation().length() > 0) {
            datasource.addElement("jndiConfig").addElement("name").setText(ds.getJndiLocation());
        }
        Element configuration = datasource.addElement("definition").addAttribute("type", "RDBMS")
                .addElement("configuration");
        return configuration;
    }

    public String toConfigurationEntry(Resource resource) {
        Element res = DocumentHelper.createDocument().addElement("Resource");
        res.addAttribute("name", resource.getName());
        res.addAttribute("auth", "Container");
        res.addAttribute("type", resource.getType());
        res.addAttribute("driverClassName", resource.getClassName());

        for (String parameterName : resource.getParameterNames()) {
            res.addAttribute(parameterName, resource.getParameter(parameterName));
        }
        return res.asXML();
    }
}
