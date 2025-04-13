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
 * the License --.
 */

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.Token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token class to parse a size string (like "10MB", "1.5GB") into bytes.
 */
public class ParseByteSize extends Text {

  private static final long serialVersionUID = 1L;

  // Pattern to match sizes (including decimals)
  private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(B|KB|MB|GB|TB|PB)",
      Pattern.CASE_INSENSITIVE);

  public ParseByteSize(String value) {
    super(value);
  }

  @Override
  public String toString() {
    return Long.toString(parseSizeToBytes(super.value));
  }

  /**
   * Parse a size string like "10MB" or "1.5GB" into bytes.
   * 
   * @param sizeStr The size string (e.g., "10MB", "1.5GB")
   * @return The corresponding number of bytes.
   */
  public static long parseSizeToBytes(String sizeStr) {
    // Check for empty or null input
    if (sizeStr == null || sizeStr.trim().isEmpty()) {
      throw new IllegalArgumentException("Size string cannot be empty or null.");
    }

    // Remove any extra spaces between the value and the unit
    sizeStr = sizeStr.trim().replaceAll("\\s+", " "); // Replace multiple spaces with a single one

    // Match the pattern
    Matcher matcher = SIZE_PATTERN.matcher(sizeStr);

    // Check if the pattern matches
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid size format: " + sizeStr);
    }

    // Parse the numeric value and unit
    double value;
    try {
      value = Double.parseDouble(matcher.group(1)); // Value of the size (e.g., 10, 1.5)
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid numeric value in size: " + sizeStr);
    }

    // Handle the case where the value is zero
    if (value == 0) {
      return 0;
    }

    String unit = matcher.group(2).toUpperCase(); // Unit (e.g., KB, MB)

    // Convert based on the unit
    switch (unit) {
      case "B": // Bytes
        return (long) value;
      case "KB": // Kilobytes
        return (long) (value * 1024);
      case "MB": // Megabytes
        return (long) (value * 1024 * 1024);
      case "GB": // Gigabytes
        return (long) (value * 1024 * 1024 * 1024);
      case "TB": // Terabytes
        return (long) (value * 1024L * 1024L * 1024L * 1024L);
      case "PB": // Petabytes
        return (long) (value * 1024L * 1024L * 1024L * 1024L * 1024L);
      default:
        throw new IllegalArgumentException("Unsupported unit: " + unit);
    }
  }

  @Override
  public Token clone() {
    return new ParseByteSize(super.value);
  }
}
