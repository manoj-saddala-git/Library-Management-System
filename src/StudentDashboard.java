

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentDashboard extends JFrame {
    private JTable table;

    public StudentDashboard(String studentId, String studentName) {
        setTitle("Student Dashboard");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Welcome, " + studentName + " (ID: " + studentId + ")");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        fetchIssuedBooks(studentId);

        setLayout(new BorderLayout());
        add(nameLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void fetchIssuedBooks(String studentId) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Book ID", "Book Name", "Issue Date", "Due Date", "Return Status", "Fine"});

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSystem", "root", "Mysql@1226");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT b.book_id, b.title, i.issue_date, i.return_date, i.return_status, " +
                             "CASE WHEN i.return_status = 'pending' AND i.return_date < CURDATE() THEN '₹50' ELSE '₹0' END AS fine " +
                             "FROM issue i JOIN books b ON i.book_id = b.book_id WHERE i.student_id = ?")) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date"),
                        rs.getString("return_status"),
                        rs.getString("fine")
                });
            }

            table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


