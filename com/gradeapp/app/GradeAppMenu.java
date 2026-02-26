package com.gradeapp.app;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import com.gradeapp.dao.*;
import com.gradeapp.model.*;
import com.gradeapp.service.GradeService;

public class GradeAppMenu {
    private final Scanner scanner;
    private final MenuHelper helper;
    private final StudentDAO studentDAO;
    private final GradeDAO gradeDAO;
    private final GradeService gradeService;
    
    public GradeAppMenu(StudentDAO studentDAO, GradeDAO gradeDAO, GradeService gradeService) {
        this.scanner = new Scanner(System.in);
        this.helper = new MenuHelper(scanner);
        this.studentDAO = studentDAO;
        this.gradeDAO = gradeDAO;
        this.gradeService = gradeService;
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = helper.readInt("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1 -> studentManagementMenu();
                    case 2 -> gradeManagementMenu();
                    case 3 -> analyticsMenu();
                    case 4 -> bulkOperationsMenu();
                    case 0 -> {
                        if (helper.confirm("Are you sure you want to exit?")) {
                            System.out.println("\n👋 Thank you for using Grade Management System!");
                            running = false;
                        }
                    }
                    default -> System.out.println("❌ Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage());
                helper.pause();
            }
        }
        
        scanner.close();
    }
    
