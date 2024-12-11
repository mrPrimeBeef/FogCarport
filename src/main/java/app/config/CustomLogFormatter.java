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
        StringBuilder logMessage = new StringBuilder();

        // Timestamp format
        logMessage.append(dateFormat.format(new Date(record.getMillis())))
                .append(" UTC --- ");

        // Class name and method name
        if (record.getSourceClassName() != null) {
            logMessage.append(record.getSourceClassName());
        } else {
            logMessage.append("Unknown Class/Package");
        }

        if (record.getSourceMethodName() != null) {
            logMessage.append(" ")
                    .append(record.getSourceMethodName())
                    .append("()");
        } else {
            logMessage.append(" Unknown Method");
        }

        // Log level and message
        logMessage.append(System.lineSeparator())
                .append(record.getLevel())
                .append(": ")
                .append(formatMessage(record))
                .append(System.lineSeparator())
                .append(System.lineSeparator());

        return logMessage.toString();
    }
}