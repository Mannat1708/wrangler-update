package io.cdap.wrangler.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDuration extends Token {
    private static final Pattern PATTERN = Pattern.compile("(?i)(\\d+(\\.\\d+)?)\\s*(ms|s|m|h)");
    private final long milliseconds;

    public TimeDuration(String value) {
        super(value); // Ensure Token class supports this constructor
        Matcher matcher = PATTERN.matcher(value.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time duration format: " + value);
        }

        double number = Double.parseDouble(matcher.group(1)); // Extract number part
        String unit = matcher.group(3).toLowerCase(); // Extract time unit (ms, s, m, h)

        // Convert to milliseconds based on the unit
        switch (unit) {
            case "ms":
                milliseconds = (long) number;
                break;
            case "s":
                milliseconds = (long) (number * 1000);
                break;
            case "m":
                milliseconds = (long) (number * 60 * 1000);
                break;
            case "h":
                milliseconds = (long) (number * 60 * 60 * 1000);
                break;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }

    public long getMilliseconds() {
        return milliseconds;
    }
}
