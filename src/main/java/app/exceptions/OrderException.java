package app.exceptions;

import java.util.logging.Logger;

public class OrderException extends Exception {
    private static final Logger LOGGER = Logger.getLogger("Logger");

    public OrderException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }

    public OrderException(String userMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("systemMessage: " + systemMessage);
    }

    public OrderException(String userMessage, String developerMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("developerMessage: " + developerMessage);
        System.out.println("systemMessage: " + systemMessage);
    }
}