package com.gradeapp.model;

import java.time.format.DateTimeFormatter;

public class Grade extends GradeBase {
    private Integer studentId;

    public Grade(Integer id,int studentId,String subject,double score){
            super(id,subject,score);
            this.studentId = studentId;
    }
    public Integer getStudentId() {
        return studentId;
    }
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    @Override
    public String toString() {
        DateTimeFormatter ftm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = examDate.format(ftm);
        return String.format("grade->{id=%d,studentId=%d,subject=%s,score=%.2f,examDate="+date+"}",id,studentId,subject,score);
    }
}

