/* Name: Henry Baker
 * Date: 2/10/2023
 * 
 * Class RegistrationSystem allows authorized users to access and modify student
 * data in a database. With a student's ID number, a user can get the student's
 * transcript, check degree requirements, and enroll or unenroll the student in a
 * course. 
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class RegistrationSystem {
    private Database database;
    private Scanner scanner;
    private Student student;

    /* Initializes the database with the given user info. */
    public RegistrationSystem(String user_id, String password, String connectionString) {
        database = new JdbcDatabase(user_id, password, connectionString);
        scanner = new Scanner(System.in);
    }

    /* Prompts the user for student ID, welcomes the user and displays an 
     * operations menu with options for get transcript, check degree requirements,
     * add course, remove course, and exit. */
    public void run() {
        try 
        {   
            StudentDAO studentDAO = new StudentDAO(database);
            welcomeUser();
            String studentID = getStudentID();
            student = studentDAO.getStudentById(Integer.valueOf(studentID));
            System.out.println("Welcome " + student.getName() + "!");
            boolean exit = false;
            while (!exit) {
                System.out.println();
                String optionNumber = operationMenu();
                switch(optionNumber) {
                    case "1":
                        getTranscript(studentID);
                        break;
                    case "2":
                        degreeRequirements(student);
                        break;
                    case "3":
                        addCourse(studentID);
                        break;
                    case "4":
                        removeCourse(studentID);
                        break;
                    case "5":
                        System.out.println("Goodbye...");
                        exit = true;
                        break;
                }
            }
        }
        catch (SQLException sqle) {
            System.out.println("Error executing query! " + sqle);
        }
    }

    /* Initial welcome message when programs starts. */
    private void welcomeUser() {
        System.out.println("=".repeat(29));
        System.out.println("Welcome to the registration system!");
    }

    /* Continuously prompts the user for their student ID until a 
     * valid ID is entered, then returns the ID. */
    private String getStudentID() throws SQLException {
        boolean studentExists = false;
        String studentID = "";
        while (!studentExists) {
            System.out.println();
            System.out.print("Please enter your student id: ");
            studentID = scanner.nextLine();
            String sql = DatabaseConstants.selectStudentNameById;
            PreparedStatement statement = database.getPreparedStatement(sql);
            statement.setString(1,studentID);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                studentExists = true;
            } else {
                System.out.println("Student not found, please try again...");
            }
        }
        return studentID;
    }
    
    /* Displays the operation menu to the user and prompts
     * them to choose an option by inputing a number. Returns
     * the input. */
    public String operationMenu() {
        int menuLength = 29;
        System.out.println("=".repeat(menuLength));
        System.out.println("Select an operation:");
        System.out.println("-".repeat(menuLength));
        System.out.println("1) Get Transcript");
        System.out.println("2) Check Degree Requirements");
        System.out.println("3) Add Course");
        System.out.println("4) Remove Course");
        System.out.println("5) Exit");
        System.out.println("=".repeat(menuLength));
        String input = scanner.nextLine();
        System.out.println();
        return input;
    }

    /* Queries the database for the student's transcript and prints the 
     * resulting table. */
    private void getTranscript(String studentID) throws SQLException{
        String sql = DatabaseConstants.selectTranscriptById;
        PreparedStatement statement = database.getPreparedStatement(sql);
        statement.setString(1,studentID);
        ResultSet result = statement.executeQuery();
        Table transcript = new Table(result);
        transcript.printTable();
    }
    
    /* Queries the database for the student's remaining degree requirements
     * and prints the resulting table. */
    private void degreeRequirements(Student student) throws SQLException {
        String sql = DatabaseConstants.selectDegreeRequirementsById;
        PreparedStatement statement = database.getPreparedStatement(sql);
        int studentID = student.getId();
        statement.setInt(1,studentID);
        statement.setString(2,student.getDepartmentName());
        ResultSet result = statement.executeQuery();
        Table degreeRequirements = new Table(result);
        degreeRequirements.printTable();
    }

    /* Prompts user for semester, year, and choice from a list of 
     * available courses, then removes the specified section from the
     * student's enrollment if they are enrolled in the course. */
    private void addCourse(String studentID) throws SQLException {     
        updateCourse(studentID, "add");
    }

    /* Prompts user for semester, year, and choice from a list of 
     * available courses, then unenrolls the student with the given
     * ID in that course if they meet the requirements. */
    private void removeCourse(String studentID) throws SQLException {     
        updateCourse(studentID, "remove");
    }

    /* Prompts user for semester, year, and choice from a list of 
     * available courses, then either enrolls or unenrolls the 
     * student in the selected course if possible. */
    private void updateCourse(String studentID, String addOrRemove) throws SQLException{
        String semester = getSemester();
        System.out.println("Select a year:");
        String year = scanner.nextLine();
        System.out.println();
        System.out.println("Select a section to "+ addOrRemove +":");
        ArrayList<String> selectedCourse = getCourse(semester, year, addOrRemove.equals("remove"));
        String courseID = selectedCourse.get(1);
        boolean alreadyEnrolled = alreadyEnrolled(courseID, semester, year);
        boolean meetsPrereqs = meetsPrereqs(courseID);
        switch (addOrRemove) {
            case "add":
                if (canAdd(alreadyEnrolled, meetsPrereqs)) {
                    String sectionID = selectedCourse.get(2); 
                    String sql = DatabaseConstants.insertCourse;
                    PreparedStatement statement = database.getPreparedStatement(sql);
                    statement.setString(1,studentID);
                    statement.setString(2,courseID);
                    statement.setString(3,sectionID);
                    statement.setString(4,semester);
                    statement.setString(5,year);
                    statement.executeUpdate();
                    System.out.println("Course added.");
                }
                break;
            case "remove":
                if (canRemove(alreadyEnrolled)) {
                    String sql = DatabaseConstants.removeCourse;
                    PreparedStatement statement = database.getPreparedStatement(sql);
                    statement.setString(1,courseID);
                    statement.setString(2,studentID);
                    statement.setString(3,semester);
                    statement.setString(4,year);
                    statement.executeUpdate();
                    System.out.println("Course removed.");
                } 
                break;
        }
    }

    /* Prompts the user to select a semester and returns the selection. */
    private String getSemester() {
        System.out.println("=".repeat(29));
        System.out.println("Select a semester:");
        System.out.println("-".repeat(29));
        System.out.println("1) Fall");
        System.out.println("2) Spring");
        System.out.println("3) Summmer");
        System.out.println("=".repeat(29));
        String input = scanner.nextLine();
        System.out.println();
        switch (input) {
            case "1":
                return "Fall";
            case "2":
                return "Spring";
            case "3":
                return "Summer";
        }
        return null;
    }
    
    /* Returns the tuple containing information about the course
     * selected by the user. */
    private ArrayList<String> getCourse(String semester, String year, boolean taking) throws SQLException {
        String sql = "";
        if (taking) {
            sql = DatabaseConstants.selectEnrolledSections;
        } else {
            sql = DatabaseConstants.selectAvailableSections;
        }
        PreparedStatement statement = database.getPreparedStatement(sql);
        statement.setString(1,semester);
        statement.setString(2,year);
        if(taking) {
            statement.setInt(3,student.getId());
        }
        ResultSet result = statement.executeQuery();
        Table sections = new Table(result);
        sections.printSelectionTable();
        int selectionNumber = Integer.valueOf(scanner.nextLine());
        System.out.println();
        return sections.getTuple(selectionNumber);
    }

    /* Returns true if the student is already enrolled in the course
     * with the given attributes, returns false otherwise. */
    private boolean alreadyEnrolled(String courseID, String semester, String year) throws SQLException {
        String sql = DatabaseConstants.alreadyEnrolledInCourse;
        PreparedStatement statement = database.getPreparedStatement(sql);
        statement.setString(1,courseID);
        statement.setInt(2,student.getId());
        statement.setString(3,semester);
        statement.setString(4, year);
        ResultSet result = statement.executeQuery();
        return result.next();
    }

    /* Returns true if the student meets the prerequisites for the course
     * with the given attributes, returns false otherwise. */
    private boolean meetsPrereqs(String courseID) throws SQLException {
        String sql = DatabaseConstants.selectRemainingPrereqs;
        PreparedStatement statement = database.getPreparedStatement(sql);
        statement.setString(1,courseID);
        statement.setInt(2,student.getId());
        ResultSet result = statement.executeQuery();
        return !result.next();
    }
    
    /* Returns true if the requirements for adding a course are met. */
    private boolean canAdd(boolean alreadyEnrolled, boolean meetsPrereqs) {
        if (alreadyEnrolled) {
            System.out.println("You are already enrolled in this course!");
            return false;
        } else if (!meetsPrereqs) {
            System.out.println("You do not meet the prerequisites for this course!");
            return false;
        }
        return true;
    }

    /* Returns true if the requirements for removing a course are met. */
    private boolean canRemove(boolean alreadyEnrolled) {
        if (!alreadyEnrolled) {
            System.out.println("You are not currently enrolled in this course!");
            return false;
        }
        return true;
    }
}
