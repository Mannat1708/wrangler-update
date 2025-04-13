package io.cdap.wrangler.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteSize extends Token {
    private static final Pattern PATTERN = Pattern.compile("(?i)(\\d+(\\.\\d+)?)\\s*(B|KB|MB|GB|TB)");
    private final long bytes;

    public ByteSize(String value) {
        super(value); // Ensure Token class supports this constructor
        Matcher matcher = PATTERN.matcher(value.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid byte size format: " + value);
        }

        double number = Double.parseDouble(matcher.group(1)); // Parse number part
        String unit = matcher.group(3).toUpperCase(); // Unit (B, KB, MB, etc.)

        // Convert to bytes based on the unit
        switch (unit) {
            case "B":
                bytes = (long) number;
                break;
            case "KB":
                bytes = (long) (number * 1024);
                break;
            case "MB":
                bytes = (long) (number * 1024 * 1024);
                break;
            case "GB":
                bytes = (long) (number * 1024 * 1024 * 1024);
                break;
            case "TB":
                bytes = (long) (number * 1024L * 1024L * 1024L * 1024L);
                break;
            default:
                throw new IllegalArgumentException("Unsupported byte unit: " + unit);
        }
    }

    public long getBytes() {
        return bytes;
    }
}
