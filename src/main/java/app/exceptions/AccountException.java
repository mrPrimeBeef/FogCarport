package app.exceptions;

public class AccountException extends Exception {
    public AccountException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }
}