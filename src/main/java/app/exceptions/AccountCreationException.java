package app.exceptions;

public class AccountCreationException extends Exception {

    public AccountCreationException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }

    public AccountCreationException(String userMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("systemMessage: " + systemMessage);
    }

    public AccountCreationException(String userMessage, String developerMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("developerMessage: " + developerMessage);
        System.out.println("systemMessage: " + systemMessage);
    }
}