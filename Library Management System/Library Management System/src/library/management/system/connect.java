package library.management.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database Connection Utility
 * Change DB_URL, USER, PASS to match your MySQL setup
 */
public class connect {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root123"; // <-- Change this to your MySQL password

    public static Connection connection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(connect.class.getName()).log(Level.SEVERE, "MySQL Driver not found!", ex);
        } catch (SQLException ex) {
            Logger.getLogger(connect.class.getName()).log(Level.SEVERE, "DB Connection failed!", ex);
        }
        return con;
    }
}
