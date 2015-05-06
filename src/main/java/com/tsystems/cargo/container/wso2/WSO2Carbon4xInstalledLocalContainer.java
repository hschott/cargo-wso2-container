package com.tsystems.cargo.container.wso2;

import java.io.File;
import java.io.FilenameFilter;

import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.spi.jvm.JvmLauncher;

public class WSO2Carbon4xInstalledLocalContainer extends AbstractWSO2InstalledLocalContainer
    implements WSO2Carbon4xContainer
{

    public static final String NAME = "WSO2 Carbon 4.x";

    public WSO2Carbon4xInstalledLocalContainer(LocalConfiguration configuration)
    {
        super(configuration);
    }

    @Override
    protected void addBootstrapJarToClasspath(JvmLauncher java)
    {
        String base = getFileHandler().getAbsolutePath(getHome());

        String bin = getFileHandler().append(base, "bin");
        String[] bootstrap = new File(bin).list(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.startsWith("org.wso2.carbon.bootstrap-4.") && name.endsWith(".jar");
            }
        });

        for (String jar : bootstrap)
        {
            java.addClasspathEntries(getFileHandler().append(bin, jar));
        }
    }

    @Override
    public String getCommonName()
    {
        return NAME;
    }

    public String getId()
    {
        return ID;
    }
}
