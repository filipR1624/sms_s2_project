package gui;

import dao.*;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Admin Dashboard for the School Management System.
 * Provides access to all management functions.
 */
public class AdminDashboard extends JFrame implements ActionListener {

    private User currentUser;

    // Menu items
    private JButton manageStudentsButton;
    private JButton manageGradesButton;
    private JButton manageAbsencesButton;
    private JButton manageHomeworkButton;
    private JButton manageUsersButton;
    private JButton statisticsButton;
    private JButton logoutButton;

    // Dashboard statistics labels
    private JLabel totalStudentsLabel;
    private JLabel totalTeachersLabel;
    private JLabel totalParentsLabel;
    private JLabel totalClassesLabel;

    /**
     * Constructor - initializes the admin dashboard
     *
     * @param user The current user
     */
    public AdminDashboard(User user) {
        this.currentUser = user;

        // Set up the frame
        setTitle("School Management System - Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        headerPanel.setPreferredSize(new Dimension(900, 80));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(new EmptyBorder(10, 20, 10, 0));

        // System title
        JLabel systemLabel = new JLabel("School Management System - Admin Panel");
        systemLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        systemLabel.setBorder(new EmptyBorder(0, 20, 10, 0));

        // Add labels to a sub-panel
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(welcomeLabel, BorderLayout.NORTH);
        labelPanel.add(systemLabel, BorderLayout.SOUTH);

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        logoutButton.setFocusPainted(false);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);

        // Add components to header
        headerPanel.add(labelPanel, BorderLayout.WEST);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        // Add header to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create menu panel (left side)
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        menuPanel.setBorder(new EmptyBorder(20, 0, 0, 20));
        menuPanel.setPreferredSize(new Dimension(200, 600));

        // Create menu buttons
        manageStudentsButton = createMenuButton("Manage Students");
        manageGradesButton = createMenuButton("Manage Grades");
        manageAbsencesButton = createMenuButton("Manage Absences");
        manageHomeworkButton = createMenuButton("Manage Homework");
        manageUsersButton = createMenuButton("Manage Users");
        statisticsButton = createMenuButton("Statistics");

        // Add buttons to menu panel
        menuPanel.add(manageStudentsButton);
        menuPanel.add(manageGradesButton);
        menuPanel.add(manageAbsencesButton);
        menuPanel.add(manageHomeworkButton);
        menuPanel.add(manageUsersButton);
        menuPanel.add(statisticsButton);

        // Add filler to push buttons to the top
        menuPanel.add(Box.createVerticalGlue());

        // Add menu panel to main panel
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Create content panel (right side)
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY),
                new EmptyBorder(20, 20, 20, 0)));

        // Create dashboard panel with statistics
        JPanel dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);

        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);

        // Load initial statistics
        loadStatistics();
    }

    /**
     * Creates a styled menu button
     *
     * @param text The button text
     * @return The created button
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.addActionListener(this);
        return button;
    }

    /**
     * Creates the dashboard panel with statistics
     *
     * @return The dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Dashboard title
        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(dashboardLabel, BorderLayout.NORTH);

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Create statistic cards
        totalStudentsLabel = createStatisticCard("Total Students", "0");
        totalTeachersLabel = createStatisticCard("Total Teachers", "0");
        totalParentsLabel = createStatisticCard("Total Parents", "0");
        totalClassesLabel = createStatisticCard("Total Classes", "0");

        // Add cards to statistics panel
        statsPanel.add(totalStudentsLabel);
        statsPanel.add(totalTeachersLabel);
        statsPanel.add(totalParentsLabel);
        statsPanel.add(totalClassesLabel);

        // Add statistics panel to dashboard
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a card-style label for statistics
     *
     * @param title The statistic title
     * @param value The initial value
     * @return The created label
     */
    private JLabel createStatisticCard(String title, String value) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" +
                "<div style='font-size: 14pt;'>" + title + "</div>" +
                "<div style='font-size: 24pt; font-weight: bold; margin-top: 10px;'>" + value + "</div></div></html>");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(200, 120));
        label.setOpaque(true);
        label.setBackground(new Color(240, 240, 240));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        return label;
    }

    /**
     * Loads statistics for the dashboard
     */
    private void loadStatistics() {
        try {
            // Count students
            StudentDAO studentDAO = new StudentDAO();
            int studentCount = studentDAO.countStudents();
            totalStudentsLabel.setText("<html><div style='text-align: center;'>" +
                    "<div style='font-size: 14pt;'>Total Students</div>" +
                    "<div style='font-size: 24pt; font-weight: bold; margin-top: 10px;'>" + studentCount + "</div></div></html>");

            // Count teachers
            UserDAO userDAO = new UserDAO();
            int teacherCount = userDAO.countUsersByType(User.AccountType.TEACHER);
            totalTeachersLabel.setText("<html><div style='text-align: center;'>" +
                    "<div style='font-size: 14pt;'>Total Teachers</div>" +
                    "<div style='font-size: 24pt; font-weight: bold; margin-top: 10px;'>" + teacherCount + "</div></div></html>");

            // Count parents
            int parentCount = userDAO.countUsersByType(User.AccountType.PARENT);
            totalParentsLabel.setText("<html><div style='text-align: center;'>" +
                    "<div style='font-size: 14pt;'>Total Parents</div>" +
                    "<div style='font-size: 24pt; font-weight: bold; margin-top: 10px;'>" + parentCount + "</div></div></html>");

            // Count classes
            TeacherDAO teacherDAO = new TeacherDAO();
            int classCount = 0;
            // This is a placeholder - we'd need a ClassDAO to get actual count
            totalClassesLabel.setText("<html><div style='text-align: center;'>" +
                    "<div style='font-size: 14pt;'>Total Classes</div>" +
                    "<div style='font-size: 24pt; font-weight: bold; margin-top: 10px;'>" + classCount + "</div></div></html>");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading statistics: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            // Log out and return to login screen
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            this.dispose();
        } else if (e.getSource() == manageStudentsButton) {
            // Open student management form
            AddStudentForm studentForm = new AddStudentForm();
            studentForm.setVisible(true);
        } else if (e.getSource() == manageGradesButton) {
            // Open grade management form
            AddGradeForm gradeForm = new AddGradeForm();
            gradeForm.setVisible(true);
        } else if (e.getSource() == manageAbsencesButton) {
            // Open absence management form
            AbsenceForm absenceForm = new AbsenceForm();
            absenceForm.setVisible(true);
        } else if (e.getSource() == manageHomeworkButton) {
            // Open homework management form (not implemented yet)
            JOptionPane.showMessageDialog(this,
                    "Homework management feature coming soon!",
                    "Under Construction", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == manageUsersButton) {
            // Open user management form (not implemented yet)
            JOptionPane.showMessageDialog(this,
                    "User management feature coming soon!",
                    "Under Construction", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == statisticsButton) {
            // Refresh statistics
            loadStatistics();
            JOptionPane.showMessageDialog(this,
                    "Statistics updated!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Main method to test the dashboard
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create an admin user for testing
        User adminUser = new User(0, "Admin User", "admin@school.edu", "admin", User.AccountType.ADMIN, "123 School St", "555-1234");

        // Create and display the dashboard
        SwingUtilities.invokeLater(() -> {
            AdminDashboard dashboard = new AdminDashboard(adminUser);
            dashboard.setVisible(true);
        });
    }
}