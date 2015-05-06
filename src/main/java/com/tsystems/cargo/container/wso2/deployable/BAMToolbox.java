package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.deployable.DeployableType;

/**
 * A WSO2 BAM Toolbox deployable. Matches
 * https://docs.wso2.com/display/BAM241/Creating+a+Custom+Toolbox packaging type.
 */
public class BAMToolbox extends AbstractWSO2Deployable implements WSO2Deployable
{

    public static final DeployableType TYPE = DeployableType.toType("tbox");

    private String applicationName;

    public BAMToolbox(final String file)
    {
        super(file);
    }

    public final String getApplicationName()
    {
        if (applicationName == null || applicationName.length() == 0)
        {
            applicationName = parseApplication(".tbox");
        }
        return applicationName;
    }

    public DeployableType getType()
    {
        return TYPE;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }
}
