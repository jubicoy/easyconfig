package fi.jubic.easyconfig.internal;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingContext {
    private final String context;
    private final int indent;

    private MappingContext(
            String context,
            int indent
    ) {
        this.context = context;
        this.indent = indent;
    }

    public MappingContext() {
        this("", 0);
    }

    public MappingContext(String context) {
        this.context = context;
        this.indent = 0;
    }

    public MappingContext(Class<?> propertyClass) {
        this(propertyClass.getCanonicalName(), 0);
    }

    public MappingContext push(String context) {
        return new MappingContext(
                this.context.length() > 0
                        ? this.context + " " + context
                        : context,
                this.indent
        );
    }

    public MappingContext subContext() {
        return subContext("");
    }

    public MappingContext subContext(String context) {
        return new MappingContext(
                context,
                this.indent + 2
        );
    }

    public String format(String message) {
        String indentStr = Stream
                .concat(
                        Stream.iterate(" ", s -> s)
                                .limit(indent),
                        indent > 0 ? Stream.of("- ") : Stream.empty()
                )
                .collect(Collectors.joining());

        if (context.length() > 0) {
            return String.format("%s%s: %s", indentStr, context, message);
        }
        return indentStr + message;
    }
}
