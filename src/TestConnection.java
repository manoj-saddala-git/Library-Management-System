import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("✅ Database connected successfully!");
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
