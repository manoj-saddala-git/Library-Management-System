import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel);

        JButton librarianLoginButton = new JButton("Librarian Login");
        JButton studentLoginButton = new JButton("Student Login");

        add(librarianLoginButton);
        add(studentLoginButton);

        librarianLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LibrarianLoginForm(); // opens Librarian Login
                dispose(); // close the current window
            }
        });

        studentLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentLoginForm(); // opens Student Login
                dispose(); // close the current window
            }
        });


        setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage();
    }
}
