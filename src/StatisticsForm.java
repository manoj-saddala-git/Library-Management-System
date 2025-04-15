import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StatisticsForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton backButton;
    private JFrame parent;

    public StatisticsForm(JFrame parent) {
        this.parent = parent;

        setTitle("Issued Book Statistics");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table columns
        String[] columnNames = {
                "Student ID", "Student Name", "Book ID", "Book Name",
                "Issue Date", "Return Date", "Return Status", "Fine"
        };

        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Back button panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back");
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Fetch and show data
        fetchAndDisplayData();

        // Back button logic
        backButton.addActionListener(e -> {
            dispose(); // close this window
            if (parent != null) {
                parent.setVisible(true); // go back to the previous window
            }
        });

        setVisible(true);
    }

    private void fetchAndDisplayData() {
        String url = "jdbc:mysql://localhost:3306/LibraryManagementSystem";
        String user = "root";
        String password = "Mysql@1226";

        String query = "SELECT i.student_id, s.name, i.book_id, b.title, " +
                "i.issue_date, i.return_date, " +
                "CASE WHEN i.returned = 1 THEN 'Success' ELSE 'Pending' END AS return_status, " +
                "CASE WHEN i.returned = 0 AND CURDATE() > i.return_date THEN '₹50' ELSE '₹0' END AS fine " +
                "FROM issue i " +
                "JOIN students s ON i.student_id = s.student_id " +
                "JOIN books b ON i.book_id = b.book_id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("student_id");
                row[1] = rs.getString("name");
                row[2] = rs.getString("book_id");
                row[3] = rs.getString("title");
                row[4] = rs.getDate("issue_date");
                row[5] = rs.getDate("return_date");
                row[6] = rs.getString("return_status");
                row[7] = rs.getString("fine");

                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving statistics.");
        }
    }

    // Use this main method only for standalone testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StatisticsForm(null));
    }
}
