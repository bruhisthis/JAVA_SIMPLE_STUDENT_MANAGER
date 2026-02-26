package com.gradeapp.dao;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import com.gradeapp.exception.BaseExceptionApp;
import com.gradeapp.exception.DAOException;
import com.gradeapp.model.Grade;
import com.gradeapp.model.GradeBase;

public interface GradeDAO {
    Integer addGrade(Grade grade)throws DAOException,BaseExceptionApp;
    Integer addGrade(Grade grade, Connection conn) throws DAOException, BaseExceptionApp;
    void hardDeleteGrade(Integer id) throws DAOException,BaseExceptionApp;
    void updatescore(Integer id,Double score) throws DAOException,BaseExceptionApp;
    void updatesExamDate(Integer id,LocalDateTime tmp) throws DAOException,BaseExceptionApp;
    Optional<GradeBase> findGradeSById(Integer id) throws DAOException,BaseExceptionApp;
    Optional<Grade> findStudentGradeById(String sujbect,Integer studentId) throws DAOException,BaseExceptionApp;
    List<Grade> findAllGrade() throws DAOException,BaseExceptionApp;
    List<Grade> findStudentListGradeById(Integer id) throws DAOException,BaseExceptionApp;
    List<Grade> findGradesBySubject(String subject) throws DAOException, BaseExceptionApp;
}
 