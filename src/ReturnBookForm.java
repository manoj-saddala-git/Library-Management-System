import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnBookForm extends JFrame {

    private JTextField studentIdField, bookIdField;
    private JButton returnButton, backButton, searchButton;
    private JLabel studentInfoLabel;
    private JTable issuedBooksTable;
    private DefaultTableModel tableModel;
    private JFrame parentFrame; // reference to the previous screen

    public ReturnBookForm(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        setTitle("Return Book");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Enter Student ID:"));
        studentIdField = new JTextField(10);
        topPanel.add(studentIdField);
        searchButton = new JButton("Search");
        topPanel.add(searchButton);

        // Student Info Label
        studentInfoLabel = new JLabel(" ");
        topPanel.add(studentInfoLabel);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Book ID", "Issue Date", "Return Date", "Return Status", "Fine"}, 0);
        issuedBooksTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(issuedBooksTable);

        // Return Section
        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        returnPanel.add(new JLabel("Enter Book ID to return:"));
        bookIdField = new JTextField(10);
        returnPanel.add(bookIdField);

        returnButton = new JButton("Return Book");
        backButton = new JButton("Back");
        returnPanel.add(returnButton);
        returnPanel.add(backButton);

        // Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(returnPanel, BorderLayout.SOUTH);

        // Actions
        searchButton.addActionListener(e -> fetchIssuedBooks());
        returnButton.addActionListener(e -> returnBook());
        backButton.addActionListener(e -> {
            this.dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });

        setVisible(true);
    }

    private void fetchIssuedBooks() {
        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID.");
            return;
        }

        tableModel.setRowCount(0); // Clear previous rows
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSystem", "root", "Mysql@1226")) {
            // Get student name
            String studentName = "";
            PreparedStatement studentStmt = con.prepareStatement("SELECT name FROM students WHERE student_id = ?");
            studentStmt.setString(1, studentId);
            ResultSet studentRs = studentStmt.executeQuery();
            if (studentRs.next()) {
                studentName = studentRs.getString("name");
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.");
                return;
            }

            studentInfoLabel.setText("Student ID: " + studentId + " | Name: " + studentName);

            PreparedStatement ps = con.prepareStatement(
                    "SELECT book_id, issue_date, return_date, return_status FROM issue WHERE student_id = ?"
            );
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();

            boolean hasBooks = false;
            while (rs.next()) {
                hasBooks = true;
                String bookId = rs.getString("book_id");
                String issueDate = rs.getString("issue_date");
                String returnDateStr = rs.getString("return_date");
                String status = rs.getString("return_status");

                LocalDate returnDate = LocalDate.parse(returnDateStr);
                LocalDate today = LocalDate.now();
                long overdueDays = ChronoUnit.DAYS.between(returnDate, today);
                String fine = (status.equalsIgnoreCase("pending") && overdueDays > 0) ? "₹50" : "₹0";

                tableModel.addRow(new Object[]{bookId, issueDate, returnDateStr, status, fine});
            }

            if (!hasBooks) {
                JOptionPane.showMessageDialog(this, "No books are taken by this student \"" + studentName + "\".");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void returnBook() {
        String studentId = studentIdField.getText().trim();
        String bookId = bookIdField.getText().trim();

        if (studentId.isEmpty() || bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Student ID and Book ID.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSystem", "root", "Mysql@1226")) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT return_date FROM issue WHERE student_id = ? AND book_id = ? AND return_status = 'pending'"
            );
            ps.setString(1, studentId);
            ps.setString(2, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String returnDateStr = rs.getString("return_date");
                LocalDate returnDate = LocalDate.parse(returnDateStr);
                LocalDate today = LocalDate.now();

                long daysOverdue = ChronoUnit.DAYS.between(returnDate, today);
                boolean isOverdue = daysOverdue > 0;

                // Update return_status
                ps = con.prepareStatement(
                        "UPDATE issue SET return_status = 'success' WHERE student_id = ? AND book_id = ? AND return_status = 'pending'"
                );
                ps.setString(1, studentId);
                ps.setString(2, bookId);
                int updated = ps.executeUpdate();

                if (updated > 0) {
                    if (isOverdue) {
                        JOptionPane.showMessageDialog(this, "Book returned. Fine: ₹50 for overdue.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Book returned successfully.");
                    }
                    bookIdField.setText("");
                    fetchIssuedBooks(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating return.");
                }

            } else {
                JOptionPane.showMessageDialog(this, "No such issued book found or already returned.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
