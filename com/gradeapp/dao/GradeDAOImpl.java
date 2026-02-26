package com.gradeapp.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gradeapp.exception.BaseExceptionApp;
import com.gradeapp.exception.DAOException;
import com.gradeapp.exception.InvalidInputException;
import com.gradeapp.exception.StudentNotFoundException;
import com.gradeapp.exception.InvalidInputException.INPUTYPE;
import com.gradeapp.model.Grade;
import com.gradeapp.model.GradeBase;
import com.gradeapp.util.AppLogger;
import com.gradeapp.util.DBConnection;

public class GradeDAOImpl implements GradeDAO {
    private static final Logger logger = AppLogger.getLogger(GradeDAOImpl.class);

    private  void idCheekvalidation(Integer id) throws InvalidInputException{
        if (id == null ) {
            throw new InvalidInputException();
        }
        if (id <=0) {
            throw new InvalidInputException("invalid input",null,id,null,null);
        }
    }
    private void scoreCheekvalidation(Double score)throws InvalidInputException{
        if (score == null || score > 100|| score < 0) {
            throw new InvalidInputException("invalid input: ",null,null,score,INPUTYPE.DOUBLE);
        }
    }
    private void StringCheekValidation(String s)throws InvalidInputException{
        if (s == null || s.isBlank()) {
            throw new InvalidInputException("invalid",s,null,null,INPUTYPE.STRING);
        }
    }
    private LocalDateTime getLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnName);
        return (ts != null) ? ts.toLocalDateTime() : null;
    } 
    private void setTimestampParameter(PreparedStatement pstmt, int index, LocalDateTime dateTime) throws SQLException {
        if (dateTime != null) {
            pstmt.setTimestamp(index, Timestamp.valueOf(dateTime));
        } else {
            pstmt.setTimestamp(index, null);
        }
    }

    @Override
    public Integer addGrade(Grade grade,Connection conn) throws DAOException, BaseExceptionApp {
        String sql = " INSERT INTO grades (idstudent,subject,score,exam_date) VALUES (?, ?, ?, ?)";
        if (grade == null) {
            throw new InvalidInputException();
        }
        
        idCheekvalidation(grade.getStudentId());
        scoreCheekvalidation(grade.getScore());
        StringCheekValidation(grade.getSubject());

        try (PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, grade.getStudentId());
            pstmt.setString(2,grade.getSubject());
            pstmt.setDouble(3, grade.getScore());
            setTimestampParameter(pstmt, 4,grade.getExamDate());
            int row = pstmt.executeUpdate();
            if(row == 0){
                throw new DAOException("insert faild", null);
            }
            try(ResultSet rs = pstmt.getGeneratedKeys()){
                if (rs.next()) {
                    logger.info("grade inserted successfully");
                    return rs.getInt(1);
                }
            }
            logger.warning("insertion succeded but id not returned ,idStudent: " +grade.getStudentId()+" subject: "+grade.getSubject());
            throw new DAOException("insertion succeeded but id not returned.", null);
        } catch (SQLException e) {
            if(e.getSQLState() != null && e.getSQLState().equals("23503")){
                logger.log(Level.WARNING,"Student not founnd: " + grade.getStudentId(), e);
                throw new StudentNotFoundException(grade.getStudentId(),null);
            }
            logger.log(Level.SEVERE,"insertino faild",e);
            throw new DAOException("insertion faild", e);
        }
    }
    @Override
    public Integer addGrade(Grade grade) throws DAOException,BaseExceptionApp{
            try (Connection newConn = DBConnection.getConnection()) {
                return addGrade(grade, newConn);
            } catch(SQLException e){
                logger.log(Level.SEVERE,"insertion failed in simple insert operation", e);
                throw new DAOException(e.getMessage(), e);
            } catch(BaseExceptionApp f){
                logger.log(Level.WARNING, f.getMessage(), f);
                throw f;
            }
        }
    @Override
    public void hardDeleteGrade(Integer id) throws DAOException, BaseExceptionApp {
        String sql = "DELETE FROM  grades WHERE id = ?";
        idCheekvalidation(id);
        try (Connection conn = DBConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            int row = pstmt.executeUpdate();
            if (row == 0) {
                throw new DAOException("delete faild: no row effecetd", null);
            }
            logger.info("grade id "+id+"removed successefully.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"delete faild ", e);
            throw new DAOException("delete faild error", e);
        }
    }
    @Override
    public void updatescore(Integer idgrade, Double score) throws DAOException, BaseExceptionApp {// since we have 1-N relation and N is cardinality of  grade to student so  we just can use idstudent because it will be unique by default but in gerneal we have N-N so other talbe need to create.for relation
        String sql = "UPDATE grades SET score = ? WHERE  idstudent = ?";
        idCheekvalidation(idgrade);
        scoreCheekvalidation(score);
        try (Connection conn =DBConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(2, idgrade);
            pstmt.setDouble(1, score);
            int row = pstmt.executeUpdate();
            if (row == 0 ) {
                throw new DAOException("update failde : no row effected", null);
            }
            logger.info("student id + "+idgrade+"new score update: "+score);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"update faild ", e);
            throw new DAOException("update faild error", e);
        }
    }
    @Override
    public void updatesExamDate(Integer id,LocalDateTime tmp) throws DAOException, BaseExceptionApp {
        String sql = "UPDATE grades SET exam_date = ? WHERE id = ?";
        idCheekvalidation(id);
        if(tmp ==null){
            throw new InvalidInputException();
        }
        Timestamp tm = Timestamp.valueOf(tmp);
        try (Connection conn = DBConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(2, id);
            pstmt.setTimestamp(1, tm);
            int row = pstmt.executeUpdate();
            if (row == 0) {
                throw new DAOException("update faild : no row effected" , null);
            }
            DateTimeFormatter  ftm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fromatedTime = tmp.format(ftm);
            logger.info("grade id + "+id+" new exam date: "+fromatedTime);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"update faild ", e);
            throw new DAOException("update faild error", e);
        }
    }
    @Override
    public Optional<GradeBase> findGradeSById(Integer id) throws DAOException, BaseExceptionApp {
        String sql = "SELECT id,subject,score,exam_date FROM  grades  WHERE id = ?";
        idCheekvalidation(id);
        Integer idgrade = null;
        String subject = null;
        Double score = null;
        LocalDateTime examDate = null;
        GradeBase tempGradeBase = null;
        try (Connection conn = DBConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    idgrade = rs.getInt("id");
                    subject = rs.getString("subject");
                    score = rs.getDouble("score");
                    examDate = getLocalDateTime(rs, "exam_date");
                    tempGradeBase = new GradeBase(idgrade, subject,score);
                    tempGradeBase.setExamDate(examDate);
                    logger.info("grade id "+ id+" was found.");;
                    return Optional.of(tempGradeBase);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"read form database faild ", e);
            throw new DAOException("read faild error: id:"+id, e);
        }
    }
    @Override
    public Optional<Grade> findStudentGradeById(String subject, Integer studentId) throws DAOException, BaseExceptionApp {
        String sql = "SELECT * FROM grades WHERE subject = ? AND idstudent = ?";
        StringCheekValidation(subject);
        idCheekvalidation(studentId);
        try (Connection conn = DBConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, subject);
            pstmt.setInt(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractGradeFromResultSet(rs));
                }   
            }
            logger.warning("retrival was failed for grade subject: "+subject+" student id :"+studentId);
            return Optional.empty();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "retrival was faild", e);
            throw new DAOException("retrival was faild", e);
        }
    }
    @Override
    public List<Grade> findAllGrade() throws DAOException, BaseExceptionApp {
        String sql = "SELECT * FROM grades";
        List<Grade> listtemp = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery() ) {
            while (rs.next()) { 
                listtemp.add(extractGradeFromResultSet(rs));
            }
            return listtemp;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"read from data base is faild", e);
            throw new DAOException("read from data base is faild", e);
        }
    }
    private Grade extractGradeFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int studentId = rs.getInt("idstudent");
        String subject = rs.getString("subject");
        double score = rs.getDouble("score");
        Timestamp timestamp = rs.getTimestamp("exam_date");
        
        Grade grade = new Grade(id, studentId, subject, score);
        if (timestamp != null) {
            grade.setExamDate(timestamp.toLocalDateTime());
        }
        
        return grade;
    }
    
    @Override
    public List<Grade> findStudentListGradeById(Integer id) throws DAOException, BaseExceptionApp {
        idCheekvalidation(id);
        
        String sql = "SELECT * FROM grades WHERE idstudent = ? ORDER BY subject";
        List<Grade> grades = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGradeFromResultSet(rs));
                }
            }
            
            logger.info("Retrieved " + grades.size() + " grades for student " + id);
            return grades;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find grades for student " + id, e);
            throw new DAOException("Database query failed", e);
        }
    }
    public List<Grade> findGradesBySubject(String subject) throws DAOException, BaseExceptionApp {
        StringCheekValidation(subject);
        
        String sql = "SELECT * FROM grades WHERE subject = ? ORDER BY score DESC";
        List<Grade> grades = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, subject);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGradeFromResultSet(rs));
                }
            }
            
            logger.info("Retrieved " + grades.size() + " grades for subject: " + subject);
            return grades;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to find grades for subject: " + subject, e);
            throw new DAOException("Database query failed", e);
        }
    }
}
