package fi.jubic.easyconfig.jooq;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.jooq.conf.BackslashEscaping;
import org.jooq.conf.ExecuteWithoutWhere;
import org.jooq.conf.ParamCastMode;
import org.jooq.conf.ParamType;
import org.jooq.conf.ParseUnknownFunctions;
import org.jooq.conf.ParseUnsupportedSyntax;
import org.jooq.conf.ParseWithMetaLookups;
import org.jooq.conf.RenderKeywordStyle;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.conf.StatementType;
import org.jooq.conf.ThrowExceptions;

public class JooqSettings extends Settings {
    @Override
    @EasyConfigProperty(
            value = "RENDER_CATALOG",
            defaultValue = "true"
    )
    public void setRenderCatalog(Boolean value) {
        super.setRenderCatalog(value);
    }

    @Override
    @EasyConfigProperty(
            value = "RENDER_SCHEMA",
            defaultValue = "true"
    )
    public void setRenderSchema(Boolean value) {
        super.setRenderSchema(value);
    }

    // setRenderMapping

    @EasyConfigProperty(
            value = "RENDER_NAME_STYLE",
            defaultValue = "QUOTED"
    )
    public void setRenderNameStyle(String value) {
        super.setRenderNameStyle(RenderNameStyle.fromValue(value));
    }

    @EasyConfigProperty(
            value = "RENDER_KEYWORD_STYLE",
            defaultValue = "AS_IS"
    )
    public void setRenderKeywordStyle(String value) {
        super.setRenderKeywordStyle(RenderKeywordStyle.fromValue(value));
    }

    @Override
    @EasyConfigProperty(
            value = "RENDER_FORMATTED",
            defaultValue = "false"
    )
    public void setRenderFormatted(Boolean value) {
        super.setRenderFormatted(value);
    }

    // setRenderFormatting

    @Override
    @EasyConfigProperty(
            value = "RENDER_SCALAR_SUBQUERIES_FOR_STORED_FUNCTIONS",
            defaultValue = "false"
    )
    public void setRenderScalarSubqueriesForStoredFunctions(Boolean value) {
        super.setRenderScalarSubqueriesForStoredFunctions(value);
    }

    @Override
    @EasyConfigProperty(
            value = "RENDER_ORDER_BY_ROWNUMBER_FOR_EMULATED_PAGINATION",
            defaultValue = "true"
    )
    public void setRenderOrderByRownumberForEmulatedPagination(Boolean value) {
        super.setRenderOrderByRownumberForEmulatedPagination(value);
    }

    @EasyConfigProperty(
            value = "BACKSLASH_ESCAPING",
            defaultValue = "DEFAULT"
    )
    public void setBackslashEscaping(String value) {
        super.setBackslashEscaping(BackslashEscaping.fromValue(value));
    }

    @EasyConfigProperty(
            value = "PARAM_TYPE",
            defaultValue = "INDEXED"
    )
    public void setParamType(String value) {
        super.setParamType(ParamType.fromValue(value));
    }

    @EasyConfigProperty(
            value = "PARAM_CAST_MODE",
            defaultValue = "DEFAULT"
    )
    public void setParamCastMode(String value) {
        super.setParamCastMode(ParamCastMode.fromValue(value));
    }

    @EasyConfigProperty(
            value = "STATEMENT_TYPE",
            defaultValue = "PREPARED_STATEMENT"
    )
    public void setStatementType(String value) {
        super.setStatementType(StatementType.fromValue(value));
    }

    @Override
    @EasyConfigProperty(
            value = "EXECUTE_LOGGING",
            defaultValue = "true"
    )
    public void setExecuteLogging(Boolean value) {
        super.setExecuteLogging(value);
    }

    @Override
    @EasyConfigProperty(
            value = "EXECUTE_WITH_OPTIMISTIC_LOCKING",
            defaultValue = "false"
    )
    public void setExecuteWithOptimisticLocking(Boolean value) {
        super.setExecuteWithOptimisticLocking(value);
    }

    @Override
    @EasyConfigProperty(
            value = "EXECUTE_WITH_OPTIMISTIC_LOCKING_EXCLUDE_UNVERSIONED",
            defaultValue = "false"
    )
    public void setExecuteWithOptimisticLockingExcludeUnversioned(Boolean value) {
        super.setExecuteWithOptimisticLockingExcludeUnversioned(value);
    }

    @Override
    @EasyConfigProperty(
            value = "ATTACH_RECORDS",
            defaultValue = "true"
    )
    public void setAttachRecords(Boolean value) {
        super.setAttachRecords(value);
    }

    @Override
    @EasyConfigProperty(
            value = "UPDATABLE_PRIMARY_KEYS",
            defaultValue = "false"
    )
    public void setUpdatablePrimaryKeys(Boolean value) {
        super.setUpdatablePrimaryKeys(value);
    }

    @Override
    @EasyConfigProperty(
            value = "REFLECTION_CACHING",
            defaultValue = "true"
    )
    public void setReflectionCaching(Boolean value) {
        super.setReflectionCaching(value);
    }

    @Override
    @EasyConfigProperty(
            value = "CACHE_RECORD_MAPPERS",
            defaultValue = "true"
    )
    public void setCacheRecordMappers(Boolean value) {
        super.setCacheRecordMappers(value);
    }

