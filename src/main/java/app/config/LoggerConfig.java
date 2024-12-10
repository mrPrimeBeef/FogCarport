package app.config;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerConfig {
    private static Logger LOGGER;

    public static Logger getLOGGER() {
        if (LOGGER == null) {
            try {
                LOGGER = Logger.getLogger("GlobalLogger");

                FileHandler fileHandler = new FileHandler("loggingfile.log", true);
                fileHandler.setFormatter(new CustomLogFormatter());
                LOGGER.addHandler(fileHandler);

                LOGGER.setLevel(java.util.logging.Level.ALL);
            } catch (IOException e) {
                System.err.println("Kunne ikke konfigurere logger: " + e.getMessage());
            }
        }
        return LOGGER;
    }
}
