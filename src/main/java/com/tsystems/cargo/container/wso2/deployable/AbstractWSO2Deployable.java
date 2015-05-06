package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.spi.deployable.AbstractDeployable;

public abstract class AbstractWSO2Deployable extends AbstractDeployable
{

    private long deployTimeout = -1;

    public AbstractWSO2Deployable(String file)
    {
        super(file);
    }

    public long getDeployTimeout()
    {
        return deployTimeout;
    }

    public void setDeployTimeout(String deployTimeout)
    {
        this.deployTimeout = Long.valueOf(deployTimeout);
    }

    protected String parseApplication(String extension)
    {
        String name = getFileHandler().getName(getFile());

        int index = name.toLowerCase().lastIndexOf(extension);
        if (index >= 0)
        {
            name = name.substring(0, index);
        }

        return name;
    }

}
