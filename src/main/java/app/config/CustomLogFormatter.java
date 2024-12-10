package app.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

// Custom Formatter
class CustomLogFormatter extends Formatter {
    private SimpleDateFormat dateFormat;

    public CustomLogFormatter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss"); // ISO 8601 format
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        // Timestamp format
        sb.append(dateFormat.format(new Date(record.getMillis())))
                .append(" UTC --- ");

        // Class name and method name
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        } else {
            sb.append("Unknown Class/Package");
        }

        if (record.getSourceMethodName() != null) {
            sb.append(" ")
                    .append(record.getSourceMethodName())
                    .append("()");
        } else {
            sb.append(" Unknown Method");
        }

        // Log level and message
        sb.append(System.lineSeparator())
                .append(record.getLevel())
                .append(": ")
                .append(formatMessage(record))
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        return sb.toString();
    }
}