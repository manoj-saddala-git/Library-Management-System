import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IssueBookForm extends JFrame {

    private JTextField studentIdField, bookIdField, issueDateField, returnDateField;
    private JButton checkEligibilityButton, issueButton, backButton;
    private String librarianId;

    public IssueBookForm(String librarianId) {
        this.librarianId = librarianId;

        setTitle("Issue Book");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        panel.add(studentIdField);

        panel.add(new JLabel("Book ID:"));
        bookIdField = new JTextField();
        panel.add(bookIdField);

        panel.add(new JLabel("Issue Date:"));
        issueDateField = new JTextField();
        issueDateField.setEditable(false);
        panel.add(issueDateField);

        panel.add(new JLabel("Return Date:"));
        returnDateField = new JTextField();
        returnDateField.setEditable(false);
        panel.add(returnDateField);

        checkEligibilityButton = new JButton("Check Eligibility");
        issueButton = new JButton("Issue Book");
        issueButton.setEnabled(false); // Disable until eligibility is confirmed
        backButton = new JButton("Back");

        panel.add(checkEligibilityButton);
        panel.add(issueButton);
        panel.add(new JLabel()); // Empty cell
        panel.add(backButton);

        add(panel);

        setDefaultDates();

        checkEligibilityButton.addActionListener(e -> checkEligibility());
        issueButton.addActionListener(e -> issueBook());
        backButton.addActionListener(e ->
                dispose());

        setVisible(true);
    }

    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar issueCal = Calendar.getInstance();
        Calendar returnCal = Calendar.getInstance();
        returnCal.add(Calendar.DAY_OF_MONTH, 20);

        issueDateField.setText(sdf.format(issueCal.getTime()));
        returnDateField.setText(sdf.format(returnCal.getTime()));
    }

    private void checkEligibility() {
        String studentId = studentIdField.getText().trim();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Student ID.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSystem", "root", "Mysql@1226");
             PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM issue WHERE student_id = ? AND return_status = 'pending'");
             PreparedStatement fineStmt = conn.prepareStatement("SELECT COUNT(*) FROM issue WHERE student_id = ? AND return_status = 'pending' AND return_date < CURDATE()")) {

            // Check number of books issued
            checkStmt.setString(1, studentId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int issuedBooks = rs.getInt(1);

            if (issuedBooks >= 3) {
                JOptionPane.showMessageDialog(this, "Student already has 3 books issued.");
                issueButton.setEnabled(false);
                return;
            }

            // Check overdue books
            fineStmt.setString(1, studentId);
            ResultSet fineRs = fineStmt.executeQuery();
            fineRs.next();
            int overdue = fineRs.getInt(1);

            if (overdue > 0) {
                JOptionPane.showMessageDialog(this, "Student has overdue books. ₹50 fine must be paid.");
                issueButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Student is eligible to borrow a book.");
                issueButton.setEnabled(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void issueBook() {
        String studentId = studentIdField.getText().trim();
        String bookId = bookIdField.getText().trim();
        String issueDate = issueDateField.getText().trim();
        String returnDate = returnDateField.getText().trim();

        if (studentId.isEmpty() || bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSystem", "root", "Mysql@1226");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO issue (student_id, book_id, issue_date, return_date, return_status) VALUES (?, ?, ?, ?, 'pending')")) {

            insertStmt.setString(1, studentId);
            insertStmt.setString(2, bookId);
            insertStmt.setString(3, issueDate);
            insertStmt.setString(4, returnDate);

            int result = insertStmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Book issued successfully!");
                studentIdField.setText("");
                bookIdField.setText("");
                issueButton.setEnabled(false); // Reset eligibility
                setDefaultDates();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to issue book.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
