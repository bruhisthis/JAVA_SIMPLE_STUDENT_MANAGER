package com.gradeapp.dao;

import java.util.Optional;
import java.util.List;

import com.gradeapp.exception.*;
import com.gradeapp.model.Student;

public interface StudentDAO {
    Integer addstudent(Student s) throws DAOException,BaseExceptionApp;
    void updateStudentEmail(Integer id,String email) throws DAOException,BaseExceptionApp;
    void updateStudentName(Integer id,String name) throws DAOException,BaseExceptionApp;
    void hardDeleteStudent(Integer id) throws DAOException,BaseExceptionApp;
    Optional<Student> findStudentById(Integer id) throws DAOException,BaseExceptionApp;
    Optional<Student> findStudentByName(String name) throws DAOException,BaseExceptionApp;
    List<Student> findAllStudent() throws DAOException,BaseExceptionApp;
}
 