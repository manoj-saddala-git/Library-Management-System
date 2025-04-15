import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentLoginForm extends JFrame {
    private JTextField studentIdField;
    private JPasswordField passwordField;

    public StudentLoginForm() {
        setTitle("Student Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel idLabel = new JLabel("Student ID:");
        studentIdField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        loginButton.addActionListener(e -> loginStudent());
        cancelButton.addActionListener(e -> dispose());

        panel.add(idLabel);
        panel.add(studentIdField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(cancelButton);

        add(panel);
        setVisible(true);
    }

    private void loginStudent() {
        String studentId = studentIdField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSystem", "root", "Mysql@1226");
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM students WHERE student_id = ? AND password = ?")) {

            stmt.setString(1, studentId);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("name");
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();  // Close login form
                new StudentDashboard(studentId, studentName);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Student ID or Password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new StudentLoginForm();
    }
}
