package app.exceptions;

import app.config.LoggerConfig;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;


class LoggerConfigTest {
    private static final Logger LOGGER = LoggerConfig.getLOGGER();

    @Test
    public void testLogFileCreation() {
        LOGGER.info("test1");
        LOGGER.severe("Test logging-besked");

        // Verificer at log-filen er oprettet og indeholder beskeden
        try (BufferedReader reader = new BufferedReader(new FileReader("errors.log"))) {
            String lastLine = "";
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lastLine = currentLine;
            }
            assertTrue(lastLine.contains("Test logging-besked"), "Log-filen skal indeholde test-beskeden");
        } catch (IOException e) {
            fail("Kunne ikke læse log-filen: " + e.getMessage());
        }
    }
}