package app.exceptions;

import java.util.logging.Logger;

public class OrderException extends Exception {
    private static final Logger LOGGER = Logger.getLogger(OrderException.class.getName());

    public OrderException(String userMessage) {
        super(userMessage);
        logException(userMessage, null, null);
    }

    public OrderException(String userMessage, String systemMessage) {
        super(userMessage);
        logException(userMessage, null, systemMessage);
    }

    public OrderException(String userMessage, String developerMessage, String systemMessage) {
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