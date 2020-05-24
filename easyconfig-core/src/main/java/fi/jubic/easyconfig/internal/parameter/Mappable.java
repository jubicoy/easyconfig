package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.providers.EnvProvider;

public interface Mappable<T> {
    /**
     * Initialize the parameter using the given {@code EnvProvider}. The return value is wrapped in
     * a error message carrying wrapper.
     *
     * @param prefix variable namespace prefix
     * @param envProvider the provider to be used
     * @return the result
     */
    Result<T> initialize(String prefix, EnvProvider envProvider);
}
