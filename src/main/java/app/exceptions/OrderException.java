package app.exceptions;

public class OrderException extends Exception {
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