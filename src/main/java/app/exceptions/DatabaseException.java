package app.exceptions;

public class DatabaseException extends Exception {

    public DatabaseException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }

    public DatabaseException(String userMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("systemMessage: " + systemMessage);
    }

    public DatabaseException(String userMessage, String developerMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("developerMessage: " + developerMessage);
        System.out.println("systemMessage: " + systemMessage);
    }
}