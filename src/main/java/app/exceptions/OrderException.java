package app.exceptions;

public class OrderException extends Exception {
    public OrderException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }
}