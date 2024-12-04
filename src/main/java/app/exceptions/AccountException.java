package app.exceptions;

import java.util.logging.Logger;

public class AccountException extends Exception {
    private static final Logger LOGGER = Logger.getLogger(AccountException.class.getName());

    public AccountException(String userMessage) {
        super(userMessage);
        logException(userMessage, null, null);
    }

    public AccountException(String userMessage, String systemMessage) {
        super(userMessage);
        logException(userMessage, null, systemMessage);
    }

    public AccountException(String userMessage, String developerMessage, String systemMessage) {
        super(userMessage);
        logException(userMessage, developerMessage, systemMessage);
    }

    private void logException(String userMessage, String developerMessage, String systemMessage) {
        StringBuilder logMessage = new StringBuilder("AccountException: ");
        logMessage.append("userMessage:").append(userMessage);

        if (developerMessage != null) {
            logMessage.append(", developerMessage: ").append(developerMessage);
        }
        if (systemMessage != null) {
            logMessage.append(", systemMessage: ").append(systemMessage);
        }

        LOGGER.severe(logMessage.toString());
    }
}
