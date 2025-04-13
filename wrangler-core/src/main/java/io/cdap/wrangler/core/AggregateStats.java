package io.cdap.wrangler.core;

import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.ExecutionContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;

public class AggregateStats implements Directive {

    // Define column names for byte size and time duration
    private String byteSizeColumn;
    private String timeDurationColumn;
    private String totalSizeColumn;
    private String totalTimeColumn;

    // Running totals for aggregation
    private long totalSize = 0;
    private long totalTime = 0;

    @Override
    public void define() {
        // Define the columns required by this directive
        byteSizeColumn = "data_transfer_size"; // Example: Column with byte size values
        timeDurationColumn = "response_time"; // Example: Column with time duration values
        totalSizeColumn = "total_size_mb"; // Target column for total size (MB)
        totalTimeColumn = "total_time_sec"; // Target column for total time (seconds)
    }

    @Override
    public void initialize(ExecutionContext context) {
        // Reset the running totals when the execution starts
        totalSize = 0;
        totalTime = 0;
    }

    @Override
    public void execute(Row row) {
        // Retrieve the byte size and time duration values from each row
        ByteSize byteSize = (ByteSize) row.getValue(byteSizeColumn);
        TimeDuration timeDuration = (TimeDuration) row.getValue(timeDurationColumn);

        // Convert the values to canonical units (bytes and nanoseconds)
        if (byteSize != null && timeDuration != null) {
            totalSize += byteSize.getBytes(); // Add byte size to total size
            totalTime += timeDuration.getMilliseconds(); // Add time duration to total time
        }
    }

    @Override
    public Row finalize(ExecutionContext context) {
        // Convert totals to desired units (MB and seconds)
        double totalSizeInMB = totalSize / (1024.0 * 1024);
        double totalTimeInSec = totalTime / 1000.0;

        // Create a new row to return the aggregated result
        Row resultRow = new Row();
        resultRow.addColumn(totalSizeColumn, totalSizeInMB); // Add the total size (MB)
        resultRow.addColumn(totalTimeColumn, totalTimeInSec); // Add the total time (seconds)

        return resultRow; // Return the aggregated row
    }
}
