package gui;

import dao.UserDAO;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

/**
 * Login form for the School Management System.
 * Validates user credentials and redirects to the appropriate dashboard.
 * Features modern password hashing for improved security.
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
        setSize(400, 270);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Create title label
        JLabel titleLabel = new JLabel("School Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 10));
        formPanel.setBackground(new Color(240, 240, 240));

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this);
        buttonPanel.add(loginButton);

        // Register button
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setForeground(new Color(41, 128, 185));
        registerButton.setBackground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1, true));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(this);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);

        // Add hover effects
        addButtonHoverEffects();
    }

    /**
     * Adds hover effects to buttons
     */
    private void addButtonHoverEffects() {
        // Login button hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(52, 152, 219));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(41, 128, 185));
            }
        });

        // Register button hover effect
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(240, 240, 240));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(Color.WHITE);
            }
        });
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

        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Attempt to authenticate
        UserDAO userDAO = new UserDAO();
        try {
            Optional<User> userOptional = userDAO.getUserByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Use secure password verification
                boolean passwordMatches = BCrypt.checkpw(password, user.getPassword());

                if (passwordMatches) {
                    // Successful login
                    openDashboard(user);
                } else {
                    statusLabel.setText("Invalid password");
                    // If stored password is plaintext (for backward compatibility), try direct comparison
                    if (password.equals(user.getPassword())) {
                        // Update the password to use hashing for future logins
                        updatePasswordToHashed(user, password);
                        openDashboard(user);
                    }
                }
            } else {
                statusLabel.setText("User not found");
            }
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            // Reset cursor
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Updates a user's password to use hashing
     *
     * @param user The user whose password needs to be updated
     * @param plainPassword The plaintext password to hash
     */
    private void updatePasswordToHashed(User user, String plainPassword) {
        try {
            String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
            user.setPassword(hashedPassword);

            UserDAO userDAO = new UserDAO();
            boolean updated = userDAO.updateUser(user);

            if (!updated) {
                System.err.println("Failed to update password to hashed version for user: " + user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Error updating password to hashed version: " + e.getMessage());
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
        try {
            switch (user.getAccountType()) {
                case TEACHER:
                    try {
                        HorizontalTeacherDashboard teacherDashboard = new HorizontalTeacherDashboard(user);
                        teacherDashboard.setVisible(true);
                    } catch (Exception ex) {
                        // If there's an error loading the teacher dashboard (missing profile, etc.)
                        JOptionPane.showMessageDialog(null,
                                "Error loading teacher dashboard: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        // Reopen login form
                        new LoginForm().setVisible(true);
                    }
                    break;
                case PARENT:
                    ParentDashboard parentDashboard = new ParentDashboard(user);
                    parentDashboard.setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(null,
                            "Unknown account type: " + user.getAccountType(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    new LoginForm().setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error loading dashboard: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            // Reopen login form
            new LoginForm().setVisible(true);
        }
    }
}