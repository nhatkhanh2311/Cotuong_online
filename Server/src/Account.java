import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account
{
    public static String Signin(String user, String pass) throws SQLException
    {
        Connection conn = MySQLConnection.connect();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM player WHERE username = ? AND password = ?");
        ps.setString(1, user);
        ps.setString(2, pass);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) return rs.getString("username");
        return null;
    }
    public static String Signup(String user, String pass, String repass, String email) throws SQLException
    {
        if (exist(user)) return "exist";
        if (!pass.equals(repass)) return "notsame";
        Connection conn = MySQLConnection.connect();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO player(username, password, played) VALUES(?, ?, 0)");
        ps.setString(1, user);
        ps.setString(2, pass);
        ps.execute();
        return "ok";
    }
    public static boolean exist(String user) throws SQLException
    {
        Connection conn = MySQLConnection.connect();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM player WHERE username = ?");
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) return true;
        return false;
    }
}