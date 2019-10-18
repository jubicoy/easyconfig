# EasyConfig

[![Build Status](https://travis-ci.org/jubicoy/easyconfig.svg?branch=master)](https://travis-ci.org/jubicoy/easyconfig)

Configure applications using environment variables with sensible
defaults.

## Features

* Inject through constructor or setters.
* Provide default values annotations.
* Compose nested configurations.
* Dotenv support

## Usage

### Constructor injection

Define a constructor and annotate parameters using `@EasyConfigProperty`. Default values can be defined using the `defaultValue` property. Environment variables `SERVER_HOST` and `SERVER_PORT` are assigned to the corresponding fields when the configuration class is read from the environment.

```java
package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;

@EasyConfig
public class ServerConfig {
    private final String host;
    private final int port;

    public ServerConfig(
            @EasyConfigProperty(
                    value = "SERVER_PORT",
                    defaultValue = "0.0.0.0"
            ) String host,
            @EasyConfigProperty("SERVER_PORT") int port
    ) {
        this.host = host;
        this.port = port;
    }

    // The rest of the owl
}
```

### Setter injection

Parameter injection can also be accomplished using default constructor and annotated setters. Environment variables `SERVER_HOST` and `SERVER_PORT` are assigned to the corresponding fields when the configuration class is read from the environment.

```java
package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;

@EasyConfig
public class ServerConfig {
    private String host;
    private int port;

    public ServerConfig() {

    }

    @EasyConfigProperty(
            value = "SERVER_HOST",
            defaultValue = "0.0.0.0"
    )
    public void setHost(String host) {
        this.host = host;
    }

    @EasyConfigProperty(value = "SERVER_PORT", defaultValue = "8080")
    public void setPort(int port) {
        this.port = port;
    }

    // The rest of the owl
}
```

### Injection

Once an annotated class has been defined properties can be injected as shown below. A custom `EnvProvider` can be passed to `ConfigMapper` for testing purposes. However, the configuration object can usually be initialized through the default constructor without much effort.

```java
try {
    ServerConfig serverConfig = new ConfigMapper().read(ServerConfig.class);
} catch (MappingException e) {
    e.printStackTrace();
}
```

### Nested configuration

The child configuration is annotated as per usual.

```java
package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;

@EasyConfig
public class ServerConfig {
    private String host;
    private int port;

    public ServerConfig() {

    }

    @EasyConfigProperty(
            value = "HOST",
            defaultValue = "0.0.0.0"
    )
    public void setHost(String host) {
        this.host = host;
    }

    @EasyConfigProperty(value = "PORT", defaultValue = "8080")
    public void setPort(int port) {
        this.port = port;
    }

    // The rest of the owl
}
```

Parent config has the child configas a property. Setter or constructor parameter is annotated using `EasyConfigProperty` with an optional prefix. In this case the injected variables are `SERVER_HOST` and `SERVER_PORT`. Without the prefix `HOST` and `PORT` would be injected instead.

```java
@EasyConfig
public class AppConfig {
    private ServerConfig serverConfig;

    public AppConfig() {

    }

    @EasyConfigProperty("SERVER_")
    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
```

### Primitive lists

Lists of primitive values (strings, integers, etc.) can be injected as a semicolon separated list. A custom list separator can also be defined.

```java
package example;


import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import java.util.List;

public class Configuration {
    private List<Integer> intList;
    private List<String> stringList;
    private List<Integer> anotherIntList;

    // "1;2;3;4"
    @EasyConfigProperty("INT_LIST")
    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    // Default used if env variable is missing
    @EasyConfigProperty(
            value = "STRING_LIST_WITH_DEFAULT",
            defaultValue = "a;b;c;d"
    )
    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    // Custom delimiter, "1,2,3,4"
    @EasyConfigProperty(
            value = "ANOTHER_INT_LIST",
            listDelimiter = ","
    )
    public void setAnotherIntList(List<Integer> anotherIntList) {
        this.anotherIntList = anotherIntList;
    }
}
```

## Dotenv support

Dotenv support is provided via cdimascio/java-dotenv. `.env` file containing environment overrides is searched in current working directory or path provided using `EASYCONFIG_DOTENV_DIR` environment variable.

Current functionality overrides environment variables with the ones provided by the `.env` file. The priority is subject to change in the future.