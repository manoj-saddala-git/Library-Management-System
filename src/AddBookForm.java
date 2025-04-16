import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddBookForm extends JFrame {
    private JTextField bookIdField, titleField, authorField, publisherField, quantityField;

    public AddBookForm(String librarianId) {
        setTitle("Add New Book");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        panel.add(new JLabel("Book ID:"));
        bookIdField = new JTextField();
        panel.add(bookIdField);

        panel.add(new JLabel("Title:"));
        titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("Author:"));
        authorField = new JTextField();
        panel.add(authorField);

        panel.add(new JLabel("Publisher:"));
        publisherField = new JTextField();
        panel.add(publisherField);

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        JButton addButton = new JButton("Add Book");
        JButton backButton = new JButton("Back");

        panel.add(addButton);
        panel.add(backButton);

        add(panel);

        // Add button functionality
        addButton.addActionListener(e -> {
            String bookId = bookIdField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            String publisher = publisherField.getText();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantity must be a number.");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LibraryManagementSystem", "root", "Mysql@1226");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?)")) {
                stmt.setString(1, bookId);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setString(4, publisher);
                stmt.setInt(5, quantity);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Book added successfully!");
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add book.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        // Back button functionality
        backButton.addActionListener(e -> {
            new LibrarianDashboard(librarianId);
            dispose();
        });

        setVisible(true);
    }

    private void clearFields() {
        bookIdField.setText("");
        titleField.setText("");
        authorField.setText("");
        publisherField.setText("");
        quantityField.setText("");
    }
}
