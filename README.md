[![Build Status](https://travis-ci.org/hschott/cargo-wso2-container.svg?branch=master)](https://travis-ci.org/hschott/cargo-wso2-container)

# Introduction

This project is a [Cargo](https://codehaus-cargo.github.io/cargo/Home) container implementation for [WSO2 Carbon](http://www.wso2.com).
It provides capabilities to deploy WSO2 applications to installed and remote WSO2 Carbon servers.

This document will focus on [Maven](http://maven.apache.org/) integration.
For more details about configuring Cargo visit please [Cargo](https://codehaus-cargo.github.io/cargo/) website.

## Supported Container Features

*The cargo-wso2-container supports WSO2 Carbon 4.x*

Container ID: wso2carbon4x

* [Container Instantiation](https://codehaus-cargo.github.io/cargo/Container+Instantiation)
* [Local Container](https://codehaus-cargo.github.io/cargo/Local+Container)
  * [Container Classpath](https://codehaus-cargo.github.io/cargo/Container+Classpath)
  * [Container Start](https://codehaus-cargo.github.io/cargo/Container+Start)
  * [Container Stop](https://codehaus-cargo.github.io/cargo/Container+Stop)
  * [Container Timeout](https://codehaus-cargo.github.io/cargo/Container+Timeout)
  * [Installed Container](https://codehaus-cargo.github.io/cargo/Installed+Container)
    * [Installer](https://codehaus-cargo.github.io/cargo/Installer)
    * [Passing System Properties](https://codehaus-cargo.github.io/cargo/Passing+system+properties)
* [Remote Container](https://codehaus-cargo.github.io/cargo/Remote+Container)

## Unsupported Features

* [Embedded Container](https://codehaus-cargo.github.io/cargo/Embedded+Container)

## Supported Configuration Feature

* [Standalone Local Configuration for installed container](https://codehaus-cargo.github.io/cargo/Standalone+Local+Configuration)
* [Existing Local Configuration for installed container](https://codehaus-cargo.github.io/cargo/Existing+Local+Configuration)
* [Runtime Configuration](https://codehaus-cargo.github.io/cargo/Runtime+Configuration)

## Supported Deployer Features

Static deployment and un-deployment of:

* [WAR](https://codehaus-cargo.github.io/cargo/Static+deployment+of+WAR)
* [Expanded WAR](https://codehaus-cargo.github.io/cargo/Static+deployment+of+expanded+WAR),
* CAR (Carbon Application)
* AAR (Axis2 Service)
* MAR (Axis2 Module)
* ZIP (WSO2 ESB Connector)
* TBOX (WSO2 BAM Toolbox)

Static deployment does a simple copy of the deployable file to Carbon server repository, which defaults to `${carbon_home}/repository/deployment/server`. And vice versa undeployment removes the deployable file.

Remote deployment and un-deployment of:

* WAR
* CAR
* AAR 
* MAR
* ZIP (WSO2 ESB Connector)
* TBOX

For deployables of type AAR, MAR, TBOX and CAR an `<applicationName/>` under which the deployable resides on the server can be configured.
For deployables of type WAR a `<context/>` and `<version/>` can be configured.

For every remote deployable a property `<deployTimeout/>` in milliseconds can be configured, which enables a check if the artifact is successfully deployed on or undeployed from the server.

Remote start and stop of:

* WAR
* AAR (all services within a service group)
* MAR (globally engage and disengage)
* ZIP

Remote deployment/undeployment, start/stop and check for artifact is done via calls to [WSO2 Admin Services](https://docs.wso2.com/display/shared/Calling+Admin+Services+from+Apps).


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
              <version>${cargo.wso2.container.version}</version>
            </dependency>
          </dependencies>
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
cargo.remote.username | N/A
cargo.remote.password | N/A
cargo.wso2carbon.username | admin
cargo.wso2carbon.password | admin
cargo.remote.uri | ${cargo.protocol}://${cargo.hostname}:${cargo.servlet.port}

If configured `cargo.remote.uri` is preferred.

#### Authentication
Use `cargo.remote.username` and `cargo.remote.password` to authenticate against an HTTP server in front of WSO2 Carbon Mangement Console. Use `cargo.wso2carbon.username` and `cargo.wso2carbon.password` to authenticate against the WSO2 Carbon Mangement Console.

#### Watch for the deployable

With Cargo it is possible to watch for a successful deployment by pinging a URL on the server. This is standard Cargo behavior but not usefull for every type of WSO2 deployable. 
Therefore an additional property `deployTimeout` can be used to enable a check for successful deployment or undeployment of an deployable. Setting this property to a value larger then 0 will cause Cargo to wait for success for that amount of milliseconds.


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
cargo.wso2carbon.username | admin
cargo.wso2carbon.password | admin
cargo.jvmargs | N/A
cargo.runtime.args | N/A

When a container is started `cargo.protocol`, `cargo.hostname` and `cargo.servlet.port` are used to check if the containers Carbon Management Console is up and running.

The WSO2 Carbon container is stopped via JMX management bean invocation. Therefore `cargo.rmi.port`, `cargo.wso2carbon.username` and `cargo.wso2carbon.password` can be configured.

Use `cargo.port.offset` to shift all ports by the given integer value.
See [Default Ports of WSO2 Products](https://docs.wso2.com/display/Carbon420/Default+Ports+of+WSO2+Products)


### Supported Local Configuration Properties (Standalone Container)

Property name | Default value | Description
---: | --- | ---
cargo.wso2carbon.contextroot | / | (context root of WSO2 Carbon Management Console)
cargo.wso2carbon.serverroles | N/A | Additional server roles (comma separated)

In addition to the aforementioned properties, this container configuration can also set up datasources and/or resources.
For more details, please read: [DataSource and Resource Support](https://codehaus-cargo.github.io/cargo/DataSource+and+Resource+Support).
The JDBC driver jar file will be looked up from the container shared classpath by the classname and gets copied to local installation.


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
          <version>${cargo.wso2.container.version}</version>
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

#### Remote WSO2 Carbon container with URI, CAR deployable and deployTimeout. The plugin execution is bound to pre-integration-test phase.

```xml

    <plugin>
      <groupId>org.codehaus.cargo</groupId>
      <artifactId>cargo-maven2-plugin</artifactId>
      <version>1.4.10</version>
      <dependencies>
        <dependency>
          <groupId>com.tsystems.cargo.wso2</groupId>
          <artifactId>cargo-wso2-container</artifactId>
          <version>${cargo.wso2.container.version}</version>
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
                <properties>
                  <deployTimeout>120000</deployTimeout>
                </properties>
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
          <version>${cargo.wso2.container.version}</version>
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
##### License

```txt

    Copyright {2014, 2015} Holger Balow-Schott
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use these files except in compliance with the License.
    You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

```
