import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class testdb {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/testdb"; // Thay testdb bằng tên CSDL của bạn nếu khác
        String user = "root";
        String password = ""; // Mặc định XAMPP không có mật khẩu cho root

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Kết nối thành công đến MySQL!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Kết nối thất bại!");
            e.printStackTrace();
        }
    }
}
