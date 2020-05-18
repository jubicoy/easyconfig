# easyconfig-logback

Extend `logback.xml` dynamically using environment variables.

## Features

* Change log level of any logger. Including the root logger.
* Add appenders to any logger. The appenders need to be already defined and attached to a dummy logger to retain them.
* Add new loggers.
* Use environment variables provided by custom `EnvProvider` implementation in `logback.xml`.

## Usage

Initializing an instance of `LogbackConfig` will configure the global `LoggerContext` to include configuration provided through the environment.

A basic `logback.xml` is required as a base configuration. The following configuration defines a `console` appender attached to the root logger, `named-logger` and a `custom` appender attached to `dummy` logger. Logback discards all appenders that are not attached to loggers. 

```xml
<configuration>
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %-5level %d{yyyy-MM-dd HH:mm:ss.SSS} %msg%n
            </pattern>
        </encoder>
    </appender>
    <appender name="custom" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %-5level %d{yyyy-MM-dd HH:mm:ss.SSS} %msg%n
            </pattern>
        </encoder>
    </appender>

    <root level="WARN">
        <appender-ref ref="console" />
    </root>

    <!-- A dummy logger to retain the logger reference -->
    <logger name="dummy" level="OFF">
        <appender-ref ref="custom" />
    </logger>

    <logger name="named-logger" level="WARN" />
</configuration>
```

Once the config is in place, `LogbackConfig` needs to be initialized using `ConfigMapper`.

```java
new ConfigMapper().read(LogbackConfig.class);
```

It is also possible to nest the `LogbackConfig` into the application's main configuration.

```java
public MainConfiguration(
        @ConfigProperty LogbackConfig logbackConfig
) {
    [...]
}
```

Environment variables can now be used to modify the configuration:

```bash
# Change root logger level and add appenders
LOGBACK_ROOT_LEVEL=DEBUG
LOGBACK_ROOT_APPENDER_REFS=custom

# Change named logger level and add appenders
LOGBACK_LOGGER_1_NAME=named-logger
LOGBACK_LOGGER_1_LEVEL=DEBUG
LOGBACK_LOGGER_1_APPENDER_REFS=custom

# Add new loggers
LOGBACK_LOGGER_2_NAME=new-logger
LOGBACK_LOGGER_2_LEVEL=INFO
```
