package com.gradeapp.exception;

import java.sql.SQLException;

public class DAOException extends SQLException {
    public DAOException(String mess,Throwable cause ){
        super(mess,cause);
    } 
}
