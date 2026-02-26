package com.gradeapp.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MenuHelper {
    private final Scanner scanner;
    
    public MenuHelper(Scanner scanner) {
        this.scanner = scanner;
    }
     
    // Read integer with validation
    public Integer readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("⚠️  Input cannot be empty!");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number! Please try again.");
            }
        }
    }
    
    // Read double with validation
    public Double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("⚠️  Input cannot be empty!");
                    continue;
                }
                double value = Double.parseDouble(input);
                if (value < 0 || value > 100) {
                    System.out.println("❌ Score must be between 0 and 100!");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number! Please try again.");
            }
        }
    }
    
    // Read string with validation
    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("⚠️  Input cannot be empty!");
                continue;
            }
            return input;
        }
    }
    
    // Read email with validation
    public String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("⚠️  Email cannot be empty!");
                continue;
            }
            if (!input.contains("@")) {
                System.out.println("❌ Invalid email format!");
                continue;
            }
            return input;
        }
    }
    
    // Read date (optional)
    public LocalDateTime readDate(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        while (true) {
            System.out.print(prompt + " (format: yyyy-MM-dd HH:mm, or press Enter to use current time): ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                return LocalDateTime.now();
            }
            
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Use yyyy-MM-dd HH:mm");
            }
        }
    }
    
    // Confirm action
    public boolean confirm(String message) {
        System.out.print(message + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
    
    // Pause
    public void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    // Clear screen (works on most terminals)
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}