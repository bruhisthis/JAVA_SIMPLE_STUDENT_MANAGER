package com.gradeapp.exception;

public class StudentNotFoundException extends BaseExceptionApp {
    private Integer id;
    private String name;
    public StudentNotFoundException(String m){
        super(m);
        id = null;
        name = null;
    }
    public StudentNotFoundException(Integer id, String name) {
        super(buildMessage(id, name));
        this.id = id;
        this.name = name;
    }

    private static String buildMessage(Integer id, String name) {
        if (id != null && name == null) {
            return "student id not found " + id;
        }
        if (id == null && name != null) {
            return "student name not found " + name;
        }
        return "Student not found (no identifier provided).";
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