    private void displayMainMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   📚 GRADE MANAGEMENT SYSTEM 📚        ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  1. 👤 Student Management              ║");
        System.out.println("║  2. 📝 Grade Management                ║");
        System.out.println("║  3. 📊 Analytics & Reports             ║");
        System.out.println("║  4. 🔄 Bulk Operations                 ║");
        System.out.println("║  0. 🚪 Exit                            ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    // ========== STUDENT MANAGEMENT ==========
    
    private void studentManagementMenu() throws Exception {
        System.out.println("\n┌──────────────────────────────┐");
        System.out.println("│   👤 STUDENT MANAGEMENT      │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1. Add Student              │");
        System.out.println("│  2. View All Students        │");
        System.out.println("│  3. Find Student by ID       │");
        System.out.println("│  4. Find Student by Name     │");
        System.out.println("│  5. Update Student Email     │");
        System.out.println("│  6. Delete Student           │");
        System.out.println("│  0. Back to Main Menu        │");
        System.out.println("└──────────────────────────────┘");
        
        int choice = helper.readInt("Enter your choice: ");
        
        switch (choice) {
            case 1 -> addStudent();
            case 2 -> viewAllStudents();
            case 3 -> findStudentById();
            case 4 -> findStudentByName();
            case 5 -> updateStudentEmail();
            case 6 -> deleteStudent();
            case 0 -> { /* Return to main menu */ }
            default -> System.out.println("❌ Invalid choice!");
        }
    }
    
    private void addStudent() throws Exception {
        System.out.println("\n➕ ADD NEW STUDENT");
        System.out.println("─────────────────────");
        
        String name = helper.readString("Student Name: ");
        String email = helper.readEmail("Email: ");
        
        Student student = new Student(null, name, email);
        Integer id = studentDAO.addstudent(student);
        
        System.out.println("\n✅ Student added successfully! ID: " + id);
        helper.pause();
    }
    
    private void viewAllStudents() throws Exception {
        System.out.println("\n📋 ALL STUDENTS");
        System.out.println("─────────────────────────────────────────────────────");
        
        List<Student> students = studentDAO.findAllStudent();
        
        if (students.isEmpty()) {
            System.out.println("⚠️  No students found.");
        } else {
            System.out.printf("%-5s %-20s %-30s %-20s%n", "ID", "Name", "Email", "Enrollment Date");
            System.out.println("─────────────────────────────────────────────────────");
            
            for (Student s : students) {
                System.out.printf("%-5d %-20s %-30s %-20s%n",
                    s.getId(),
                    s.getName(),
                    s.getEmail(),
                    s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "N/A"
                );
            }
            
            System.out.println("\nTotal: " + students.size() + " students");
        }
        
        helper.pause();
    }
    
    private void findStudentById() throws Exception {
        System.out.println("\n🔍 FIND STUDENT BY ID");
        System.out.println("─────────────────────");
        
        Integer id = helper.readInt("Student ID: ");
        Optional<Student> student = studentDAO.findStudentById(id);
        
        if (student.isPresent()) {
            Student s = student.get();
            System.out.println("\n✅ Student Found:");
            System.out.println("ID: " + s.getId());
            System.out.println("Name: " + s.getName());
            System.out.println("Email: " + s.getEmail());
            System.out.println("Enrollment Date: " + s.getEnrollmentDate());
        } else {
            System.out.println("❌ Student not found!");
        }
        
        helper.pause();
    }
    
    private void findStudentByName() throws Exception {
        System.out.println("\n🔍 FIND STUDENT BY NAME");
        System.out.println("─────────────────────");
        
        String name = helper.readString("Student Name: ");
        Optional<Student> student = studentDAO.findStudentByName(name);
        
        if (student.isPresent()) {
            Student s = student.get();
            System.out.println("\n✅ Student Found:");
            System.out.println("ID: " + s.getId());
            System.out.println("Name: " + s.getName());
            System.out.println("Email: " + s.getEmail());
        } else {
            System.out.println("❌ Student not found!");
        }
        
        helper.pause();
    }
    
    private void updateStudentEmail() throws Exception {
        System.out.println("\n✏️  UPDATE STUDENT EMAIL");
        System.out.println("─────────────────────");
        
        Integer id = helper.readInt("Student ID: ");
        String newEmail = helper.readEmail("New Email: ");
        
        if (helper.confirm("Update email for student ID " + id + "?")) {
            studentDAO.updateStudentEmail(id, newEmail);
            System.out.println("✅ Email updated successfully!");
        } else {
            System.out.println("⚠️  Update cancelled.");
        }
        
        helper.pause();
    }
    
    private void deleteStudent() throws Exception {
        System.out.println("\n🗑️  DELETE STUDENT");
        System.out.println("─────────────────────");
        System.out.println("⚠️  WARNING: This will also delete all grades for this student!");
        
        Integer id = helper.readInt("Student ID: ");
        
        // Show student details first
        Optional<Student> student = studentDAO.findStudentById(id);
        if (student.isEmpty()) {
            System.out.println("❌ Student not found!");
            helper.pause();
            return;
        }
        
        System.out.println("\nStudent to delete:");
        System.out.println("Name: " + student.get().getName());
        System.out.println("Email: " + student.get().getEmail());
        
        if (helper.confirm("Are you ABSOLUTELY SURE you want to delete this student?")) {
            studentDAO.hardDeleteStudent(id);
            System.out.println("✅ Student deleted successfully!");
        } else {
            System.out.println("⚠️  Deletion cancelled.");
        }
        
        helper.pause();
    }
    
    // ========== GRADE MANAGEMENT ==========
    
    private void gradeManagementMenu() throws Exception {
        System.out.println("\n┌──────────────────────────────┐");
        System.out.println("│   📝 GRADE MANAGEMENT        │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1. Add Grade                │");
        System.out.println("│  2. View All Grades          │");
        System.out.println("│  3. View Grades by Student   │");
        System.out.println("│  4. View Grades by Subject   │");
        System.out.println("│  5. Update Grade Score       │");
        System.out.println("│  6. Delete Grade             │");
        System.out.println("│  0. Back to Main Menu        │");
        System.out.println("└──────────────────────────────┘");
        
        int choice = helper.readInt("Enter your choice: ");
        
        switch (choice) {
            case 1 -> addGrade();
            case 2 -> viewAllGrades();
            case 3 -> viewGradesByStudent();
            case 4 -> viewGradesBySubject();
            case 5 -> updateGradeScore();
            case 6 -> deleteGrade();
            case 0 -> { /* Return */ }
            default -> System.out.println("❌ Invalid choice!");
        }
    }
    
    private void addGrade() throws Exception {
        System.out.println("\n➕ ADD NEW GRADE");
        System.out.println("─────────────────────");
        
        Integer studentId = helper.readInt("Student ID: ");
        
        // Check if student exists
        Optional<Student> student = studentDAO.findStudentById(studentId);
        if (student.isEmpty()) {
            System.out.println("❌ Student not found!");
            helper.pause();
            return;
        }
        
        System.out.println("Student: " + student.get().getName());
        
        String subject = helper.readString("Subject: ");
        Double score = helper.readDouble("Score (0-100): ");
        LocalDateTime examDate = helper.readDate("Exam Date");
        Grade grade = new Grade(null, studentId, subject, score);
        grade.setExamDate(examDate);
        
        Integer id = gradeDAO.addGrade(grade);
        System.out.println("\n✅ Grade added successfully! ID: " + id);
        helper.pause();
    }
    
    private void viewAllGrades() throws Exception {
        System.out.println("\n📋 ALL GRADES");
        System.out.println("────────────────────────────────────────────────────────────");
        
        List<Grade> grades = gradeDAO.findAllGrade();
        
        if (grades.isEmpty()) {
            System.out.println("⚠️  No grades found.");
        } else {
            System.out.printf("%-5s %-12s %-15s %-8s %-20s%n",
                "ID", "Student ID", "Subject", "Score", "Exam Date");
            System.out.println("────────────────────────────────────────────────────────────");
            
            for (Grade g : grades) {
                System.out.printf("%-5d %-12d %-15s %-8.2f %-20s%n",
                    g.getId(),
                    g.getStudentId(),
                    g.getSubject(),
                    g.getScore(),
                    g.getExamDate() != null ? g.getExamDate().toString() : "N/A"
                );
            }
            
            System.out.println("\nTotal: " + grades.size() + " grades");
        }
        
        helper.pause();
    }
    
    private void viewGradesByStudent() throws Exception {
        System.out.println("\n📋 GRADES BY STUDENT");
        System.out.println("─────────────────────");
        
        Integer studentId = helper.readInt("Student ID: ");
        
        // Check if student exists
        Optional<Student> student = studentDAO.findStudentById(studentId);
        if (student.isEmpty()) {
            System.out.println("❌ Student not found!");
            helper.pause();
            return;
        }
        
        System.out.println("\nStudent: " + student.get().getName());
        System.out.println("────────────────────────────────────────────────");
        
        List<Grade> grades = gradeDAO.findStudentListGradeById(studentId);
        
        if (grades.isEmpty()) {
            System.out.println("⚠️  No grades found for this student.");
        } else {
            System.out.printf("%-15s %-8s %-20s%n", "Subject", "Score", "Exam Date");
            System.out.println("────────────────────────────────────────────────");
            
            for (Grade g : grades) {
                System.out.printf("%-15s %-8.2f %-20s%n",
                    g.getSubject(),
                    g.getScore(),
                    g.getExamDate() != null ? g.getExamDate().toString() : "N/A"
                );
            }
            
            // Calculate average
            Double average = gradeService.calculateAverageScore(studentId);
            System.out.println("\n📊 Average Score: " + String.format("%.2f", average));
        }
        
        helper.pause();
    }
     
    private void viewGradesBySubject() throws Exception {
        System.out.println("\n📋 GRADES BY SUBJECT");
        System.out.println("─────────────────────");
        
        String subject = helper.readString("Subject: ");
        List<Grade> grades = gradeDAO.findGradesBySubject(subject);
        
        if (grades.isEmpty()) {
            System.out.println("⚠️  No grades found for subject: " + subject);
        } else {
            System.out.printf("%-12s %-8s%n", "Student ID", "Score");
            System.out.println("─────────────────────");
            
            for (Grade g : grades) {
                System.out.printf("%-12d %-8.2f%n", g.getStudentId(), g.getScore());
            }
            
            // Calculate subject average
            Double average = gradeService.calculateSubjectAverage(subject);
            System.out.println("\n📊 Subject Average: " + String.format("%.2f", average));
        }
        
        helper.pause();
    }
    
    private void updateGradeScore() throws Exception {
        System.out.println("\n✏️  UPDATE GRADE SCORE");
        System.out.println("─────────────────────");
        
        Integer gradeId = helper.readInt("Grade ID: ");
        Double newScore = helper.readDouble("New Score (0-100): ");
        
        if (helper.confirm("Update score for grade ID " + gradeId + "?")) {
            gradeDAO.updatescore(gradeId, newScore);
            System.out.println("✅ Score updated successfully!");
        } else {
            System.out.println("⚠️  Update cancelled.");
        }
        
        helper.pause();
    }
    
    private void deleteGrade() throws Exception {
        System.out.println("\n🗑️  DELETE GRADE");
        System.out.println("─────────────────────");
        
        Integer gradeId = helper.readInt("Grade ID: ");
        
        if (helper.confirm("Delete grade ID " + gradeId + "?")) {
            gradeDAO.hardDeleteGrade(gradeId);
            System.out.println("✅ Grade deleted successfully!");
        } else {
            System.out.println("⚠️  Deletion cancelled.");
        }
        
        helper.pause();
    }
    
    // ========== ANALYTICS MENU ==========
    
    private void analyticsMenu() throws Exception {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│   📊 ANALYTICS & REPORTS           │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. Student Average Score          │");
        System.out.println("│  2. Top N Students                 │");
        System.out.println("│  3. Subject Averages               │");
        System.out.println("│  4. Failing Subjects               │");
        System.out.println("│  5. Students with No Grades        │");
        System.out.println("│  6. Rank Students by Subject       │");
        System.out.println("│  0. Back to Main Menu              │");
        System.out.println("└────────────────────────────────────┘");
        
        int choice = helper.readInt("Enter your choice: ");
        
        switch (choice) {
            case 1 -> calculateStudentAverage();
            case 2 -> showTopStudents();
            case 3 -> showSubjectAverages();
            case 4 -> showFailingSubjects();
            case 5 -> showStudentsWithNoGrades();
            case 6 -> rankStudentsBySubject();
            case 0 -> { /* Return */ }
            default -> System.out.println("❌ Invalid choice!");
        }
    }
    
    private void calculateStudentAverage() throws Exception {
        System.out.println("\n📊 STUDENT AVERAGE SCORE");
        System.out.println("─────────────────────");
        
        Integer studentId = helper.readInt("Student ID: ");
        
        Optional<Student> student = studentDAO.findStudentById(studentId);
        if (student.isEmpty()) {
            System.out.println("❌ Student not found!");
            helper.pause();
            return;
        }
        
        Double average = gradeService.calculateAverageScore(studentId);
        
        System.out.println("\nStudent: " + student.get().getName());
        System.out.println("Average Score: " + String.format("%.2f", average));
        
        // Show grade breakdown
        Optional<Double> highest = gradeService.getHighestscore(studentId);
        Optional<Double> lowest = gradeService.getLowestscore(studentId);
        long gradeCount = gradeService.contGradesstudent(studentId);
        
        System.out.println("\n📈 Statistics:");
        System.out.println("  Total Grades: " + gradeCount);
        highest.ifPresent(h -> System.out.println("  Highest Score: " + String.format("%.2f", h)));
        lowest.ifPresent(l -> System.out.println("  Lowest Score: " + String.format("%.2f", l)));
        
        helper.pause();
    }
    
    private void showTopStudents() throws Exception {
        System.out.println("\n🏆 TOP STUDENTS");
        System.out.println("─────────────────────");
        
        Integer limit = helper.readInt("How many top students? ");
        
        List<Student> topStudents = gradeService.getTopStudents(limit);
        
        if (topStudents.isEmpty()) {
            System.out.println("⚠️  No students found.");
        } else {
            System.out.printf("\n%-5s %-20s %-30s %-10s%n", "Rank", "Name", "Email", "Average");
            System.out.println("───────────────────────────────────────────────────────────────");
            
            int rank = 1;
            for (Student s : topStudents) {
                Double avg = gradeService.calculateAverageScore(s.getId());
                System.out.printf("%-5d %-20s %-30s %-10.2f%n",
                    rank++, s.getName(), s.getEmail(), avg);
            }
        }
        
        helper.pause();
    }
    
    private void showSubjectAverages() throws Exception {
        System.out.println("\n📊 SUBJECT AVERAGES");
        System.out.println("─────────────────────────────");
        
        Map<String, Double> averages = gradeService.getAllsubjectAverages();
        
        if (averages.isEmpty()) {
            System.out.println("⚠️  No grades found.");
        } else {
            System.out.printf("%-20s %-10s%n", "Subject", "Average");
            System.out.println("─────────────────────────────");
            
            averages.forEach((subject, avg) ->
                System.out.printf("%-20s %-10.2f%n", subject, avg)
            );
        }
        
        helper.pause();
    }
    
    private void showFailingSubjects() throws Exception {
        System.out.println("\n⚠️  FAILING SUBJECTS");
        System.out.println("─────────────────────");
        
        Double threshold = helper.readDouble("Threshold score: ");
        
        List<String> failingSubjects = gradeService.getFailingSubjects(threshold);
        
        if (failingSubjects.isEmpty()) {
            System.out.println("✅ No subjects below threshold " + threshold);
        } else {
            System.out.println("\n⚠️  Subjects with average below " + threshold + ":");
            failingSubjects.forEach(subject -> {
                try {
                    Double avg = gradeService.calculateSubjectAverage(subject);
                    System.out.println("  • " + subject + " (Average: " + String.format("%.2f", avg) + ")");
                } catch (Exception e) {
                    System.out.println("  • " + subject);
                }
            });
        }
        
        helper.pause();
    }
    
    private void showStudentsWithNoGrades() throws Exception {
        System.out.println("\n⚠️  STUDENTS WITH NO GRADES");
        System.out.println("─────────────────────────────");
        
        List<Student> students = gradeService.getStudentsWithNoGrades();
        
        if (students.isEmpty()) {
            System.out.println("✅ All students have at least one grade!");
        } else {
            System.out.printf("%-5s %-20s %-30s%n", "ID", "Name", "Email");
            System.out.println("───────────────────────────────────────────────────");
            
            for (Student s : students) {
                System.out.printf("%-5d %-20s %-30s%n", s.getId(), s.getName(), s.getEmail());
            }
            
            System.out.println("\nTotal: " + students.size() + " students without grades");
        }
        
        helper.pause();
    }
    
    private void rankStudentsBySubject() throws Exception {
        System.out.println("\n🏆 RANK STUDENTS BY SUBJECT");
        System.out.println("─────────────────────");
        
        String subject = helper.readString("Subject: ");
        
        List<Student> rankedStudents = gradeService.rankStudentsBySubject(subject);
        
        if (rankedStudents.isEmpty()) {
            System.out.println("⚠️  No students found for subject: " + subject);
        } else {
            System.out.printf("\n%-5s %-20s %-10s%n", "Rank", "Name", "Score");
            System.out.println("───────────────────────────────────────");
            
            int rank = 1;
            for (Student s : rankedStudents) {
                // Get highest score for this subject
                List<Grade> grades = gradeDAO.findGradesBySubject(subject);
                Double score = grades.stream()
                    .filter(g -> g.getStudentId().equals(s.getId()))
                    .mapToDouble(Grade::getScore)
                    .max()
                    .orElse(0.0);
                
                System.out.printf("%-5d %-20s %-10.2f%n", rank++, s.getName(), score);
            }
        }
        
        helper.pause();
    }
    
    // ========== BULK OPERATIONS ==========
    
    private void bulkOperationsMenu() throws Exception {
        System.out.println("\n┌──────────────────────────────┐");
        System.out.println("│   🔄 BULK OPERATIONS         │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1. Transfer Grades          │");
        System.out.println("│  0. Back to Main Menu        │");
        System.out.println("└──────────────────────────────┘");
        
        int choice = helper.readInt("Enter your choice: ");
        
        switch (choice) {
            case 1 -> transferGrades();
            case 0 -> { /* Return */ }
            default -> System.out.println("❌ Invalid choice!");
        }
    }
    
    private void transferGrades() throws Exception {
        System.out.println("\n🔄 TRANSFER GRADES");
        System.out.println("─────────────────────");
        System.out.println("⚠️  This will COPY all grades from one student to another.");
        System.out.println("   Original student keeps their grades.\n");
        
        Integer fromStudentId = helper.readInt("From Student ID: ");
        
        // Check source student exists
        Optional<Student> fromStudent = studentDAO.findStudentById(fromStudentId);
        if (fromStudent.isEmpty()) {
            System.out.println("❌ Source student not found!");
            helper.pause();
            return;
        }
        
        System.out.println("From: " + fromStudent.get().getName());
        
        // Show how many grades will be transferred
        List<Grade> grades = gradeDAO.findStudentListGradeById(fromStudentId);
        System.out.println("Grades to transfer: " + grades.size());
        
        if (grades.isEmpty()) {
            System.out.println("⚠️  Source student has no grades to transfer!");
            helper.pause();
            return;
        }
        
        Integer toStudentId = helper.readInt("To Student ID: ");
        
        // Check target student exists
        Optional<Student> toStudent = studentDAO.findStudentById(toStudentId);
        if (toStudent.isEmpty()) {
            System.out.println("❌ Target student not found!");
            helper.pause();
            return;
        }
        
        System.out.println("To: " + toStudent.get().getName());
        
        if (helper.confirm("\nTransfer " + grades.size() + " grades from " + 
                          fromStudent.get().getName() + " to " + toStudent.get().getName() + "?")) {
            gradeService.transferGrades(fromStudentId, toStudentId);
            System.out.println("✅ Grades transferred successfully!");
        } else {
            System.out.println("⚠️  Transfer cancelled.");
        }
        
        helper.pause();
    }
}