    @EasyConfigProperty(
            value = "THROW_EXCEPTIONS",
            defaultValue = "THROW_ALL"
    )
    public void setThrowExceptions(String value) {
        super.setThrowExceptions(ThrowExceptions.fromValue(value));
    }

    @Override
    @EasyConfigProperty(
            value = "FETCH_WARNINGS",
            defaultValue = "true"
    )
    public void setFetchWarnings(Boolean value) {
        super.setFetchWarnings(value);
    }

    @Override
    @EasyConfigProperty(
            value = "FETCH_SERVER_OUTPUT_SIZE",
            defaultValue = "0"
    )
    public void setFetchServerOutputSize(Integer value) {
        super.setFetchServerOutputSize(value);
    }

    @Override
    @EasyConfigProperty(
            value = "RETURN_ALL_ON_UPDATABLE_RECORD",
            defaultValue = "false"
    )
    public void setReturnAllOnUpdatableRecord(Boolean value) {
        super.setReturnAllOnUpdatableRecord(value);
    }

    @Override
    @EasyConfigProperty(
            value = "RETURN_RECORD_TO_POJO",
            defaultValue = "true"
    )
    public void setReturnRecordToPojo(Boolean value) {
        super.setReturnRecordToPojo(value);
    }

    @Override
    @EasyConfigProperty(
            value = "MAP_JPA_ANNOTATIONS",
            defaultValue = "true"
    )
    public void setMapJPAAnnotations(Boolean value) {
        super.setMapJPAAnnotations(value);
    }

    @Override
    @EasyConfigProperty(
            value = "MAP_CONSTRUCTOR_PARAMETER_NAMES",
            defaultValue = "false"
    )
    public void setMapConstructorParameterNames(Boolean value) {
        super.setMapConstructorParameterNames(value);
    }

    @Override
    @EasyConfigProperty(
            value = "QUERY_TIMEOUT",
            defaultValue = "0"
    )
    public void setQueryTimeout(Integer value) {
        super.setQueryTimeout(value);
    }

    @Override
    @EasyConfigProperty(
            value = "MAX_ROWS",
            defaultValue = "0"
    )
    public void setMaxRows(Integer value) {
        super.setMaxRows(value);
    }

    @Override
    @EasyConfigProperty(
            value = "FETCH_SIZE",
            defaultValue = "0"
    )
    public void setFetchSize(Integer value) {
        super.setFetchSize(value);
    }

    @Override
    @EasyConfigProperty(
            value = "DEBUG_INFO_ON_STACK_TRACE",
            defaultValue = "true"
    )
    public void setDebugInfoOnStackTrace(Boolean value) {
        super.setDebugInfoOnStackTrace(value);
    }

    @Override
    @EasyConfigProperty(
            value = "IN_LIST_PADDING",
            defaultValue = "false"
    )
    public void setInListPadding(Boolean value) {
        super.setInListPadding(value);
    }

    @Override
    @EasyConfigProperty(
            value = "IN_LIST_PAD_BASE",
            defaultValue = "2"
    )
    public void setInListPadBase(Integer value) {
        super.setInListPadBase(value);
    }

    @Override
    @EasyConfigProperty(
            value = "DELIMITER",
            defaultValue = ";"
    )
    public void setDelimiter(String value) {
        super.setDelimiter(value);
    }

    @Override
    @EasyConfigProperty(
            value = "EMULATE_ON_DUPLICATE_KEY_UPDATE_ON_PRIMARY_KEY_ONLY",
            defaultValue = "false"
    )
    public void setEmulateOnDuplicateKeyUpdateOnPrimaryKeyOnly(Boolean value) {
        super.setEmulateOnDuplicateKeyUpdateOnPrimaryKeyOnly(value);
    }

    @EasyConfigProperty(
            value = "EXECUTE_UPDATE_WITHOUT_WHERE",
            defaultValue = "LOG_DEBUG"
    )
    public void setExecuteUpdateWithoutWhere(String value) {
        super.setExecuteUpdateWithoutWhere(ExecuteWithoutWhere.fromValue(value));
    }

    @EasyConfigProperty(
            value = "EXECUTE_DELETE_WITHOUT_WHERE",
            defaultValue = "LOG_DEBUG"
    )
    public void setExecuteDeleteWithoutWhere(String value) {
        super.setExecuteDeleteWithoutWhere(ExecuteWithoutWhere.fromValue(value));
    }

    @EasyConfigProperty(
            value = "PARSE_WITH_META_LOOKUPS",
            defaultValue = "IGNORE_ON_FAILURE"
    )
    public void setParseWithMetaLookups(String value) {
        super.setParseWithMetaLookups(ParseWithMetaLookups.fromValue(value));
    }

    @EasyConfigProperty(
            value = "PARSE_UNSUPPORTED_SYNTAX",
            defaultValue = "IGNORE"
    )
    public void setParseUnsupportedSyntax(String value) {
        super.setParseUnsupportedSyntax(ParseUnsupportedSyntax.fromValue(value));
    }

    @EasyConfigProperty(
            value = "PARSE_UNKNOWN_FUNCTIONS",
            defaultValue = "FAIL"
    )
    public void setParseUnknownFunctions(String value) {
        super.setParseUnknownFunctions(ParseUnknownFunctions.fromValue(value));
    }
}
