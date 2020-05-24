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
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;

public class ServerConfig {
    private final String host;
    private final int port;

    public ServerConfig(
            @EasyConfigProperty(
                    value = "SERVER_PORT",
                    defaultValue = "0.0.0.0"
            ) String host,
            @ConfigProperty("SERVER_PORT") int port
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
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;

public class ServerConfig {
    private String host;
    private int port;

    public ServerConfig() {

    }

    @ConfigProperty(
            value = "SERVER_HOST",
            defaultValue = "0.0.0.0"
    )
    public void setHost(String host) {
        this.host = host;
    }

    @ConfigProperty(value = "SERVER_PORT", defaultValue = "8080")
    public void setPort(int port) {
        this.port = port;
    }

    // The rest of the owl
}
```

### Injection

Once an annotated class has been defined properties can be injected as shown below. A custom `EnvProvider` can be passed to `ConfigMapper` for testing purposes. However, the configuration object can usually be initialized through the default constructor without much effort.

```java
ServerConfig serverConfig = new ConfigMapper().read(ServerConfig.class);
```

### Nested configuration

The child configuration is annotated as per usual.

```java
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;

public class ServerConfig {
    private String host;
    private int port;

    public ServerConfig() {

    }

    @ConfigProperty(
            value = "HOST",
            defaultValue = "0.0.0.0"
    )
    public void setHost(String host) {
        this.host = host;
    }

    @ConfigProperty(
            value = "PORT",
            defaultValue = "8080"
    )
    public void setPort(int port) {
        this.port = port;
    }

    // The rest of the owl
}
```

Parent config has the child config as a property. Setter or constructor parameter is annotated using `ConfigProperty` with an optional prefix. In this case the injected variables are `SERVER_HOST` and `SERVER_PORT`. Without the prefix `HOST` and `PORT` would be injected instead.

```java
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;

public class AppConfig {
    private ServerConfig serverConfig;

    public AppConfig() {

    }

    @ConfigProperty("SERVER_")
    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
```

### Primitive lists

Lists of primitive values (strings, integers, etc.) can be injected as a semicolon separated list. A custom list separator can also be defined.

```java
package example;


import fi.jubic.easyconfig.annotations.ConfigProperty;
import java.util.List;

public class Configuration {
    private List<Integer> intList;
    private List<String> stringList;
    private List<Integer> anotherIntList;

    // "1;2;3;4"
    @ConfigProperty("INT_LIST")
    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    // Default used if env variable is missing
    @ConfigProperty(
            value = "STRING_LIST_WITH_DEFAULT",
            defaultValue = "a;b;c;d"
    )
    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    // Custom delimiter, "1,2,3,4"
    @ConfigProperty(
            value = "ANOTHER_INT_LIST",
            listDelimiter = ","
    )
    public void setAnotherIntList(List<Integer> anotherIntList) {
        this.anotherIntList = anotherIntList;
    }
}
```

### Nested lists

A configuration can contain a list of configurations as a property. Any regular configuration can be used in a list. `FtpConfig` below is a good example of such config class.

```java
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;

public class FtpConfig {
    final String host;
    final int port;

    public FtpConfig(
            @ConfigProperty("HOST") String host,
            @ConfigProperty("PORT") int port
    ) {
        this.host = host;
        this.port = port;
    }
}
```

Containing config uses `{}` as a placeholder element in the `@ConfigProperty` value to tell the `ConfigMapper` that this value is read in as a list.

```java
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import java.util.List;

public class AppConfig {
    final List<FtpConfig> ftpConfigs;

    public AppConfig(
            @ConfigProperty("FTP_{}_") List<FtpConfig> ftpConfigs
    ) {
        this.ftpConfigs = ftpConfigs;
    }
}
```

Now a list of `FtpConfig` instances can be defined like this:

```bash
FTP_0_HOST=localhost
FTP_0_PORT=21
FTP_2_HOST=otherhost
FTP_2_PORT=21
```

The numbers provided for the placeholder do not have to be sequential as long as there are no duplicates.

## Configuration Extensions

Any configuration object can be extended using `ConfigExtension` annotations. For example, the below configuration uses `LiquibaseExtension` to run migrations on the database connection provided by the `PooledJdbcConfiguration`.

```java
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.extensions.LiquibaseExtension;

public class AppConfig {
    private final JdbcConfiguration jdbcConfig;

    public AppConfig(
            @LiquibaseExtension("migrations.xml")
            @ConfigProperty("") PooledJdbcConfiguration jdbcConfig
    ) {
        this.jdbcConfig = jdbcConfig;
    }
}
```

The extension are defined as a `ConfigExtension` annotated extension annotation and a `ConfigExtensionProvider` class.

```java
package example;

import fi.jubic.easyconfig.extensions.ConfigExtension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation used to extend config properties. The provider that should be
 * used to provide this extension is defined using the ConfigExtension annotation.
 * 
 */
@ConfigExtension(CustomExtensionProvider.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomExtension {
    /**
     * Non-configurable parameters used by the extension.
     */
    String customParam();
}
```

The provider will perform the actual extension operation.

```java
package example;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.extensions.ConfigExtensionProvider;
import fi.jubic.easyconfig.extensions.CustomExtension;
import fi.jubic.easyconfig.jdbc.JdbcConfiguration;

import java.sql.SQLException;
import java.util.Optional;

public class CustomExtensionProvider
        implements ConfigExtensionProvider<CustomExtension, JdbcConfiguration> {
    private final boolean configurableProperty;

    public CustomExtensionProvider(
            @ConfigProperty(value = "CONFIG_PROP") boolean configurableProperty
    ) {
        this.configurableProperty = configurableProperty;
    }

    @Override
    public JdbcConfiguration extend(
            CustomExtension extensionParams,
            JdbcConfiguration configuration
    ) {
        // The extension provider can perform actions on the extended
        // or wrap it into, for example, a wrapper that logs interactions
        // against the config.
    }
}
```


## Dotenv support

Dotenv support is provided via cdimascio/java-dotenv. `.env` file containing environment overrides is searched in current working directory or path provided using `EASYCONFIG_DOTENV_DIR` environment variable.

Current functionality overrides environment variables with the ones provided by the `.env` file. The priority is subject to change in the future.