package app.config;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    private static Logger LOGGER;

    private LoggerConfig() {
        // Privat constructor for at forhindre instansiering
    }

    public static Logger getLOGGER() {
        if (LOGGER == null) {
            try {
                LOGGER = Logger.getLogger("GlobalLogger");

                FileHandler fileHandler = new FileHandler("loggingfile.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(fileHandler);

                LOGGER.setLevel(java.util.logging.Level.ALL);
            } catch (IOException e) {
                System.err.println("Kunne ikke konfigurere logger: " + e.getMessage());
            }
        }
        return LOGGER;
    }
}
