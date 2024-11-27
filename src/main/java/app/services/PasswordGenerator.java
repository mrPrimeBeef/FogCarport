package app.services;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";


    private static final String ALL_CHARS =
            UPPERCASE_CHARS + LOWERCASE_CHARS + NUMBER_CHARS + SPECIAL_CHARS;

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {
        StringBuilder password = new StringBuilder();

        password.append(getRandomChar(UPPERCASE_CHARS));
        password.append(getRandomChar(LOWERCASE_CHARS));
        password.append(getRandomChar(NUMBER_CHARS));
        password.append(getRandomChar(SPECIAL_CHARS));

        // password length 6, so 2 is the remaining
        for (int i = 0; i < 2; i++) {
            password.append(ALL_CHARS);
        }

        return shufflechars(password.toString());
    }

    private static char getRandomChar(String charSet) {
        return charSet.charAt(random.nextInt(charSet.length()));
    }

    private static String shufflechars(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}
