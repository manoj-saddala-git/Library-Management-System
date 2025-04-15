import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class AddStudentForm extends JFrame {
    private JTextField studentIdField, nameField, gmailField, phoneField;
    private JComboBox<String> courseBox, branchBox;
    private JPasswordField passwordField;

    public AddStudentForm() {
        setTitle("Add New Student");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2, 10, 10));
        setResizable(false);

        add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        add(studentIdField);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Course:"));
        courseBox = new JComboBox<>(new String[]{"B.Tech", "M.Tech", "MBA"});
        add(courseBox);

        add(new JLabel("Branch:"));
        branchBox = new JComboBox<>(new String[]{"CSE", "ECE", "EEE", "Mechanical", "Civil Engineering", "Not Applicable"});
        add(branchBox);

        add(new JLabel("Gmail:"));
        gmailField = new JTextField();
        add(gmailField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton addButton = new JButton("Add Student");
        JButton backButton = new JButton("Back");

        add(addButton);
        add(backButton);

        addButton.addActionListener(e -> addStudent());
        backButton.addActionListener(e -> {
            new LibrarianDashboard("admin");
            dispose();
        });

        setVisible(true);
    }

    private void addStudent() {
        String id = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String course = (String) courseBox.getSelectedItem();
        String branch = (String) branchBox.getSelectedItem();
        String gmail = gmailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Email validation
        if (!gmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!");
            return;
        }

        // Phone validation
        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits!");
            return;
        }

        // Required fields check
        if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        // Database Insertion
        try {
            String url = "jdbc:mysql://localhost:3306/LibraryManagementSystem";
            String user = "root";
            String dbPassword = "Mysql@1226"; // change if needed

            Connection conn = DriverManager.getConnection(url, user, dbPassword);
            String sql = "INSERT INTO students (student_id, name, course, branch, gmail, phone, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, course);
            ps.setString(4, branch);
            ps.setString(5, gmail);
            ps.setString(6, phone);
            ps.setString(7, password);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student.");
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void clearForm() {
        studentIdField.setText("");
        nameField.setText("");
        gmailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        courseBox.setSelectedIndex(0);
        branchBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        new AddStudentForm();
    }
}
