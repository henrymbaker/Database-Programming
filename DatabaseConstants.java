/* Class DatabaseConstants stores SQL prepared statements used to access the database. */

public class DatabaseConstants {
    public static final String JdbcConnectionString = "jdbc:mysql://mysql.cs.wwu.edu:3306/bakerh22?useSSL=false";
    public static final String JdbcUserId = "bakerh22";
    public static final String JdbcPassword = ",KY3Tuh8cCS";

    public static final String selectStudentById = "SELECT * FROM student WHERE ID=?";
    public static final String selectStudentNameById = "SELECT name FROM student WHERE ID=?";

    public static final String selectTranscriptById = 
        "SELECT takes.course_id, course.title, takes.semester, takes.year, takes.grade, course.credits " + 
        "FROM student, takes, course "+
        "WHERE student.ID = takes.ID "+
        "AND takes.course_id = course.course_id " +
        "AND student.ID =? "+
        "ORDER BY takes.year, takes.semester";

    public static final String selectDegreeRequirementsById = 
        "SELECT DISTINCT course.course_id, course.title "+
        "FROM student, takes, course "+
        "WHERE course.course_id NOT IN (SELECT course_id FROM takes WHERE takes.ID = ?) "+
        "AND student.ID = takes.ID "+
        "AND takes.course_id = course.course_id "+
        "AND course.dept_name =? ";
    
    public static final String selectAvailableSections = 
        "SELECT course_id, sec_id " +
        "FROM section "+
        "WHERE semester =? "+
        "AND year =? ";
    
    public static final String selectEnrolledSections = 
        "SELECT course_id, sec_id " +
        "FROM takes "+
        "WHERE semester =? "+
        "AND year =? " +
        "AND takes.ID = ?";

    public static final String selectRemainingPrereqs = 
        "SELECT prereq_id " +
        "FROM prereq "+
        "WHERE prereq.course_id =? " +
        "AND prereq.prereq_id NOT IN (SELECT course_id FROM takes WHERE takes.ID =?)";
    
    public static final String alreadyEnrolledInCourse = 
        "SELECT course_id " +
        "FROM takes " +
        "WHERE course_id =? " +
        "AND ID =? " +
        "AND semester =? " +
        "AND year =?";

    public static final String insertCourse = "INSERT INTO takes VALUES (?,?,?,?,?,null)";
    public static final String removeCourse = 
        "DELETE FROM takes WHERE course_id = ? " + 
        "AND ID = ? AND semester = ? AND year = ?";
}
