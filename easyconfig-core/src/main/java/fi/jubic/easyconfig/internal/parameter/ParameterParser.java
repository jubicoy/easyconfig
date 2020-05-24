package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;

import java.util.Optional;

public interface ParameterParser {
    Optional<Result<Mappable<?>>> parseParameter(
            MappingContext context,
            ConfigPropertyDef propertyDef
    );
}
