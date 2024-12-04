package app.exceptions;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    public static void setup() {
        try {
            FileHandler fileHandler = new FileHandler("errors.log", true); // true = append mode
            fileHandler.setFormatter(new SimpleFormatter());


            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);

            // Valgfrit: Indstil root loggerens niveau til at logge alt
            rootLogger.setLevel(java.util.logging.Level.ALL);

        } catch (IOException e) {
            System.err.println("Kunne ikke konfigurere logger: " + e.getMessage());
        }
    }
}