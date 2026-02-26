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
            // Create logs directory if not exists
            java.io.File logDir = new java.io.File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // Get root logger
            Logger rootLogger = Logger.getLogger("");
            
            // Remove default console handler
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
            
            // ===== FILE HANDLER =====
            // Creates rotating log files: app_0.log, app_1.log, etc.
            // Max 5MB per file, keep 5 files
            FileHandler fileHandler = new FileHandler(
                LOG_FILE_PATTERN,
                5 * 1024 * 1024,  // 5MB per file
                5,                // Keep 5 files
                true              // Append mode
            );
            
            // Custom formatter for file logs
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
            
            // ===== CONSOLE HANDLER (ERRORS ONLY) =====
            // Show only SEVERE and WARNING in console
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
            
            // Set root logger level
            rootLogger.setLevel(Level.ALL);
            
            // Log startup message
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
