package com.gradeapp.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gradeapp.dao.GradeDAO;
import com.gradeapp.dao.StudentDAO;
import com.gradeapp.exception.BaseExceptionApp;
import com.gradeapp.exception.DAOException;
import com.gradeapp.exception.GradeNotFoundException;
import com.gradeapp.exception.InvalidInputException;
import com.gradeapp.exception.StudentNotFoundException;
import com.gradeapp.exception.InvalidInputException.INPUTYPE;
import com.gradeapp.model.Grade;
import com.gradeapp.model.Student;
import com.gradeapp.util.AppLogger;
import com.gradeapp.util.DBConnection;

public class GradeService {
    private static final Logger logger = AppLogger.getLogger(GradeService.class);
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;
    public GradeService(GradeDAO g,StudentDAO s){
        gradeDAO = g;
        studentDAO = s;
    }

    public Double calculateAverageScore(Integer idStudent)throws DAOException,BaseExceptionApp{
        Student s = studentDAO.findStudentById(idStudent).orElseThrow(()->new StudentNotFoundException(idStudent, null));
        List<Grade> list = gradeDAO.findStudentListGradeById(idStudent);
        Double avrg = list.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
        logger.info("student id: "+s.getId()+" name: "+s.getName()+" was found ,with average score: "+avrg);
        return avrg;
    }
    public Optional<Double> getHighestscore(Integer idStudent) throws DAOException,BaseExceptionApp{
        Student s = studentDAO.findStudentById(idStudent).orElseThrow(()->new StudentNotFoundException(idStudent, null));
        List<Grade> list = gradeDAO.findStudentListGradeById(idStudent);
        Optional<Double> maxscore = list.stream().mapToDouble(Grade::getScore).boxed().max(Double::compare);

        maxscore.ifPresentOrElse(score -> 
         logger.info("student id: "+s.getId()+" name: "+s.getName()+" was found, with max score: "+score),
         () -> logger.info("student id: "+s.getId()+" name: "+s.getName()+" was found, with no grades")
        );
        return maxscore;
        }
        public Optional<Double> getLowestscore(Integer idStudent) throws DAOException,BaseExceptionApp{
        Student s = studentDAO.findStudentById(idStudent).orElseThrow(()->new StudentNotFoundException(idStudent, null));
        List<Grade> list = gradeDAO.findStudentListGradeById(idStudent);
        Optional<Double> minscore = list.stream().mapToDouble(Grade::getScore).boxed().min(Double::compare);

        minscore.ifPresentOrElse(score -> 
         logger.info("student id: "+s.getId()+" name: "+s.getName()+" was found, with max score: "+score),
         () -> logger.info("student id: "+s.getId()+" name: "+s.getName()+" was found, with no grades")
        );

        return minscore;
        }
        public long contGradesstudent(Integer idstudent)throws DAOException,BaseExceptionApp{
            Student s = studentDAO.findStudentById(idstudent).orElseThrow(()->new StudentNotFoundException(idstudent, null));
            List<Grade> list = gradeDAO.findStudentListGradeById(idstudent);
            
            long gradeNumbers = list.size();
            logger.info("student id: "+s.getId()+" name: "+s.getName()+" was found,with "+gradeNumbers+" grades number");
            return gradeNumbers;
        }
        
