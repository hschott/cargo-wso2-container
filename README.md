# Introduction

This project is a [Cargo](http://cargo.codehaus.org/) container implementation for [WSO2 Carbon](http://www.wso2.com).
It provides capabilities to deploy WSO2 applications to installed and remote WSO2 Carbon servers.

This document will focus on [Maven](http://maven.apache.org/) integration.
For more details about configuring Cargo visit please [Cargo](http://cargo.codehaus.org/) website.

## Supported Container Features

*The cargo-wso2-container 0.1.0 supports WSO2 Carbon 4.x*

Container ID: wso2carbon4x

* [Container Instantiation](http://cargo.codehaus.org/Container+Instantiation)
* [Local Container](http://cargo.codehaus.org/Local+Container)
  * [Container Classpath](http://cargo.codehaus.org/Container+Classpath)
  * [Container Start](http://cargo.codehaus.org/Container+Start)
  * [Container Stop](http://cargo.codehaus.org/Container+Stop)
  * [Container Timeout](http://cargo.codehaus.org/Container+Timeout)
  * [Installed Container](http://cargo.codehaus.org/Installed+Container)
    * [Installer](http://cargo.codehaus.org/Installer)
    * [Passing System Properties](http://cargo.codehaus.org/Passing+system+properties)
* [Remote Container](http://cargo.codehaus.org/Remote+Container)

## Unsupported Features

* [Embedded Container](http://cargo.codehaus.org/Embedded+Container)

## Supported Configuration Feature

* [Standalone Local Configuration for installed container](http://cargo.codehaus.org/Standalone+Local+Configuration)
* [Existing Local Configuration for installed container](http://cargo.codehaus.org/Existing+Local+Configuration)
* [Runtime Configuration](http://cargo.codehaus.org/Runtime+Configuration)

## Supported Deployer Features

Static deployment of:

* [WAR](http://cargo.codehaus.org/Static+deployment+of+WAR)
* [Expanded WAR](http://cargo.codehaus.org/Static+deployment+of+expanded+WAR),
* CAR (Carbon Application)
* AAR (Axis2 Service)
* MAR (Axis2 Module)
* ZIP (WSO2 Connector)
* TBOX (BAM Toolbox)

For deployables of type WAR a version can be configured.

Remote deployment and un-deployment of:

* WAR
* CAR
* AAR 
* MAR
* ZIP (WSO2 Connector)
* TBOX

Remote start and stop of:

* WAR
* AAR (all services within a service group)
* MAR (globally engage and disengage)
* ZIP (WSO2 Connector)


## Configuration

To use cargo-wso2-container you will need to add cargo plugin to your project add cargo-wso2-container as a dependency to cargo.

### Add cargo and cargo-wso2-container as a dependency

Update your pom to use cargo plugin and cargo-wso2-container as a dependency:

```xml

    <build>
      <plugins>
        <plugin>
          <groupId>org.codehaus.cargo</groupId>
          <artifactId>cargo-maven2-plugin</artifactId>
          <version>1.4.10</version>
          <dependencies>
            <dependency>
              <groupId>com.tsystems.cargo.wso2</groupId>
              <artifactId>cargo-wso2-container</artifactId>
              <version>0.1.0</version>
            </dependency>
        </plugin>
      </plugins>
    </build>

```

### Supported Runtime Configuration Properties (Remote Container)

Property name | Default value
---: | ---
cargo.protocol | http
cargo.hostname | 127.0.0.1
cargo.servlet.port | 9763
cargo.remote.username | admin
cargo.remote.password | admin
cargo.remote.uri | ${cargo.protocol}://${cargo.hostname}:${cargo.servlet.port}

If configured cargo.remote.uri is preferred.


### Supported Existing Local Configuration Properties (Existing Container)

Property name | Default value
---: | ---
cargo.protocol | http
cargo.hostname | 127.0.0.1
cargo.servlet.port | 9763
cargo.port.offset | 0
cargo.java.home | JAVA_HOME
cargo.process.spawn | false
cargo.rmi.port | 9999
cargo.remote.username | admin
cargo.remote.password | admin
cargo.jvmargs | N/A
cargo.runtime.args | N/A

When a container is started cargo.protocol, cargo.hostname and cargo.servlet.port are used to check if the containers Carbon Management Console is up and running.
The WSO2 Carbon container is stopped via JMX management bean invocation. Therefore cargo.rmi.port, cargo.remote.username and cargo.remote.password can be configured.

Use cargo.port.offset to shift all ports by the given integer value.
See [Default Ports of WSO2 Products](https://docs.wso2.com/display/Carbon420/Default+Ports+of+WSO2+Products)


#### Supported Local Configuration Properties (Standalone Container)

Property name | Default value | Description
---: | --- | ---
cargo.wso2carbon.contextroot | / | (context root of WSO2 Carbon Management Console)
cargo.wso2carbon.serverroles | N/A | Additional server roles (comma separated)

In addition to the aforementioned properties, this container configuration can also set up datasources and/or resources.
For more details, please read: [DataSource and Resource Support](http://cargo.codehaus.org/DataSource+and+Resource+Support).
The JDBC driver jar file will be looked up from Maven dependencies by the classname and gets copied to local installation.


## Examples

#### Remote WSO2 Carbon container with HTTPS and versioned WAR deployable.

```xml

    <plugin>
      <groupId>org.codehaus.cargo</groupId>
      <artifactId>cargo-maven2-plugin</artifactId>
      <version>1.4.10</version>
      <dependencies>
        <dependency>
          <groupId>com.tsystems.cargo.wso2</groupId>
          <artifactId>cargo-wso2-container</artifactId>
          <version>0.1.0</version>
        </dependency>
      </dependencies>
      <configuration>
        <container>
          <containerId>wso2carbon4x</containerId>
          <type>remote</type>
        </container>
        <configuration>
          <type>runtime</type>
          <properties>
            <cargo.protocol>https</cargo.protocol>
            <cargo.servlet.port>9443</cargo.servlet.port>
          </properties>
        </configuration>
        <deployables>
          <deployable>
            <groupId>${project.groupId}</groupId>
            <artifactId>${project.artifactId}</artifactId>
            <type>war</type>
            <properties>
              <context>resource</context>
              <version>1.3</version>
            </properties>
          </deployable>
        </deployables>
      </configuration>
    </plugin>

```

#### Remote WSO2 Carbon container with URI, CAR deployable and Ping. The plugin execution is bound to pre-integration-test phase.

```xml

    <plugin>
      <groupId>org.codehaus.cargo</groupId>
      <artifactId>cargo-maven2-plugin</artifactId>
      <version>1.4.10</version>
      <dependencies>
        <dependency>
          <groupId>com.tsystems.cargo.wso2</groupId>
          <artifactId>cargo-wso2-container</artifactId>
          <version>0.1.0</version>
        </dependency>
      </dependencies>
      <executions>
        <execution>
          <id>deploy-car</id>
          <phase>pre-integration-test</phase>
          <goals>
            <goal>redeploy</goal>
          </goals>
          <configuration>
            <container>
              <containerId>wso2carbon4x</containerId>
              <type>remote</type>
            </container>
            <configuration>
              <type>runtime</type>
              <properties>
                <cargo.remote.uri>https://company.domain.tld/mgmt/</cargo.remote.uri>
              </properties>
            </configuration>
            <deployables>
              <deployable>
                <groupId>${project.groupId}</groupId>
                <artifactId>${project.artifactId}</artifactId>
                <type>carbon/application</type>
                <pingURL>https://company.domain.tld/appcontext/metrics/healthcheck</pingURL>
                <pingTimeout>120000</pingTimeout>
              </deployable>
            </deployables>
          </configuration>
        </execution>
      </executions>
    </plugin>

```

#### Local WSO2 Carbon container installation with artifact installer, port offset, context of WSO2 Carbon Management Console and JDBC datasource.

```xml

    <plugin>
      <groupId>org.codehaus.cargo</groupId>
      <artifactId>cargo-maven2-plugin</artifactId>
      <version>1.4.10</version>
      <dependencies>
        <dependency>
          <groupId>com.tsystems.cargo.wso2</groupId>
          <artifactId>cargo-wso2-container</artifactId>
          <version>0.1.0</version>
        </dependency>
      </dependencies>
      <configuration>
        <container>
          <containerId>wso2carbon4x</containerId>
          <dependencies>
            <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <classpath>shared</classpath>
            </dependency>
          </dependencies>
          <artifactInstaller>
            <!--
            The WSO2 public maven repository had to be setup in order to use artifactInstaller.
            -->
            <groupId>org.wso2.appserver</groupId>
            <artifactId>wso2as</artifactId>
            <version>5.2.0</version>
          </artifactInstaller>
        </container>
        <configuration>
          <type>standalone</type>
          <properties>
            <cargo.port.offset>1</cargo.port.offset>
            <cargo.wso2carbon.contextroot>/mgmt</cargo.wso2carbon.contextroot>
            <cargo.wso2carbon.serverroles>role1,role2</cargo.wso2carbon.serverroles>
            <cargo.datasource.datasource.mydatasource>
              cargo.datasource.driver=com.mysql.jdbc.Driver|
              cargo.datasource.url=jdbc:mysql://localhost:3306/mydatabase|
              cargo.datasource.jndi=jdbc/myjndiname|
              cargo.datasource.username=me|
              cargo.datasource.password=secret
            </cargo.datasource.datasource.mydatasource>
          </properties>
        </configuration>
      </configuration>
  </plugin>

```


