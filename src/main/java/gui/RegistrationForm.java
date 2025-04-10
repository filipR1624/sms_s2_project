package gui;

import dao.ParentDAO;
import dao.TeacherDAO;
import dao.UserDAO;
import model.Parent;
import model.Teacher;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Registration form for the School Management System.
 * Allows new users to create an account with securely hashed passwords.
 */
public class RegistrationForm extends JFrame implements ActionListener {
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField addressField;
    private JTextField phoneField;
    private JComboBox<String> accountTypeCombo;
    private JSpinner classIdSpinner;
    private JSpinner numChildrenSpinner;
    private JPanel teacherPanel;
    private JPanel parentPanel;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    /**
     * Constructor - initializes the registration form
     */
    public RegistrationForm() {
        // Set up the frame
        setTitle("School Management System - Register");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create title label
        JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 10));

        // Full name field
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameField = new JTextField(20);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);

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

        // Confirm password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);

        // Address field
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(20);
        formPanel.add(addressLabel);
        formPanel.add(addressField);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone:");
        phoneField = new JTextField(20);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);

        // Account type combo
        JLabel accountTypeLabel = new JLabel("Account Type:");
        String[] accountTypes = {"Teacher", "Parent"};
        accountTypeCombo = new JComboBox<>(accountTypes);
        accountTypeCombo.addActionListener(this);
        formPanel.add(accountTypeLabel);
        formPanel.add(accountTypeCombo);

        // Teacher-specific panel
        teacherPanel = new JPanel(new GridLayout(1, 2, 5, 10));
        JLabel classIdLabel = new JLabel("Class ID:");
        SpinnerNumberModel classModel = new SpinnerNumberModel(1, 1, 100, 1);
        classIdSpinner = new JSpinner(classModel);
        teacherPanel.add(classIdLabel);
        teacherPanel.add(classIdSpinner);

        // Parent-specific panel
        parentPanel = new JPanel(new GridLayout(1, 2, 5, 10));
        JLabel numChildrenLabel = new JLabel("Number of Children:");
        SpinnerNumberModel childrenModel = new SpinnerNumberModel(1, 1, 10, 1);
        numChildrenSpinner = new JSpinner(childrenModel);
        parentPanel.add(numChildrenLabel);
        parentPanel.add(numChildrenSpinner);

        // Add role-specific panel (initially teacher)
        formPanel.add(new JPanel()); // Empty panel for spacing
        formPanel.add(teacherPanel);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        formPanel.add(new JPanel()); // Empty panel for spacing
        formPanel.add(statusLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Register button
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        buttonPanel.add(registerButton);

        // Cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);

        // Set parent panel initially invisible
        parentPanel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == accountTypeCombo) {
            // Show/hide appropriate panels based on account type
            String selectedType = (String) accountTypeCombo.getSelectedItem();
            Container parent = null;
            int index = -1;

            // First, find the parent container and the index
            if (teacherPanel.getParent() != null) {
                parent = teacherPanel.getParent();
                for (int i = 0; i < parent.getComponentCount(); i++) {
                    Component comp = parent.getComponent(i);
                    if (comp == teacherPanel || comp == parentPanel) {
                        index = i;
                        break;
                    }
                }
            } else if (parentPanel.getParent() != null) {
                parent = parentPanel.getParent();
                for (int i = 0; i < parent.getComponentCount(); i++) {
                    Component comp = parent.getComponent(i);
                    if (comp == teacherPanel || comp == parentPanel) {
                        index = i;
                        break;
                    }
                }
            }

            // Swap the panels
            if (parent != null && index != -1) {
                // Remove whatever is currently at that index
                parent.remove(index);

                // Add the appropriate panel
                if ("Teacher".equals(selectedType)) {
                    parent.add(teacherPanel, index);
                    teacherPanel.setVisible(true);
                    parentPanel.setVisible(false);
                } else if ("Parent".equals(selectedType)) {
                    parent.add(parentPanel, index);
                    parentPanel.setVisible(true);
                    teacherPanel.setVisible(false);
                }

                // Refresh the container
                parent.revalidate();
                parent.repaint();
            }
        } else if (e.getSource() == registerButton) {
            // Handle registration
            registerUser();
        } else if (e.getSource() == cancelButton) {
            // Go back to login form
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            this.dispose();
        }
    }

    /**
     * Registers a new user with the provided information
     */
    private void registerUser() {
        // Get form data
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String accountType = (String) accountTypeCombo.getSelectedItem();

        // Basic validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all required fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            return;
        }

        // Password strength validation
        if (password.length() < 8) {
            statusLabel.setText("Password must be at least 8 characters long");
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            statusLabel.setText("Invalid email format");
            return;
        }

        try {
            // Convert account type to enum
            User.AccountType userType = "Teacher".equals(accountType) ?
                    User.AccountType.TEACHER : User.AccountType.PARENT;

            // Hash the password using BCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

            // Create user object with hashed password
            User user = new User(fullName, email, hashedPassword, userType, address, phone);

            // Create teacher or parent based on account type
            if (userType == User.AccountType.TEACHER) {
                int classId = (Integer) classIdSpinner.getValue();

                // Create teacher user first
                UserDAO userDAO = new UserDAO();
                int userId = userDAO.addUser(user);

                // Set the generated user ID on the user
                user.setUserId(userId);

                // Create teacher with the user ID
                Teacher teacher = new Teacher(userId, classId);
                TeacherDAO teacherDAO = new TeacherDAO();

                try {
                    int teacherId = teacherDAO.addTeacherWithValidation(teacher);
                    JOptionPane.showMessageDialog(this,
                            "Teacher account created successfully!",
                            "Registration Complete", JOptionPane.INFORMATION_MESSAGE);

                    // Return to login form
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                    this.dispose();
                } catch (IllegalArgumentException ex) {
                    // If teacher creation fails, delete the user
                    userDAO.deleteUser(userId);
                    throw ex;
                }
            } else {
                int numChildren = (Integer) numChildrenSpinner.getValue();

                // Create parent with validation
                Parent parent = new Parent(0, numChildren); // User ID will be set by DAO
                ParentDAO parentDAO = new ParentDAO();
                int parentId = parentDAO.addParentWithValidation(parent, user);

                JOptionPane.showMessageDialog(this,
                        "Parent account created successfully!",
                        "Registration Complete", JOptionPane.INFORMATION_MESSAGE);

                // Return to login form
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                this.dispose();
            }
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}