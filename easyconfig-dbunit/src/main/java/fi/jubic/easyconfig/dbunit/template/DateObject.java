package fi.jubic.easyconfig.dbunit.template;

import java.util.Date;
import java.util.HashMap;

public class DateObject extends HashMap<String, Object> {
    public DateObject(Date now) {
        super();
        put("now", DateMethodUtil.toTimestamp(DateMethodUtil.convertDate(now)));
        put("add", new DateAddMethod(now));
        put("sub", new DateSubtractMethod(now));
        put("year", "year");
        put("month", "month");
        put("week", "week");
        put("day", "day");
        put("hour", "hour");
        put("min", "min");
        put("sec", "sec");
    }
}
