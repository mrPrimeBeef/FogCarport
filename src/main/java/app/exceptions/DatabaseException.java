package app.exceptions;

import java.util.logging.Logger;

public class DatabaseException extends Exception {
    private static final Logger LOGGER = Logger.getLogger(DatabaseException.class.getName());

    public DatabaseException(String userMessage) {
        super(userMessage);
        logException(userMessage, null, null);
    }

    public DatabaseException(String userMessage, String systemMessage) {
        super(userMessage);
        logException(userMessage, null, systemMessage);
    }

    public DatabaseException(String userMessage, String developerMessage, String systemMessage) {
        super(userMessage);
        logException(userMessage, developerMessage, systemMessage);
    }

    private void logException(String userMessage, String developerMessage, String systemMessage) {
        StringBuilder logMessage = new StringBuilder("OrderException: ");
        logMessage.append("userMessage=").append(userMessage);

        if (developerMessage != null) {
            logMessage.append(", developerMessage=").append(developerMessage);
        }
        if (systemMessage != null) {
            logMessage.append(", systemMessage=").append(systemMessage);
        }

        LOGGER.severe(logMessage.toString());
    }
}