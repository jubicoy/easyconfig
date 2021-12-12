package fi.jubic.easyconfig.jdbc;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class TestAppender extends AppenderBase<ILoggingEvent> {
    static List<ILoggingEvent> events = new ArrayList<>();

    @Override
    public String getName() {
        return "TestAppender";
    }

    @Override
    protected void append(ILoggingEvent loggingEvent) {
        events.add(loggingEvent);
    }

    static void clear() {
        events.clear();
    }
}
