package app.exceptions;

public class OrderCreationException extends Exception {

    public OrderCreationException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }

    public OrderCreationException(String userMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("systemMessage: " + systemMessage);
    }

    public OrderCreationException(String userMessage, String developerMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("developerMessage: " + developerMessage);
        System.out.println("systemMessage: " + systemMessage);
    }
}