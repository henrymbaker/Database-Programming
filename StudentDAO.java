import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    private Database database;

    public StudentDAO(Database database)
    {
        this.database = database;
    }

    public Student getStudentById(int id)
    {
        try {
            PreparedStatement statement = database.getPreparedStatement(DatabaseConstants.selectStudentById);
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if(result.next()){
                Student studentById = hydrateStudent(result);

                return studentById;
            }
        }
        catch(SQLException e){

        }

        return null;
    }

    private Student hydrateStudent(ResultSet result) throws SQLException
    {
        Student student = new Student();
        student.setId(result.getInt("id"));
        student.setName(result.getString("name"));
        student.setDepartmentName(result.getString("dept_name"));
        student.setTotalCredits(result.getFloat("tot_cred"));

        return student;
    }
}
