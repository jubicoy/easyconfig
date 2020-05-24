package fi.jubic.easyconfig.dbunit.template;

import freemarker.template.TemplateModelException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;

class DateMethodUtil {
    static <T> T safeGet(List list, int i) throws TemplateModelException {
        Object o = list.get(i);
        try {
            //noinspection unchecked
            return (T)o;
        }
        catch (ClassCastException ignore) {
            throw new TemplateModelException(
                    String.format("Invalid argument at %d", i)
            );
        }
    }

    static LocalDateTime convertDate(Date date) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault()
        );
    }

    static LocalDateTime add(Date time, int amount, String step) {
        return convertDate(time).plus(amount, parseUnit(step));
    }

    static LocalDateTime subtract(Date time, int amount, String step) {
        return convertDate(time).minus(amount, parseUnit(step));
    }

    static String toTimestamp(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    private static TemporalUnit parseUnit(String unitStr) {
        switch (unitStr) {
            case "year":
                return ChronoUnit.YEARS;
            case "month":
                return ChronoUnit.MONTHS;
            case "week":
                return ChronoUnit.WEEKS;
            default:
            case "day":
                return ChronoUnit.DAYS;
            case "hour":
                return ChronoUnit.HOURS;
            case "min":
                return ChronoUnit.MINUTES;
            case "sec":
                return ChronoUnit.SECONDS;
        }
    }
}
