package app.exceptions;

public class EmailException extends Exception {

    public EmailException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }

}