package com.gradeapp.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppLogger {
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE_PATTERN = LOG_DIR + "/app_%g.log";
    private static boolean initialized = false;

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        if (!initialized) {
            setupLogging();
            initialized = true;
        }
        return logger;
    }
    private static void setupLogging() {
        try {
            java.io.File logDir = new java.io.File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            Logger rootLogger = Logger.getLogger("");
            
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
            
            FileHandler fileHandler = new FileHandler(
                LOG_FILE_PATTERN,
                5 * 1024 * 1024,  
                5,          
                true            
            );
            
            
            fileHandler.setFormatter(new SimpleFormatter() {
                private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                
                @Override
                public synchronized String format(LogRecord record) {
                    return String.format(
                        "[%s] [%s] [%s.%s] %s%n",
                        LocalDateTime.now().format(dtf),
                        record.getLevel(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        formatMessage(record)
                    );
                }
            });
            
            fileHandler.setLevel(Level.ALL);
            rootLogger.addHandler(fileHandler);
            
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.WARNING);
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    return String.format(
                        "[%s] %s%n",
                        record.getLevel(),
                        formatMessage(record)
                    );
                }
            });
            rootLogger.addHandler(consoleHandler);
            
            rootLogger.setLevel(Level.ALL);
            
            Logger logger = Logger.getLogger(AppLogger.class.getName());
            logger.info("============================================");
            logger.info("  Logging System Initialized");
            logger.info("  Log Directory: " + LOG_DIR);
            logger.info("  Log File Pattern: " + LOG_FILE_PATTERN);
            logger.info("============================================");
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logging: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
