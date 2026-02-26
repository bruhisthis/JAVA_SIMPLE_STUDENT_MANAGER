package com.gradeapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Student {
        private Integer id;
    private String name;
    private String email;
    private LocalDateTime enrollmentDate;
    public Student(Integer id,String name,String email){
        this.id=id;
        this.name = name;
        if (email.contains("@gmail.com") && !email.isEmpty()) {
            this.email = email;
        }else{
            this.email = "no email";
        }
        enrollmentDate = LocalDateTime.now();
    }
    public String getEmail() {
        return email;
    }
    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }
    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    @Override
    public String toString() {
        DateTimeFormatter ftm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = enrollmentDate.format(ftm);
        return String.format("student->{id=%d,name=%s,email=%s,enrollmentdate="+date+"}",id,name,email, null);
    }
}
