/*
 * Copyright © 2024 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * the License for the specific language governing permissions and limitations under
 * the License--.
 */

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.Token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token class to parse a duration string (like "10s", "1m", "2h", "1d") into
 * milliseconds.
 */
public class ParseDuration extends Text {

  private static final long serialVersionUID = 1L;

  // Regular expression to match the duration (supports both integer and decimal
  // values)
  private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(s|m|h|d)",
      Pattern.CASE_INSENSITIVE);

  public ParseDuration(String value) {
    super(value);
  }

  @Override
  public String toString() {
    return Long.toString(parseDurationToMillis(super.value));
  }

  /**
   * Parse a duration string like "10s", "1m", "2h", "1d" into milliseconds.
   *
   * @param durationStr The duration string (e.g., "10s", "1m", "2h", "1d")
   * @return The corresponding duration in milliseconds.
   */
  public static long parseDurationToMillis(String durationStr) {
    // Check for empty or null input
    if (durationStr == null || durationStr.trim().isEmpty()) {
      throw new IllegalArgumentException("Duration string cannot be empty or null.");
    }

    Matcher matcher = DURATION_PATTERN.matcher(durationStr.trim());

    // Check if the pattern matches
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid duration format: " + durationStr);
    }

    // Parse the numeric value and the time unit (e.g., s, m, h, d)
    double value;
    try {
      value = Double.parseDouble(matcher.group(1));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid numeric value in duration: " + durationStr);
    }

    // Handle the case where the value is zero
    if (value == 0) {
      return 0;
    }

    String unit = matcher.group(2).toUpperCase(); // Convert unit to uppercase for consistency

    // Convert based on the unit
    switch (unit) {
      case "S": // Seconds
        return (long) (value * 1000); // Convert seconds to milliseconds
      case "M": // Minutes
        return (long) (value * 60 * 1000); // Convert minutes to milliseconds
      case "H": // Hours
        return (long) (value * 60 * 60 * 1000); // Convert hours to milliseconds
      case "D": // Days
        return (long) (value * 24 * 60 * 60 * 1000); // Convert days to milliseconds
      default:
        throw new IllegalArgumentException("Unsupported unit: " + unit);
    }
  }

  @Override
  public Token clone() {
    return new ParseDuration(super.value);
  }
}
