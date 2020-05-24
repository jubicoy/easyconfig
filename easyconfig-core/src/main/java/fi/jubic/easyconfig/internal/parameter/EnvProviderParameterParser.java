package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.providers.EnvProvider;

import java.util.Optional;

public class EnvProviderParameterParser implements ParameterParser {

    @Override
    public Optional<Result<Mappable<?>>> parseParameter(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        return propertyDef.getAnnotatedElement()
                .flatMap(annotatedElement -> Optional.ofNullable(
                        annotatedElement.getAnnotation(EnvProviderProperty.class)
                ))
                .map(annotation -> {
                    if (!EnvProvider.class.isAssignableFrom(propertyDef.getPropertyClass())) {
                        return Result.message(
                                String.format(
                                        "%s: Cannot inject %s as %s",
                                        context,
                                        propertyDef.getPropertyClass().getCanonicalName(),
                                        EnvProvider.class.getCanonicalName()
                                )
                        );
                    }

                    return Result.of(((prefix, envProvider) -> Result.of(envProvider)));
                });
    }
}
