package com.gradeapp.exception;

public class GradeNotFoundException extends BaseExceptionApp{
    private Integer id;
    public GradeNotFoundException(String m){
        super(m);
        id = null;
    }
    public GradeNotFoundException(int id){
        super("grades not found for id students"+id);
        this.id = id;
    }
    public int getId() {
        return id;
    }
} 
