package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private static final String URL = "jdbc:mysql://localhost:3306/pethomedb"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối MySQL thành công!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Kết nối thất bại: " + e.getMessage());
        }
        return conn;
    }
}
