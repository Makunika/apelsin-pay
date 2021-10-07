package ru.pshiblo.kafka.error.utils;

import lombok.experimental.UtilityClass;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
@UtilityClass
public class HeaderUtils {
    private final String MAX_ATTEMPT_HEADER = "max_attempt";
    private final String ATTEMPT_HEADER = "attempt";
    private final String ERROR_TOPIC_HEADER = "error_topic";
    private final String ALLOW_RESEND_HEADER = "allow_resend";

    public void setMaxAttempt(Headers headers, int maxAttempt) {
        if (maxAttempt < 0) {
            throw new IllegalArgumentException("maxAttempt >= 0!");
        }
        headers.remove(MAX_ATTEMPT_HEADER);
        headers.add(MAX_ATTEMPT_HEADER, Integer.toString(maxAttempt).getBytes());
    }

    public void setErrorTopic(Headers headers, String errorTopic) {
        headers.remove(ERROR_TOPIC_HEADER);
        headers.add(ERROR_TOPIC_HEADER, errorTopic.getBytes());
    }

    public void setAttempt(Headers headers, int attempt) {
        if (attempt < 0) {
            throw new IllegalArgumentException("attempt >= 0!");
        }
        headers.remove(ATTEMPT_HEADER);
        headers.add(ATTEMPT_HEADER, Integer.toString(attempt).getBytes());
    }

    public static boolean isAllowResend(Headers headers) {
        return headers.lastHeader(ALLOW_RESEND_HEADER) != null;
    }

    public static void setAllowResend(Headers headers) {
        headers.add(ALLOW_RESEND_HEADER, "true".getBytes());
    }

    public String readErrorTopic(Headers headers) {
        return readHeader(headers, ERROR_TOPIC_HEADER);
    }

    public int readMaxAttempt(Headers headers) {
        return Integer.parseInt(Objects.requireNonNull(readHeader(headers, MAX_ATTEMPT_HEADER)));
    }

    public int readAttempt(Headers headers) {
        return Integer.parseInt(Optional.ofNullable(readHeader(headers, ATTEMPT_HEADER)).orElse("0"));
    }

    private String readHeader(Headers headers, String headerString) {
        Header header = headers.lastHeader(headerString);
        if (header == null) {
            return null;
        }
        return new String(header.value());
    }
}
