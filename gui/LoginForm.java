package gui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

/**
 * Login form for the School Management System.
 * Validates user credentials and redirects to the appropriate dashboard.
 */
public class LoginForm extends JFrame implements ActionListener {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    /**
     * Constructor - initializes the login form
     */
    public LoginForm() {
        // Set up the frame
        setTitle("School Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create title label
        JLabel titleLabel = new JLabel("School Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 10));

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        buttonPanel.add(loginButton);

        // Register button
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            // Handle login
            attemptLogin();
        } else if (e.getSource() == registerButton) {
            // Open registration form
            openRegistrationForm();
        }
    }

    /**
     * Attempts to log in with the provided credentials
     */
    private void attemptLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both email and password");
            return;
        }

        // Attempt to authenticate
        UserDAO userDAO = new UserDAO();
        try {
            Optional<User> userOptional = userDAO.getUserByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Simple password check (in a real app, you'd use password hashing)
                if (password.equals(user.getPassword())) {
                    // Successful login
                    openDashboard(user);
                } else {
                    statusLabel.setText("Invalid password");
                }
            } else {
                statusLabel.setText("User not found");
            }
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Opens the registration form
     */
    private void openRegistrationForm() {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setVisible(true);
        this.dispose(); // Close the login form
    }

    /**
     * Opens the appropriate dashboard based on user type
     */
    private void openDashboard(User user) {
        this.dispose(); // Close the login form

        // Open the appropriate dashboard based on user type
        switch (user.getAccountType()) {
            case TEACHER:
                TeacherDashboard teacherDashboard = new TeacherDashboard(user);
                teacherDashboard.setVisible(true);
                break;
            case PARENT:
                ParentDashboard parentDashboard = new ParentDashboard(user);
                parentDashboard.setVisible(true);
                break;
            case ADMIN:
                AdminDashboard adminDashboard = new AdminDashboard(user);
                adminDashboard.setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown account type: " + user.getAccountType(),
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}