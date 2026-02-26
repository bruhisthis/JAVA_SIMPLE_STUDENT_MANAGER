package com.gradeapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GradeBase {
    protected Integer id;
    protected String subject;
    protected Double score;
    protected LocalDateTime examDate;
    public GradeBase(Integer id,String subject,double score){
        examDate = LocalDateTime.now();//if is created frist time in ram.
        this.id = id;
        this.score = score;
        this.subject = subject;
    }
    public LocalDateTime getExamDate() {
        return examDate;
    }
    public Integer getId() {
        return id;
    }
    public Double getScore() {
        return score;
    }
    public String getSubject() {
        return subject;
    }
    public void setExamDate(LocalDateTime examDate) {
        this.examDate = examDate;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setScore(Double score) {
        this.score = score;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    @Override
    public String toString() {
        DateTimeFormatter ftm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = examDate.format(ftm);
        return String.format("gradeBase->{id=%d,subject=%s,score=%.2f,examDate="+date+"}",id,subject,score);
    }
}
