package app.services;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void generatePasswordContainsElements() {
        PasswordGenerator pg = new PasswordGenerator();
        String password = pg.generatePassword();

        String uppercasePattern = ".*[A-Z].*";
        String lowercasePattern = ".*[a-z].*";
        String numberPattern = ".*[0-9].*";
        String specialCharPattern = ".*[^A-Za-z0-9].*"; // all tegn som ikke er bogstaver eller tal

        boolean hasUppercase = Pattern.matches(uppercasePattern, password);
        boolean hasLowercase = Pattern.matches(lowercasePattern, password);
        boolean hasNumber = Pattern.matches(numberPattern, password);
        boolean hasSpecialChar = Pattern.matches(specialCharPattern, password);

        assertTrue(hasUppercase || hasLowercase || hasNumber || hasSpecialChar);
        assertEquals(6, password.length());
    }
    @Test
    void generatePasswordloop() {
        for (int i = 0; i < 100; i++) {
            PasswordGenerator pg = new PasswordGenerator();
            String password = pg.generatePassword();
        }
    }
}
