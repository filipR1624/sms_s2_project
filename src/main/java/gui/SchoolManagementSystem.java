package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import dao.UserDAO;
import model.User;

public class SchoolManagementSystem extends JFrame {
    // Cards for different screens
    private static final String LOGIN_PANEL = "LOGIN_PANEL";
    private static final String MAIN_MENU_PANEL = "MAIN_MENU_PANEL";

    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Login components
    private JTextField emailField;
    private JPasswordField passwordField;

    // Current logged-in user
    private User currentUser;

    public SchoolManagementSystem() {
        // Set up the frame
        setTitle("School Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center on screen

        // Create the card layout and main content panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create and add the login panel
        contentPanel.add(createLoginPanel(), LOGIN_PANEL);

        // Create and add the main menu panel (initially empty, will be populated after login)
        contentPanel.add(createMainMenuPanel(), MAIN_MENU_PANEL);

        // Set the main content panel
        add(contentPanel);

        // Show the login panel first
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create components
        JLabel titleLabel = new JLabel("Login to School Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Exit");

        // Add components to layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(passwordField, gbc);

        // Button panel for better alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(buttonPanel, gbc);

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return loginPanel;
    }

    private JPanel createMainMenuPanel() {
        JPanel mainMenuPanel = new JPanel(new BorderLayout());

        // This will be populated fully after successful login
        // Here we just create the basic structure

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Add menu to menu bar
        menuBar.add(fileMenu);

        // Add action listeners
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));

        // Add menu bar to panel
        mainMenuPanel.add(menuBar, BorderLayout.NORTH);

        // Create a welcome label (will be updated after login)
        JLabel welcomeLabel = new JLabel("Welcome to the School Management System");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainMenuPanel.add(welcomeLabel, BorderLayout.CENTER);

        return mainMenuPanel;
    }

    private void updateMainMenuForUser(User user) {
        // Get the main menu panel (which is the second card in the CardLayout)
        JPanel mainMenuPanel = (JPanel) contentPanel.getComponent(1);

        // Create a welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFullName() + "!");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Create buttons for different functionality based on user type
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton viewProfileBtn = new JButton("View Profile");
        buttonPanel.add(viewProfileBtn);

        // Add buttons based on user type
        if (user.getAccountType() == User.AccountType.TEACHER) {
            JButton manageClassesBtn = new JButton("Manage Classes");
            JButton manageStudentsBtn = new JButton("Manage Students");
            buttonPanel.add(manageClassesBtn);
            buttonPanel.add(manageStudentsBtn);
        } else if (user.getAccountType() == User.AccountType.ADMIN) {
            JButton manageTeachersBtn = new JButton("Manage Teachers");
            JButton manageStudentsBtn = new JButton("Manage Students");
            JButton manageClassesBtn = new JButton("Manage Classes");
            buttonPanel.add(manageTeachersBtn);
            buttonPanel.add(manageStudentsBtn);
            buttonPanel.add(manageClassesBtn);
        }

        // Create a panel for the center content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(welcomeLabel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Remove the current center component if it exists
        Component existingCenter = ((BorderLayout) mainMenuPanel.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        if (existingCenter != null) {
            mainMenuPanel.remove(existingCenter);
        }

        // Add the new center panel
        mainMenuPanel.add(centerPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        viewProfileBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "User Profile:\nName: " + user.getFullName() +
                        "\nEmail: " + user.getEmail() +
                        "\nRole: " + user.getAccountType(),
                "User Profile", JOptionPane.INFORMATION_MESSAGE));

        // Refresh the UI
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void attemptLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            java.util.Optional<User> userOptional = userDAO.getUserByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // In a real application, you would use password hashing
                if (password.equals(user.getPassword())) {
                    // Store the current user
                    currentUser = user;

                    // Reset login fields
                    emailField.setText("");
                    passwordField.setText("");

                    // Update the main menu for this user
                    updateMainMenuForUser(user);

                    // Switch to the main menu panel
                    cardLayout.show(contentPanel, MAIN_MENU_PANEL);

                    // Update window title to include user info
                    setTitle("School Management System - " + user.getAccountType() + ": " + user.getFullName());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid password",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "User not found",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void logout() {
        // Clear current user
        currentUser = null;

        // Switch back to login panel
        cardLayout.show(contentPanel, LOGIN_PANEL);

        // Reset window title
        setTitle("School Management System");
    }


}