package fi.jubic.easyconfig.internal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Result<T> {
    private final T value;
    private final List<Message> messages;

    private Result(
            T value,
            List<Message> messages
    ) {
        if (value != null && !messages.isEmpty()) {
            throw new IllegalStateException();
        }
        this.value = value;
        this.messages = Collections.unmodifiableList(messages);
    }

    public static <T> Result<T> of(T value) {
        return new Result<>(value, Collections.emptyList());
    }

    public static <T> Result<T> message(String message) {
        return new Result<>(null, Collections.singletonList(new Message(message, null)));
    }

    public static <T> Result<T> message(String message, Throwable throwable) {
        return new Result<>(null, Collections.singletonList(new Message(message, throwable)));
    }

    public static <T> Result<T> message(List<Message> messages) {
        return new Result<>(null, messages);
    }

    public T getValue() {
        return value;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (!messages.isEmpty()) {
            return Result.message(messages);
        }
        return Result.of(mapper.apply(value));
    }

    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (!messages.isEmpty()) {
            return Result.message(messages);
        }
        return mapper.apply(value);
    }

    public Stream<String> getMessagesAsStringStream() {
        return messages.stream().flatMap(Message::toStringStream);
    }

    public static <T> Result<List<T>> unwrap(List<Result<T>> results) {
        if (results.stream().anyMatch(Result::hasMessages)) {
            return results.stream()
                    .map(Result::getMessages)
                    .flatMap(List::stream)
                    .collect(
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Result::message
                            )
                    );
        }

        return results.stream()
                .map(Result::getValue)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                Result::of
                        )
                );
    }

    public static Result<List<?>> unsafeUnwrap(List<Result<?>> results) {
        if (results.stream().anyMatch(Result::hasMessages)) {
            return results.stream()
                    .map(Result::getMessages)
                    .flatMap(List::stream)
                    .collect(
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    Result::message
                            )
                    );
        }

        return results.stream()
                .map(Result::getValue)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                Result::of
                        )
                );
    }

    public static <T> Result<T> unwrapMessages(List<Result<?>> results) {
        return results.stream()
                .peek(result -> {
                    if (!result.hasMessages()) {
                        throw new IllegalArgumentException(
                                "Result has no messages"
                        );
                    }
                })
                .map(Result::getMessages)
                .flatMap(List::stream)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                Result::message
                        )
                );
    }

    public static <T> Result<T> unwrapMessages(Result<?>... results) {
        return unwrapMessages(Arrays.asList(results));
    }

    @SuppressFBWarnings(
            value = "INFORMATION_EXPOSURE_THROUGH_AN_ERROR_MESSAGE",
            justification = "The stacktraces are printed to messages of other throwables "
                    + "making this equivalent to Throwable::printStackTrace()"
    )
    static class Message {
        private final String text;
        @Nullable
        private final Throwable throwable;

        Message(String text, @Nullable Throwable throwable) {
            this.text = text;
            this.throwable = throwable;
        }

        Optional<Throwable> getThrowable() {
            return Optional.ofNullable(throwable);
        }

        Stream<String> toStringStream() {
            return Stream
                    .of(
                            Stream.of(text),
                            getThrowable()
                                    .map(t -> {
                                        StringWriter writer = new StringWriter();
                                        t.printStackTrace(new PrintWriter(writer));
                                        return Stream.of(writer.toString()
                                                .split(System.lineSeparator()))
                                                .map(row -> "    " + row);
                                    })
                                    .orElseGet(Stream::empty)
                    )
                    .flatMap(Function.identity());
        }
    }
}
