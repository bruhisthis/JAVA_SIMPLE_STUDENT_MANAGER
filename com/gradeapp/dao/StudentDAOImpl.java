package com.gradeapp.dao;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.gradeapp.exception.*;
import com.gradeapp.exception.InvalidInputException.INPUTYPE;
import com.gradeapp.model.Student;
import com.gradeapp.util.AppLogger;
import com.gradeapp.util.DBConnection;

public class StudentDAOImpl implements StudentDAO {
    private static final Logger logger = AppLogger.getLogger(StudentDAOImpl.class);
    private Boolean inputValidator(String email,Integer id,INPUTYPE type){ 

        boolean idvalidation = (id == null || id <=0)?true:false;

        switch (type) {
            case INPUTYPE.EMAIL:
                if (!email.contains("@gmail.com") || idvalidation) {
                    return true;
                }   
            case INPUTYPE.STRING:
                if (email.isBlank()|| idvalidation) {
                    return true;
                }
            default:
              return false;    
        }  
    }

    @Override
    public Integer addstudent(Student s) throws DAOException ,BaseExceptionApp{
        String sql = "INSERT INTO students(name,email) VALUES (?, ?)";
        if (s == null) {
            throw new InvalidInputException();
        }
        try(Connection conn = DBConnection.getConnection();){    
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, s.getName());
                pstmt.setString(2, s.getEmail());
                int row = pstmt.executeUpdate();
                if (row == 0) {
                    throw new DAOException("insertion failed: no row affected",null);
                }
                
                try(ResultSet rs = pstmt.getGeneratedKeys()){
                    if (rs.next()) {
                        logger.info("student inserted succesfully "+s.getName()+"with id "+rs.getInt(1));
                        return rs.getInt(1);
                        } 
                    }
                    logger.warning("insertion succeeded but id not returned.name"+s.getName());
                    throw new DAOException("insertion succeeded but id not returned.", null);
                
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "insertion failed.", e);
                throw new DAOException("insertion failed error", e);
            }
    }
    @Override
    public void updateStudentEmail(Integer id, String email) throws DAOException, BaseExceptionApp {
        String sql ="UPDATE students SET email = ? WHERE id = ?";
        if (inputValidator(email, id,INPUTYPE.EMAIL)) {
            logger.log(Level.SEVERE, "invalid input error. id: " +id+",email: "+email);
            throw new InvalidInputException("invalid input: ",email,id,null,INPUTYPE.EMAIL);
        }
        try(Connection conn = DBConnection.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setInt(2,id);
                int row = pstmt.executeUpdate();
                if (row == 0) {
                    throw new DAOException("upadte faild : no row effected",null);
                }
                logger.info("student updated: id:"+id+",new gmail:"+email);
            } 
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"update faild ", e);
            throw new DAOException("updatefaild error", e);
        } 
        
    }
    @Override
    public void updateStudentName(Integer id, String name) throws DAOException, BaseExceptionApp{
        String sql ="UPDATE students SET name = ? WHERE id = ?";
        if (inputValidator(name, id,INPUTYPE.STRING)) {
            logger.log(Level.SEVERE, "invalid input error .id:"+id+",name:"+name);
            throw new InvalidInputException("invalid input: ",name,id,null,INPUTYPE.STRING);
        }
        try (Connection conn = DBConnection.getConnection();){
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2,id);
                int row = pstmt.executeUpdate();
                if (row == 0) {
                    throw new DAOException("update faild : no row effected", null);
                }
                logger.info("student updated: id:"+id+"new name:"+name);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"update faild ", e);
            throw new DAOException("updatefaild error", e);
        }
    }
    @Override
    public void hardDeleteStudent(Integer id) throws DAOException,BaseExceptionApp {
        String sql = "DELETE FROM students WHERE id = ?";
        if (id == null || id <=0) {
            logger.log(Level.SEVERE, "invalid input error .id:"+id);
            throw new InvalidInputException("invalid input",null,id,null,null);
        }
        try (Connection conn = DBConnection.getConnection();){
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int row = pstmt.executeUpdate();
                if (row == 0) {
                    throw new DAOException("update faild : no row effected", null);
                }
                logger.info("student deleted from database "+id);
            } 
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"delete faild ", e);
            throw new DAOException("delete faild error", e);
        }
    }
    
    @Override
    public List<Student> findAllStudent() throws DAOException, BaseExceptionApp {
        String sql = "SELECT * FROM students";
        List<Student> listemp = new ArrayList<>();
        Student stemp = null;
        Integer id =null;
        String name = null;
        String email = null;
        Timestamp tm = null;
        try(Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    id = rs.getInt("id");
                    name = rs.getString("name");
                    email = rs.getString("email");
                    tm = rs.getTimestamp("enrollment_date");
                    stemp = new Student(id, name, email);
                    stemp.setEnrollmentDate(tm.toLocalDateTime());
                    listemp.add(stemp);
                }
                return listemp;
            } 
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"read from data base is faild", e);
            throw new DAOException("read from data base is faild", e);
        }
    }
    @Override
    public Optional<Student> findStudentById(Integer id) throws DAOException, BaseExceptionApp {
        String sql ="SELECT * FROM students WHERE id = ?";
        Student stemp = null;
        Integer iD =null;
        String name = null;
        String email = null;
        Timestamp tm = null;

        if (id == null || id <=0) {
            logger.log(Level.SEVERE, "invalid input error .id:"+id);
            throw new InvalidInputException("invalid input",null,id,null,null);
        }
        try (Connection conn = DBConnection.getConnection();){
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        iD = rs.getInt("id");
                        name = rs.getString("name");
                        email = rs.getString("email");
                        tm = rs.getTimestamp("enrollment_date");
                        
                        stemp = new Student(iD, name, email);
                        stemp.setEnrollmentDate(tm.toLocalDateTime());
                        return Optional.of(stemp);
                    }
                    logger.warning("student id "+id+"not found.");
                    return Optional.empty();
                }
            } 
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"read from data base is faild", e);
            throw new DAOException("read from data base is faild", e);
        }
        
    }
    
    @Override
    public Optional<Student> findStudentByName(String name) throws DAOException, BaseExceptionApp {
        String sql ="SELECT * FROM students WHERE name = ?";
        Student stemp = null;
        Integer iD =null;
        String naMe = null;
        String email = null;
        Timestamp tm = null;
        
        if (name == null || name.isBlank()) {
            logger.log(Level.SEVERE, "invalid input error .name:"+name);
            throw new InvalidInputException("invalid input",name,null,null,INPUTYPE.STRING);
        }
        try(Connection conn = DBConnection.getConnection();) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);;
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        iD = rs.getInt("id");
                        naMe = rs.getString("name");
                        email = rs.getString("email");
                        tm = rs.getTimestamp("enrollment_date");
                        stemp = new Student(iD, naMe, email);
                        stemp.setEnrollmentDate(tm.toLocalDateTime());
                        
                        return Optional.of(stemp);
                    }
                    logger.warning("student name "+name+"not found.");
                    return Optional.empty();
                }
            } 
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"read from data base is faild", e);
            throw new DAOException("read from data base is faild", e);
        }
    } 
}