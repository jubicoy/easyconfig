package fi.jubic.easyconfig.snoozy;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.snoozy.swagger.SwaggerConfig;

public class SnoozySwaggerConfig implements SwaggerConfig {
    private final boolean alwaysServerOpenApi;
    private final boolean alwaysServeSwaggerUi;

    public SnoozySwaggerConfig(
            @ConfigProperty(
                    value = "ALWAYS_SERVE_OPENAPI",
                    defaultValue = "false"
            ) boolean alwaysServeOpenApi,
            @ConfigProperty(
                    value = "ALWAYS_SERVE_SWAGGER_UI",
                    defaultValue = "false"
            ) boolean alwaysServeSwaggerUi
    ) {
        this.alwaysServerOpenApi = alwaysServeOpenApi;
        this.alwaysServeSwaggerUi = alwaysServeSwaggerUi;
    }

    @Override
    public boolean alwaysServeOpenApi() {
        return alwaysServerOpenApi;
    }

    @Override
    public boolean alwaysServeSwaggerUi() {
        return alwaysServeSwaggerUi;
    }
}