        public Double calculateSubjectAverage(String subject)throws DAOException,BaseExceptionApp{
            List<Grade> list = gradeDAO.findGradesBySubject(subject);
            double avrg = list.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
            logger.info("subject: "+subject+" avaerage: "+avrg);
            return avrg;
        }
        public Map<String,Double> getAllsubjectAverages()throws DAOException,BaseExceptionApp{
            List<Grade> list = gradeDAO.findAllGrade();
            if (list == null|| list.isEmpty()) {
                logger.info("No grades found to calculate averages");
                return new HashMap<>();
            }
            Map<String,Double> mapsubject = list.stream().collect(Collectors.groupingBy(Grade::getSubject,Collectors.averagingDouble(Grade::getScore)));
            mapsubject.forEach((subject,average)->logger.info("Subject: "+subject+"-Average: "+average));
            logger.info("total subjects found "+mapsubject.size());
            return mapsubject;
        }
        public List<String> getFailingSubjects(Double threshold)throws DAOException,BaseExceptionApp{
            Map<String,Double> tempMap = getAllsubjectAverages();
            if (tempMap.isEmpty()) {
                logger.info("empthy grade-average map.");
                return new ArrayList<String>();
            }
            if (threshold == null || threshold <0) {
                throw new InvalidInputException("invalid input",null,null,threshold,INPUTYPE.DOUBLE);
            }
            List<String> list = tempMap.entrySet().stream().filter(x-> x.getValue() < threshold).map(Map.Entry::getKey).toList();
            logger.info("failing subject for threshold: "+ threshold);
            list.forEach(x->logger.info("subject: "+x));
            return list;
        }
        public List<Student> getTopStudents(Integer limit)throws DAOException,BaseExceptionApp{
            if (limit == null|| limit <=0) {
                throw new InvalidInputException("invalid input",null,limit,null,null);
            }
            List<Student> listStudents = studentDAO.findAllStudent();
            List<Grade> listgGrades = gradeDAO.findAllGrade();
            if (listStudents.isEmpty()) {
                logger.info("no students found");
                return new ArrayList<>();
            }
            if (listgGrades.isEmpty()) {
                logger.info("no grades found");
                return new ArrayList<>();
            }

             Map<Integer, Student> studentMap = listStudents.stream()
            .collect(Collectors.toMap(Student::getId, student -> student));

            List<Student> topStudents = listgGrades.stream()
            .collect(Collectors.groupingBy(
                Grade::getStudentId,
                Collectors.averagingDouble(Grade::getScore)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> studentMap.get(entry.getKey()))
            .filter(Objects::nonNull)
            .toList();
            return topStudents;
        }
        public List<Student> getStudentsWithNoGrades() throws DAOException,BaseExceptionApp{
            List<Grade> listGrades = gradeDAO.findAllGrade();
            List<Student> listsStudents = studentDAO.findAllStudent();
            Set<Integer> ids = listGrades.stream().map(Grade::getStudentId).collect(Collectors.toSet());
            List<Student> studentsNoGrade = listsStudents.stream().filter(s->!ids.contains(s.getId())).toList();
            logger.info("found student without grades "+studentsNoGrade.size());
            studentsNoGrade.forEach(student ->logger.info("Student without grades: ID={}, Name={}"+ student.getId()+ student.getName()));
            return studentsNoGrade;
        }


        public List<Student> rankStudentsBySubject(String subject) throws DAOException, BaseExceptionApp {
    
            List<Grade> subjectGrades = gradeDAO.findGradesBySubject(subject);
    
            if (subjectGrades.isEmpty()) {
                logger.info("No grades found for subject: " + subject);
                return new ArrayList<>();
            }
    
            List<Student> studentList = studentDAO.findAllStudent();
            Map<Integer, Student> studentMap = studentList.stream()
            .collect(Collectors.toMap(Student::getId, s -> s));
    
            List<Student> rankedStudents = subjectGrades.stream()
            .collect(Collectors.groupingBy(
                Grade::getStudentId,
                Collectors.maxBy(Comparator.comparing(Grade::getScore))
            ))
            .entrySet().stream()
            .filter(entry -> entry.getValue().isPresent())
            .sorted((e1, e2) -> Double.compare(
                e2.getValue().get().getScore(),
               e1.getValue().get().getScore()
            )).map(entry -> studentMap.get(entry.getKey()))
            .filter(Objects::nonNull)
            .toList();
    
        logger.info("Ranked " + rankedStudents.size() + " students for subject: " + subject);
        return rankedStudents;
}

        public int bulkAddGrades(List<Grade> grades) throws DAOException,BaseExceptionApp{
            Connection conn = null;
            int rows =0;
            try{
                conn =DBConnection.getConnection();
                conn.setAutoCommit(false);
                for (Grade g : grades) {
                        gradeDAO.addGrade(g, conn);
                        rows++;
                    } 
                conn.commit();
                logger.info("Transaction comitted successfully");
                return rows;
            }catch(SQLException e){
                if (conn != null) {
                    try{
                        conn.rollback();
                        logger.warning("transaction rolled back");
                    }catch(SQLException r){
                        logger.log(Level.SEVERE, "Rollback failed", r);                    
                    }
                }
                logger.log(Level.SEVERE, "Transaction failed", e);
                throw new DAOException("transsection faild", e);
            }finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "Failed to close connection", e);
                    }
                }
            }
        }

        public void transferGrades(Integer fromStudentId, Integer toStudentId) throws DAOException,BaseExceptionApp{
            if (fromStudentId == null || fromStudentId <= 0) {
                throw new InvalidInputException("Invalid source student ID: ",null,fromStudentId,null,null);
            }
            if (toStudentId == null || toStudentId <= 0) {
                throw new InvalidInputException("Invalid target student ID: ",null,toStudentId,null,null);
            }
            if (fromStudentId.equals(toStudentId)) {
                throw new InvalidInputException("Cannot transfer grades to same student",null,null,null,null);
            }

            List<Grade> grades = gradeDAO.findStudentListGradeById(fromStudentId);

            if (grades.isEmpty()) {
                logger.info("No grades to transfer from student " + fromStudentId);
                return;
            }

            List<Grade> toGrades = grades.stream().map(og->{
                Grade copy = new Grade(null,og.getStudentId(),og.getSubject(), og.getScore());//id should be null.
                copy.setExamDate(og.getExamDate());
                return copy;
            }).toList();

            bulkAddGrades(toGrades);

            logger.info("Transferred " + toGrades.size() + " grades from student " + fromStudentId + " to student " + toStudentId);
        }
        public Map<String, String> getGradeDistribution()throws DAOException,BaseExceptionApp{
            List<Grade> listAllGrades = gradeDAO.findAllGrade();
            Set<String> subjectset = listAllGrades.stream().map(Grade::getSubject).collect(Collectors.toSet());
            Map<String,Long> aMap = listAllGrades.stream().filter(g->g.getScore() <= 100 &&g.getScore()>=90).collect(Collectors.groupingBy(Grade::getSubject,Collectors.counting()));
            Map<String,Long> bMap = listAllGrades.stream().filter(g->g.getScore() <= 89 &&g.getScore()>=80).collect(Collectors.groupingBy(Grade::getSubject,Collectors.counting()));
            Map<String,Long> cMap = listAllGrades.stream().filter(g->g.getScore() <= 79 &&g.getScore()>=70).collect(Collectors.groupingBy(Grade::getSubject,Collectors.counting()));
            Map<String,Long> dMap = listAllGrades.stream().filter(g->g.getScore() <= 69 &&g.getScore()>=60).collect(Collectors.groupingBy(Grade::getSubject,Collectors.counting()));
            Map<String,Long> eMap = listAllGrades.stream().filter(g->g.getScore() <= 59 &&g.getScore()>=50).collect(Collectors.groupingBy(Grade::getSubject,Collectors.counting()));
            Map<String,Long> fMap = listAllGrades.stream().filter(g->g.getScore() <50).collect(Collectors.groupingBy(Grade::getSubject,Collectors.counting()));
            Map<String,String> subjectMapDistro = subjectset.stream().collect(Collectors.toMap(s->s,s->String.format("{A (100-90): %d,B (89,80): %d,C (79,70): %d,d (69,60): %d,e (59,50): %d,f (<50) :%d}",aMap.getOrDefault(s,0L),bMap.getOrDefault(s,0L),cMap.getOrDefault(s,0L),dMap.getOrDefault(s,0L),eMap.getOrDefault(s,0L),fMap.getOrDefault(s,0L))));
            return subjectMapDistro;
        }
        public List<Student> getStudentsBelowverage()throws DAOException,BaseExceptionApp{
            List<Student> studentsList = studentDAO.findAllStudent();
            List<Grade> gradesList = gradeDAO.findAllGrade();

            if (studentsList.isEmpty()) {
                throw new StudentNotFoundException("no student found.");
            }
            if (gradesList.isEmpty()) {
                throw new GradeNotFoundException("no grades found.");
            }
            double overallAverage = gradesList.stream()
            .mapToDouble(Grade::getScore)
            .average()
            .orElse(0.0);
    
            logger.info("Overall average: " + overallAverage);

            Map<Integer,Double> avergeperstudent = gradesList.stream()
                .collect(Collectors.groupingBy(
                    Grade::getStudentId,
                Collectors.averagingDouble(Grade::getScore)
                ));
            Map<Integer,Student> mapStudent = studentsList.stream().collect(Collectors.toMap(s->s.getId(),s->s));
            List<Student> blowAverage = avergeperstudent.entrySet().stream()
                .filter(entry -> entry.getValue() < overallAverage)
                .map(entry -> mapStudent.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            return blowAverage;         
        }

        public List<Student> getStudentsAboveAverage()throws DAOException,BaseExceptionApp{
            List<Student> studentsList = studentDAO.findAllStudent();
            List<Grade> gradesList = gradeDAO.findAllGrade();

            if (studentsList.isEmpty()) {
                throw new StudentNotFoundException("no student found.");
            }
            if (gradesList.isEmpty()) {
                throw new GradeNotFoundException("no grades found.");
            }

            double overallAverage = gradesList.stream()
                .mapToDouble(Grade::getScore)
                .average()
                .orElse(0.0);
    
            logger.info("Overall average: " + overallAverage);
            
            Map<Integer, Double> studentAverages = gradesList.stream()
                .collect(Collectors.groupingBy(
                    Grade::getStudentId,
                Collectors.averagingDouble(Grade::getScore)
                )); 
           Map<Integer, Student> studentMap = studentsList.stream()
            .collect(Collectors.toMap(Student::getId, s -> s));
    
            List<Student> aboveAverage = studentAverages.entrySet().stream()
                .filter(entry -> entry.getValue() > overallAverage)
                .map(entry -> studentMap.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    
            logger.info("Found " + aboveAverage.size() + " students above average");
            return aboveAverage;       
        }

        public List<Student> getMostImprovedStudents(Integer limit) throws DAOException,BaseExceptionApp{
            if (limit == null || limit <=0) {
                throw new InvalidInputException("invalid input",null,limit,null,null);
            }

            List<Student> studentsList = studentDAO.findAllStudent();
            List<Grade> gradesList = gradeDAO.findAllGrade();

            if (studentsList.isEmpty()) {
                throw new StudentNotFoundException("no student found.");
            }
            if (gradesList.isEmpty()) {
                throw new GradeNotFoundException("no grades found.");
            }
            gradesList.sort(Comparator.comparing(Grade::getExamDate));
            Map<Integer,Student> mapallstudent = studentsList.stream()
            .collect(Collectors.toMap(Student::getId,s->s));

            List<Student> studens = gradesList.stream()
                .collect(Collectors.groupingBy(
                    Grade::getStudentId,
                    Collectors.collectingAndThen(
                    Collectors.toList(),
                    studentGrades -> {
                        if (studentGrades.size() < 2) {
                            return 0.0;
                        }
                        
                        studentGrades.sort(Comparator.comparing(Grade::getExamDate));
                        
                        
                        Grade firstGrade = studentGrades.get(0);
                        Grade lastGrade = studentGrades.get(studentGrades.size() - 1);
                        
                        
                        return lastGrade.getScore() - firstGrade.getScore();
                        }
                    )
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 0) 
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed()) 
                .limit(limit).map(entry -> mapallstudent.get(entry.getKey())).filter(Objects::nonNull).collect(Collectors.toList());
            return studens;
        }
}

