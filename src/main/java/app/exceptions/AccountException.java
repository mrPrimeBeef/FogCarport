package app.exceptions;

import java.util.logging.Logger;

public class AccountException extends Exception {
    private static final Logger LOGGER = Logger.getLogger("Logger");
    public AccountException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }

    public AccountException(String userMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("systemMessage: " + systemMessage);
    }

    public AccountException(String userMessage, String developerMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("developerMessage: " + developerMessage);
        System.out.println("systemMessage: " + systemMessage);
    }
}