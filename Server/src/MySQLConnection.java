import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

public class MySQLConnection {
    private static String classForName, url, username, password;
    private static Stack<Connection> connPools;
    static {
        connPools = new Stack<Connection>();
        classForName = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/chinese_chess?useUnicode=yes&characterEncoding=UTF-8";
        username = "root";
        password = "quanvantruong1";
        try {
            Class.forName(classForName);
        } 	catch (Exception e) {e.printStackTrace();}
    }
    public static Connection connect() throws SQLException {
        Connection conn = null;
        if (connPools.empty()) conn = DriverManager.getConnection(url, username, password);
        else conn = connPools.pop();
        return conn;
    }
}