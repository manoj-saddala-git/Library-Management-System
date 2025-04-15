
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibrarianLoginForm extends JFrame {
    private JTextField librarianIdField;
    private JPasswordField passwordField;

    public LibrarianLoginForm() {
        setTitle("Librarian Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel with padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Form layout
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 40));
        JLabel idLabel = new JLabel("Librarian ID:");
        librarianIdField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        formPanel.add(idLabel);
        formPanel.add(librarianIdField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Action listeners
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String librarianId = librarianIdField.getText();
                String password = new String(passwordField.getPassword());

                if (librarianId.equals("admin") && password.equals("admin123")) {
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    dispose();
                    new LibrarianDashboard(librarianId); // opens actual dashboard and passes ID

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid ID or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dispose());

        // Add to main panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LibrarianLoginForm();
    }
}


