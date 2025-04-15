
import javax.swing.*;
import java.awt.*;

public class LibrarianDashboard extends JFrame {

    public LibrarianDashboard(String librarianName) {
        setTitle("Librarian Dashboard - Welcome " + librarianName);
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + librarianName + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton addStudentBtn = new JButton("Add New Student");
        JButton addBookBtn = new JButton("Add New Book");
        JButton issueBookBtn = new JButton("Issue Book");
        JButton returnBookBtn = new JButton("Return Book");
        JButton statsBtn = new JButton("View Statistics");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(addStudentBtn);
        buttonPanel.add(addBookBtn);
        buttonPanel.add(issueBookBtn);
        buttonPanel.add(returnBookBtn);
        buttonPanel.add(statsBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // Button Actions
        addStudentBtn.addActionListener(e -> {
            new AddStudentForm();
            dispose();
        });

        addBookBtn.addActionListener(e -> {
            new AddBookForm("admin");
            dispose();
        });

        issueBookBtn.addActionListener(e -> {
            new IssueBookForm("admin");
            dispose();
        });

        returnBookBtn.addActionListener(e -> {
            new ReturnBookForm(this);
            dispose();
        });

        statsBtn.addActionListener(e -> {
            new StatisticsForm(this);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new HomePage();
            dispose();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        // This is only for direct testing, normally you call this from login form after successful login
        new LibrarianDashboard("Admin");
    }
}


