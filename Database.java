import java.sql.*;

public interface Database {
    ResultSet runQuery(String sql);
    void runUpdate(String sql);
    PreparedStatement getPreparedStatement(String sql);
}
