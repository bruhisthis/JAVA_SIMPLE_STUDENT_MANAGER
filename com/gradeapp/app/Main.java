package com.gradeapp.app;

import com.gradeapp.dao.*;
import com.gradeapp.service.GradeService;
import com.gradeapp.util.AppLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = AppLogger.getLogger(Main.class);
    
    public static void main(String[] args) {
        logger.info("Grade Management System starting...");
         
        try {
            // Initialize DAOs
            StudentDAO studentDAO = new StudentDAOImpl();
            GradeDAO gradeDAO = new GradeDAOImpl();
            
            // Initialize Service
            GradeService gradeService = new GradeService(gradeDAO, studentDAO);
            
            // Start menu system
            GradeAppMenu menu = new GradeAppMenu(studentDAO, gradeDAO, gradeService);
            
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║                                            ║");
            System.out.println("║   Welcome to Grade Management System!      ║");
            System.out.println("║                                            ║");
            System.out.println("╚════════════════════════════════════════════╝");
            
            menu.start();
            
            logger.info("Grade Management System stopped successfully.");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fatal error in application", e);
            System.err.println("\n❌ FATAL ERROR: " + e.getMessage());
            System.err.println("Please check the logs for details.");
            System.exit(1);
        }
    }
}