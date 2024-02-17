import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcDatabase implements Database {

    private Connection connection;
    private String userId;
    private String userPassword;
    private String connectionString;

    public JdbcDatabase(String userId, String userPassword,String connectionString) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.connectionString = connectionString;
    }

    public JdbcDatabase() {
        this(DatabaseConstants.JdbcConnectionString, DatabaseConstants.JdbcUserId, 
            DatabaseConstants.JdbcPassword);
    }
    

    private void connect() {
        try {
            boolean isConnected = isConnected();

            if(!isConnected) {
                connection = DriverManager.getConnection(connectionString, userId, userPassword);
            }
        }
        catch (SQLException sqle) {
            throw new RuntimeException("Error connecting to database!", sqle);
        }
    }

    private boolean isConnected() throws SQLException {
        return (connection != null) && !connection.isClosed();
    }

    public ResultSet runQuery(String sql) {
        connect();

        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            return result;
        }
        catch (SQLException sqle) {
            throw new RuntimeException("Error executing query!", sqle);
        }
    }

    public void runUpdate(String sql) {
        connect();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        }
        catch (SQLException sqle) {
            throw new RuntimeException("Error executing update!", sqle);
        }
    }

    public PreparedStatement getPreparedStatement(String sql) {
        connect();

        try {
            return connection.prepareStatement(sql);
        }
        catch (SQLException sqle) {
            throw new RuntimeException("Error creating prepared statement!", sqle);
        }
    }
}