package com.tsystems.cargo.container.wso2.deployable;

import org.codehaus.cargo.container.deployable.DeployableType;

public class WSO2CarbonApplication extends CarbonApplication
{

    public static final DeployableType TYPE = DeployableType.toType("carbon/application");

    public WSO2CarbonApplication(String file)
    {
        super(file);
    }

    @Override
    public DeployableType getType()
    {
        return TYPE;
    }

}
