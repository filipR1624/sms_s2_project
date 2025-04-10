package gui;

import dao.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
// Import statements
import java.util.*;
import java.util.List;
import java.util.Optional;

/**
 * Horizontal teacher dashboard with navigation buttons at the bottom.
 * Features a clean, modern design with improved visual hierarchy.
 */
public class HorizontalTeacherDashboard extends JFrame implements ActionListener {
    // Theme colors
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Lighter blue
    private static final Color ACCENT_COLOR = new Color(231, 76, 60); // Red accent
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80); // Dark blue-gray
    private static final Color TEXT_SECONDARY_COLOR = new Color(127, 140, 141); // Gray

    private User currentUser;
    private Teacher teacher;
    private int classId;
    private Student selectedStudent;

    // Main components
    private JPanel contentPanel;
    private JPanel mainPanel;
    private JLabel classInfoLabel;
    private JLabel dateTimeLabel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private javax.swing.Timer clockTimer;

    // Navigation buttons
    private JButton dashboardButton;
    private JButton studentsButton;
    private JButton gradesButton;
    private JButton absencesButton;
    private JButton homeworkButton;
    private JButton settingsButton;
    private JButton logoutButton;

    // Dashboard components
    private JPanel dashboardPanel;
    private JLabel studentCountLabel;
    private JLabel classNameLabel;
    private JLabel upcomingEventsLabel;

    // Students tab components
    private JPanel studentsPanel;
    private JTable studentsTable;
    private DefaultTableModel studentsTableModel;
    private JButton addStudentButton;
    private JButton editStudentButton;
    private JButton deleteStudentButton;
    private JButton refreshStudentsButton;
    private JTextField searchStudentField;

    // Grades tab components
    private JPanel gradesPanel;
    private JButton addGradeButton;
    private JButton deleteGradeButton;
    private JButton viewGradesButton;
    private JComboBox<String> studentComboBox;
    private JTextField searchGradeField;
    private DefaultComboBoxModel<String> studentComboModel;
    private Map<String, Integer> studentIdMap; // Maps display names to student IDs

    // Absences tab components
    private JPanel absencesPanel;
    private JButton addAbsenceButton;
    private JButton deleteAbsenceButton;
    private JButton viewAbsencesButton;

    // Homework tab components
    private JPanel homeworkPanel;
    private JButton addHomeworkButton;
    private JButton viewHomeworkButton;

    // Settings components
    private JPanel settingsPanel;

    /**
     * Constructor - initializes the teacher dashboard
     *
     * @param user The current user
     * @throws Exception If teacher profile is not found or cannot be loaded
     */
    public HorizontalTeacherDashboard(User user) throws Exception {
        this.currentUser = user;

        // Get teacher information and class ID
        TeacherDAO teacherDAO = new TeacherDAO();
        Optional<Teacher> teacherOptional = teacherDAO.getTeacherByUserId(user.getUserId());

        if (teacherOptional.isPresent()) {
            this.teacher = teacherOptional.get();
            this.classId = teacher.getClassId();
        } else {
            // If teacher profile not found, throw a more specific exception
            throw new Exception("Teacher profile not found for this user account. Please contact an administrator.");
        }

        // Set up the frame
        setTitle("School Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setBackground(BACKGROUND_COLOR);

        // Initialize UI components
        initializeUI();

        // Load initial data
        loadStudentsData();

        // Start clock timer
        startClockTimer();
    }

    /**
     * Initializes all UI components
     */
    private void initializeUI() {
        // Set the layout for the main frame
        setLayout(new BorderLayout());

        // Create header
        createHeader();

        // Create main content panel with card layout
        createMainContent();

        // Create navigation
        createNavigation();

        // Add panels to frame
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Creates the header with user info and date/time
     */
    private void createHeader() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // User info on the left
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        classInfoLabel = new JLabel("Teacher - Class " + classId);
        classInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        classInfoLabel.setForeground(new Color(255, 255, 255, 200));

        userInfoPanel.add(nameLabel);
        userInfoPanel.add(classInfoLabel);

        // App title in center
        JLabel titleLabel = new JLabel("School Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Date and time on the right
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTimeLabel.setForeground(new Color(255, 255, 255, 200));
        dateTimeLabel.setHorizontalAlignment(JLabel.RIGHT);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(dateTimeLabel, BorderLayout.EAST);

        // Add to main frame
        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Creates the main content panel with card layout for different views
     */
    private void createMainContent() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Create card panel for different views
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.setBackground(BACKGROUND_COLOR);

        // Create all panels
        createDashboardPanel();
        createStudentsPanel();
        createGradesPanel();
        createAbsencesPanel();
        createHomeworkPanel();
        createSettingsPanel();

        // Add panels to card layout
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(studentsPanel, "students");
        cardPanel.add(gradesPanel, "grades");
        cardPanel.add(absencesPanel, "absences");
        cardPanel.add(homeworkPanel, "homework");
        cardPanel.add(settingsPanel, "settings");

        // Add card panel to content panel
        contentPanel.add(cardPanel, BorderLayout.CENTER);

        // Start with dashboard view
        cardLayout.show(cardPanel, "dashboard");
    }

    /**
     * Creates navigation buttons at the bottom of the window
     */
    private void createNavigation() {
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        navigationPanel.setBackground(new Color(240, 240, 240));
        navigationPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(218, 220, 224)),
                new EmptyBorder(10, 10, 10, 10)));

        // Create navigation buttons
        dashboardButton = createNavButton("Dashboard", "dashboard");
        studentsButton = createNavButton("Students", "students");
        gradesButton = createNavButton("Grades", "grades");
        absencesButton = createNavButton("Absences", "absences");
        homeworkButton = createNavButton("Homework", "homework");
        settingsButton = createNavButton("Settings", "settings");
        logoutButton = createLogoutButton();

        // Add buttons to panel
        navigationPanel.add(dashboardButton);
        navigationPanel.add(studentsButton);
        navigationPanel.add(gradesButton);
        navigationPanel.add(absencesButton);
        navigationPanel.add(homeworkButton);
        navigationPanel.add(settingsButton);
        navigationPanel.add(logoutButton);

        // Add to main frame
        add(navigationPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a styled navigation button
     *
     * @param text The button text
     * @param command The action command
     * @return The created button
     */
    private JButton createNavButton(String text, String command) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setActionCommand(command);
        button.addActionListener(this);

        // Active indicator panel at the bottom of the button
        if ("dashboard".equals(command)) {
            button.setBackground(new Color(245, 245, 245));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 3, 0, PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(8, 20, 5, 20)));
        }

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!command.equals(getActiveTab())) {
                    button.setBackground(new Color(245, 245, 245));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!command.equals(getActiveTab())) {
                    button.setBackground(Color.WHITE);
                }
            }
        });

        return button;
    }

    /**
     * Creates the logout button
     *
     * @return The logout button
     */
    private JButton createLogoutButton() {
        JButton button = new JButton("Logout");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(192, 57, 43)); // Darker red
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    /**
     * Gets the currently active tab
     *
     * @return The active tab name
     */
    private String getActiveTab() {
        // Default to dashboard
        String activeTab = "dashboard";

        // Check which button has the active indicator
        if (dashboardButton.getBackground().equals(new Color(245, 245, 245))) {
            activeTab = "dashboard";
        } else if (studentsButton.getBackground().equals(new Color(245, 245, 245))) {
            activeTab = "students";
        } else if (gradesButton.getBackground().equals(new Color(245, 245, 245))) {
            activeTab = "grades";
        } else if (absencesButton.getBackground().equals(new Color(245, 245, 245))) {
            activeTab = "absences";
        } else if (homeworkButton.getBackground().equals(new Color(245, 245, 245))) {
            activeTab = "homework";
        } else if (settingsButton.getBackground().equals(new Color(245, 245, 245))) {
            activeTab = "settings";
        }

        return activeTab;
    }

    /**
     * Updates the active tab indicator
     *
     * @param activeTab The active tab name
     */
    private void updateActiveTab(String activeTab) {
        // Reset all buttons
        dashboardButton.setBackground(Color.WHITE);
        dashboardButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        studentsButton.setBackground(Color.WHITE);
        studentsButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        gradesButton.setBackground(Color.WHITE);
        gradesButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        absencesButton.setBackground(Color.WHITE);
        absencesButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        homeworkButton.setBackground(Color.WHITE);
        homeworkButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        settingsButton.setBackground(Color.WHITE);
        settingsButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));

        // Set active button
        JButton activeButton = dashboardButton; // Default

        switch (activeTab) {
            case "dashboard":
                activeButton = dashboardButton;
                break;
            case "students":
                activeButton = studentsButton;
                break;
            case "grades":
                activeButton = gradesButton;
                break;
            case "absences":
                activeButton = absencesButton;
                break;
            case "homework":
                activeButton = homeworkButton;
                break;
            case "settings":
                activeButton = settingsButton;
                break;
        }

        // Apply active style
        activeButton.setBackground(new Color(245, 245, 245));
        activeButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(8, 20, 5, 20)));
    }

    /**
     * Creates the dashboard panel with summary information
     */
    private void createDashboardPanel() {
        dashboardPanel = new JPanel();
        dashboardPanel.setBackground(BACKGROUND_COLOR);
        dashboardPanel.setLayout(new BorderLayout(20, 20));
        dashboardPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_COLOR);

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        // Dashboard content panel
        JPanel dashboardContent = new JPanel(new GridLayout(1, 3, 20, 0));
        dashboardContent.setOpaque(false);



        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout(0, 15));
        activityPanel.setBackground(CARD_COLOR);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel activityTitle = new JLabel("Quick Actions");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        activityTitle.setForeground(TEXT_COLOR);

        JPanel actionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        actionsPanel.setOpaque(false);

        actionsPanel.add(createActionButton("Add Student", e -> {
            AddStudentForm form = new AddStudentForm();
            form.setVisible(true);
        }));

        actionsPanel.add(createActionButton("Record Absence", e -> {
            AbsenceForm form = new AbsenceForm(classId);
            form.setVisible(true);
        }));

        actionsPanel.add(createActionButton("Add Grade", e -> {
            AddGradeForm form = new AddGradeForm(classId);
            form.setVisible(true);
        }));

        actionsPanel.add(createActionButton("Assign Homework", e -> {
            HomeworkForm form = new HomeworkForm(classId);
            form.setVisible(true);
        }));

        activityPanel.add(activityTitle, BorderLayout.NORTH);
        activityPanel.add(actionsPanel, BorderLayout.CENTER);

        // Calendar panel
        JPanel calendarPanel = new JPanel(new BorderLayout(0, 15));
        calendarPanel.setBackground(CARD_COLOR);
        calendarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel calendarTitle = new JLabel("Today's Schedule");
        calendarTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        calendarTitle.setForeground(TEXT_COLOR);

        JLabel noEventsLabel = new JLabel("No events scheduled for today");
        noEventsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        noEventsLabel.setForeground(TEXT_SECONDARY_COLOR);
        noEventsLabel.setHorizontalAlignment(JLabel.CENTER);

        calendarPanel.add(calendarTitle, BorderLayout.NORTH);
        calendarPanel.add(noEventsLabel, BorderLayout.CENTER);

        // Create bottom panel with activity and calendar
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(activityPanel);
        bottomPanel.add(calendarPanel);

        // Add to dashboard panel
        dashboardPanel.add(welcomePanel, BorderLayout.NORTH);
        dashboardPanel.add(dashboardContent, BorderLayout.CENTER);
        dashboardPanel.add(bottomPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a statistic card for the dashboard
     *
     * @param title The card title
     * @param value The value to display
     * @param iconName Icon name (not used yet, placeholder for future icon support)
     * @return The card panel
     */
    private JPanel createStatCard(String title, String value, String iconName) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setOpaque(false);

        // Icon placeholder (empty now, removed as requested)
        JPanel iconPanel = new JPanel();
        iconPanel.setPreferredSize(new Dimension(0, 0));
        iconPanel.setOpaque(false);

        // Text panel
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY_COLOR);

        textPanel.add(valueLabel);
        textPanel.add(titleLabel);

        contentPanel.add(iconPanel, BorderLayout.WEST);
        contentPanel.add(textPanel, BorderLayout.CENTER);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Creates an action button for the dashboard
     *
     * @param text Button text
     * @param listener Action listener
     * @return The button
     */
    private JButton createActionButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(SECONDARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Remove any existing action listeners
        for (ActionListener al : button.getActionListeners()) {
            button.removeActionListener(al);
        }

        // Add the new listener
        button.addActionListener(listener);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
        });

        return button;
    }

    /**
     * Creates the students panel with table and controls
     */
    private void createStudentsPanel() {
        studentsPanel = new JPanel(new BorderLayout(0, 20));
        studentsPanel.setBackground(BACKGROUND_COLOR);
        studentsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Panel title
        JLabel titleLabel = new JLabel("Students");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        // Search and button panel
        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setOpaque(false);


        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        addStudentButton = createControlButton("Add Student", "add");
        editStudentButton = createControlButton("Edit", "edit");
        deleteStudentButton = createControlButton("Delete", "delete");
        refreshStudentsButton = createControlButton("Refresh", "refresh");

        buttonPanel.add(addStudentButton);
        buttonPanel.add(editStudentButton);
        buttonPanel.add(deleteStudentButton);
        buttonPanel.add(refreshStudentsButton);


        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        // Top panel with title and controls
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Create table model with columns
        String[] columns = {"ID", "First Name", "Last Name", "Class ID", "Address", "Parent ID"};
        studentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create table
        studentsTable = new JTable(studentsTableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.setRowHeight(40);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentsTable.setGridColor(new Color(240, 240, 240));
        studentsTable.setShowVerticalLines(false);

        // Add selection listener to track selected student
        studentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    // Get student data from the selected row
                    int studentId = (int) studentsTable.getValueAt(selectedRow, 0);
                    String firstName = (String) studentsTable.getValueAt(selectedRow, 1);
                    String lastName = (String) studentsTable.getValueAt(selectedRow, 2);
                    int classId = (int) studentsTable.getValueAt(selectedRow, 3);
                    String address = (String) studentsTable.getValueAt(selectedRow, 4);
                    int parentId = (int) studentsTable.getValueAt(selectedRow, 5);

                    // Create a Student object from the selected row data
                    selectedStudent = new Student(studentId, classId, firstName, lastName, address, parentId);

                    // Update grades view button to use the selected student
                    updateGradesView();
                }
            }
        });

        // Style table header
        JTableHeader header = studentsTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < studentsTable.getColumnCount(); i++) {
            studentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add all components to students panel
        studentsPanel.add(topPanel, BorderLayout.NORTH);
        studentsPanel.add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Creates a control button for tables
     *
     * @param text Button text
     * @param type Button type (add, edit, delete, refresh)
     * @return The button
     */
    private JButton createControlButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        // Style based on type
        if ("add".equals(type)) {
            button.setBackground(SECONDARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
        } else if ("delete".equals(type)) {
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
        } else if ("edit".equals(type)) {
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
        } else if ("refresh".equals(type)) {
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorderPainted(false);
        }
        return button;
    }

    /**
     * Creates the grades panel
     */
    private void createGradesPanel() {
        gradesPanel = new JPanel(new BorderLayout());
        gradesPanel.setBackground(BACKGROUND_COLOR);
        gradesPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Create main content panel with vertical BoxLayout to ensure proper component stacking
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Panel title
        JLabel titleLabel = new JLabel("Grades Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add some vertical space after title
        mainContent.add(titleLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create controls panel for student selection and search
        JPanel controlsPanel = new JPanel(new BorderLayout(15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Student dropdown
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dropdownPanel.setOpaque(false);

        JLabel studentLabel = new JLabel("Student:");
        studentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentLabel.setForeground(TEXT_COLOR);

        studentIdMap = new HashMap<>();
        studentComboModel = new DefaultComboBoxModel<>();
        studentComboBox = new JComboBox<>(studentComboModel);
        studentComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentComboBox.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        studentComboBox.setBackground(Color.WHITE);
        studentComboBox.setPreferredSize(new Dimension(250, 35));

        // Load students into combo box
        loadStudentsIntoComboBox();

        // Add change listener to combo box
        studentComboBox.addActionListener(e -> {
            if (studentComboBox.getSelectedIndex() > 0) { // Skip the "Select a student" prompt
                String selectedItem = (String) studentComboBox.getSelectedItem();
                if (selectedItem != null && studentIdMap.containsKey(selectedItem)) {
                    int studentId = studentIdMap.get(selectedItem);
                    StudentDAO studentDAO = new StudentDAO();
                    Optional<Student> studentOpt = studentDAO.getStudentById(studentId);
                    if (studentOpt.isPresent()) {
                        selectedStudent = studentOpt.get();

                        // Get the table component and update it
                        Component[] components = gradesPanel.getComponents();
                        for (Component comp : components) {
                            if (comp instanceof JPanel) {
                                findAndUpdateGradesTable((JPanel) comp, selectedStudent);
                            }
                        }
                    }
                }
            } else {
                // Clear the grades table when "Select a student" is chosen
                Component[] components = gradesPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        findAndClearGradesTable((JPanel) comp);
                    }
                }
            }
        });

        dropdownPanel.add(studentLabel);
        dropdownPanel.add(studentComboBox);

        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);

        searchGradeField = new JTextField(15);
        searchGradeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchGradeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchGradeField.setPreferredSize(new Dimension(150, 35));

        // Add search functionality
        searchGradeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterStudents();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterStudents();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterStudents();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchGradeField);

        controlsPanel.add(dropdownPanel, BorderLayout.WEST);
        controlsPanel.add(searchPanel, BorderLayout.EAST);

        mainContent.add(controlsPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create the grades table panel
        JPanel gradesViewPanel = new JPanel(new BorderLayout());
        gradesViewPanel.setBackground(CARD_COLOR);
        gradesViewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        gradesViewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create table for grades
        String[] columns = {"Subject", "Grade", "Date", "Comments"};
        DefaultTableModel gradesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable gradesTable = new JTable(gradesTableModel);
        gradesTable.setName("gradesTable"); // Give it a name for easier lookup
        gradesTable.setRowHeight(40);
        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gradesTable.setGridColor(new Color(240, 240, 240));
        gradesTable.setShowVerticalLines(false);

        // Style table header
        JTableHeader header = gradesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(TEXT_COLOR);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(218, 220, 224)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < gradesTable.getColumnCount(); i++) {
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }



        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);


        gradesViewPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(gradesViewPanel);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        addGradeButton = createActionButton("Add Grade", e -> {
            if (studentComboBox.getSelectedIndex() > 0) {
                // Get the student data to pass to the form
                String selectedItem = (String) studentComboBox.getSelectedItem();
                int studentId = studentIdMap.get(selectedItem);

                // Create and configure the grade form with the selected student
                AddGradeForm form = new AddGradeForm(classId);

                // Set the selected student ID in the form (assuming the form has a method to do this)
                // If AddGradeForm doesn't have this functionality, you'd need to modify it
                // For now, just display information to the user
                JOptionPane.showMessageDialog(this,
                        "Adding grade for Student ID: " + studentId + "\n" +
                                "Note: In a complete implementation, this would pre-select the student in the AddGradeForm.",
                        "Add Grade", JOptionPane.INFORMATION_MESSAGE);

                form.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a student first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        viewGradesButton = createActionButton("Refresh Grades", e -> {
            if (studentComboBox.getSelectedIndex() > 0) {
                // Show loading indicator
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    // Get the selected student
                    String selectedItem = (String) studentComboBox.getSelectedItem();
                    int studentId = studentIdMap.get(selectedItem);
                    StudentDAO studentDAO = new StudentDAO();
                    Optional<Student> studentOpt = studentDAO.getStudentById(studentId);

                    if (studentOpt.isPresent()) {
                        // Find and update the grades table
                        Component[] components = gradesPanel.getComponents();
                        for (Component comp : components) {
                            if (comp instanceof Container) {
                                findAndUpdateGradesTable((Container) comp, studentOpt.get());
                            }
                        }
                    }
                } finally {
                    // Restore cursor
                    setCursor(Cursor.getDefaultCursor());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a student first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        deleteGradeButton = createActionButton("Delete Grade", e -> {
            if (studentComboBox.getSelectedIndex() > 0) {
                // Get the table from the panel
                JTable currentGradesTable  = findGradesTable(gradesPanel);
                if (currentGradesTable  != null) {
                    int selectedRow = currentGradesTable .getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(this,
                                "Please select a grade to delete",
                                "No Selection", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Get grade data from selected row
                    String subject = (String) currentGradesTable .getValueAt(selectedRow, 0);
                    String mark = (String) currentGradesTable .getValueAt(selectedRow, 1);

                    // Confirm deletion
                    int result = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to delete the " + subject + " grade (" + mark + ")?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            // Get the grade ID - this requires updating your table model to include grade ID as a hidden column
                            // For now, we'll need to find the grade by subject and student ID
                            String selectedItem = (String) studentComboBox.getSelectedItem();
                            int studentId = studentIdMap.get(selectedItem);

                            GradeDAO gradeDAO = new GradeDAO();
                            List<Grade> grades = gradeDAO.getGradesByStudent(studentId);

                            // Find the grade that matches the subject and grade
                            for (Grade grade : grades) {
                                if (grade.getSubject().equals(subject) && String.valueOf(grade.getMark()).equals(mark)) {
                                    // Delete the grade
                                    boolean deleted = gradeDAO.deleteGrade(grade.getGradeId());

                                    if (deleted) {
                                        JOptionPane.showMessageDialog(this,
                                                "Grade deleted successfully.",
                                                "Success", JOptionPane.INFORMATION_MESSAGE);

                                        // Refresh the grades table
                                        findAndUpdateGradesTable(gradesPanel, selectedStudent);
                                    } else {
                                        JOptionPane.showMessageDialog(this,
                                                "Failed to delete grade.",
                                                "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this,
                                    "Error deleting grade: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a student first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(addGradeButton);
        buttonPanel.add(viewGradesButton);
        buttonPanel.add(deleteGradeButton);
        mainContent.add(buttonPanel);

        // Add all to the main grades panel
        gradesPanel.add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Helper method to find and update the grades table in a component hierarchy
     *
     * @param container The container to search
     * @param student The student to load grades for
     */
    private void findAndUpdateGradesTable(Container container, Student student) {
        Component[] components = container.getComponents();

        for (Component component : components) {
            if (component instanceof JTable && "gradesTable".equals(component.getName())) {
                JTable table = (JTable) component;
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                loadGradesForStudent(student, model);
                return;
            } else if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTable && "gradesTable".equals(view.getName())) {
                    JTable table = (JTable) view;
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    loadGradesForStudent(student, model);
                    return;
                }
            } else if (component instanceof Container) {
                findAndUpdateGradesTable((Container) component, student);
            }
        }
    }

    /**
     * Helper method to find and clear the grades table in a component hierarchy
     *
     * @param container The container to search
     */
    private void findAndClearGradesTable(Container container) {
        Component[] components = container.getComponents();

        for (Component component : components) {
            if (component instanceof JTable && "gradesTable".equals(component.getName())) {
                JTable table = (JTable) component;
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                return;
            } else if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTable && "gradesTable".equals(view.getName())) {
                    JTable table = (JTable) view;
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.setRowCount(0);
                    return;
                }
            } else if (component instanceof Container) {
                findAndClearGradesTable((Container) component);
            }
        }
    }

    /**
     * Loads all students from the class into the combo box
     */
    private void loadStudentsIntoComboBox() {
        studentComboModel.removeAllElements();
        studentIdMap.clear();

        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students = studentDAO.getStudentsByClass(classId);

            // Add default prompt
            studentComboModel.addElement("Select a student...");

            // Sort students by last name, then first name
            Collections.sort(students, (s1, s2) -> {
                int lastNameComparison = s1.getLastName().compareTo(s2.getLastName());
                if (lastNameComparison != 0) {
                    return lastNameComparison;
                }
                return s1.getFirstName().compareTo(s2.getFirstName());
            });

            // Add each student to the combo box
            for (Student student : students) {
                String displayName = student.getLastName() + ", " + student.getFirstName() + " (ID: " + student.getStudentId() + ")";
                studentComboModel.addElement(displayName);
                studentIdMap.put(displayName, Integer.valueOf(student.getStudentId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filters students in the combo box based on search text
     */
    private void filterStudents() {
        String searchText = searchGradeField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            // Reload all students
            loadStudentsIntoComboBox();
            return;
        }

        // Store currently selected item
        Object selectedItem = studentComboBox.getSelectedItem();

        // Clear combo box
        studentComboModel.removeAllElements();

        // Add default prompt
        studentComboModel.addElement("Select a student...");

        // Add matching students
        for (String displayName : studentIdMap.keySet()) {
            if (displayName.toLowerCase().contains(searchText)) {
                studentComboModel.addElement(displayName);
            }
        }

        // Restore selection if it exists in filtered results
        if (selectedItem != null) {
            for (int i = 0; i < studentComboModel.getSize(); i++) {
                if (studentComboModel.getElementAt(i).equals(selectedItem)) {
                    studentComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Loads grades for a specific student into the table model
     *
     * @param student The student to load grades for
     * @param tableModel The table model to load grades into
     */
    private void loadGradesForStudent(Student student, DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            // Get grades for the student from the database
            GradeDAO gradeDAO = new GradeDAO();
            List<Grade> grades = gradeDAO.getGradesByStudent(student.getStudentId());

            if (grades.isEmpty()) {
                // No grades found
                JOptionPane.showMessageDialog(this,
                        "No grades found for " + student.getFirstName() + " " + student.getLastName(),
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Format the date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                // Add each grade to the table
                for (Grade grade : grades) {
                    Object[] rowData = {
                            grade.getSubject(),
                            String.valueOf(grade.getMark()),
                            dateFormat.format(grade.getGradeDate()),
                            grade.getComment()
                    };
                    tableModel.addRow(rowData);
                }

                // Show success message
                JOptionPane.showMessageDialog(this,
                        "Loaded " + grades.size() + " grades for " + student.getFirstName() + " " + student.getLastName(),
                        "Grades Loaded", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading grades: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the grades view to reflect the currently selected student
     */
    private void updateGradesView() {
        if (selectedStudent != null) {
            // Update button functionality for all buttons that need student selection
            // Remove existing listeners
            for (ActionListener listener : viewGradesButton.getActionListeners()) {
                viewGradesButton.removeActionListener(listener);
            }

            for (ActionListener listener : addGradeButton.getActionListeners()) {
                addGradeButton.removeActionListener(listener);
            }

            // Add new listeners with selected student
            viewGradesButton.addActionListener(e -> displayStudentGrades(selectedStudent));

            addGradeButton.addActionListener(e -> {
                AddGradeForm form = new AddGradeForm(classId);
                form.setVisible(true);
            });
        }
    }

    /**
     * Displays the grades for a specific student
     *
     * @param student The student to display grades for
     */
    private void displayStudentGrades(Student student) {
        // Create a new dialog to display grades
        JDialog gradesDialog = new JDialog(this, "Grades for " + student.getFirstName() + " " + student.getLastName(), true);
        gradesDialog.setSize(600, 400);
        gradesDialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new BorderLayout(0, 20));
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogPanel.setBackground(BACKGROUND_COLOR);

        // Create table model with columns for grades
        String[] columns = {"Subject", "Grade", "Date", "Comments"};
        DefaultTableModel gradesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create table for grades
        JTable gradesTable = new JTable(gradesTableModel);
        gradesTable.setRowHeight(40);
        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gradesTable.setGridColor(new Color(240, 240, 240));
        gradesTable.setShowVerticalLines(false);

        // Style table header
        JTableHeader header = gradesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(TEXT_COLOR);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(218, 220, 224)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < gradesTable.getColumnCount(); i++) {
            gradesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);

        // Load grades data (sample data for now)
        try {
            // This would normally fetch data from the database
            // For now, just add sample data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Object[] row1 = {"Mathematics", "A", dateFormat.format(new Date()), "Excellent work"};
            Object[] row2 = {"English", "B", dateFormat.format(new Date()), "Good effort"};
            Object[] row3 = {"Science", "A-", dateFormat.format(new Date()), "Great lab work"};

            gradesTableModel.addRow(row1);
            gradesTableModel.addRow(row2);
            gradesTableModel.addRow(row3);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gradesDialog,
                    "Error loading grades: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(SECONDARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> gradesDialog.dispose());

        buttonPanel.add(closeButton);

        // Add components to dialog
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        gradesDialog.add(dialogPanel);
        gradesDialog.setVisible(true);
    }

    /**
     * Creates the absences panel with similar functionality to the grades panel
     */
    private void createAbsencesPanel() {
        absencesPanel = new JPanel(new BorderLayout());
        absencesPanel.setBackground(BACKGROUND_COLOR);
        absencesPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Create main content panel with vertical BoxLayout to ensure proper component stacking
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Panel title
        JLabel titleLabel = new JLabel("Absences Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add some vertical space after title
        mainContent.add(titleLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create controls panel for student selection and search
        JPanel controlsPanel = new JPanel(new BorderLayout(15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Student dropdown
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dropdownPanel.setOpaque(false);

        JLabel studentLabel = new JLabel("Student:");
        studentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentLabel.setForeground(TEXT_COLOR);

        // Reuse the same combo model and studentIdMap from grades panel
        JComboBox<String> absenceStudentComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        absenceStudentComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        absenceStudentComboBox.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        absenceStudentComboBox.setBackground(Color.WHITE);
        absenceStudentComboBox.setPreferredSize(new Dimension(250, 35));

        // Load students into combo box
        DefaultComboBoxModel<String> absenceStudentComboModel = (DefaultComboBoxModel<String>)absenceStudentComboBox.getModel();
        absenceStudentComboModel.removeAllElements();
        absenceStudentComboModel.addElement("Select a student...");

        // We'll use the same student map as in the grades panel
        Map<String, Integer> absenceStudentIdMap = new HashMap<>();

        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students = studentDAO.getStudentsByClass(classId);

            // Sort students by last name, then first name
            Collections.sort(students, (s1, s2) -> {
                int lastNameComparison = s1.getLastName().compareTo(s2.getLastName());
                if (lastNameComparison != 0) {
                    return lastNameComparison;
                }
                return s1.getFirstName().compareTo(s2.getFirstName());
            });

            // Add each student to the combo box
            for (Student student : students) {
                String displayName = student.getLastName() + ", " + student.getFirstName() + " (ID: " + student.getStudentId() + ")";
                absenceStudentComboModel.addElement(displayName);
                absenceStudentIdMap.put(displayName, Integer.valueOf(student.getStudentId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        dropdownPanel.add(studentLabel);
        dropdownPanel.add(absenceStudentComboBox);

        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);

        JTextField searchAbsenceField = new JTextField(15);
        searchAbsenceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchAbsenceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchAbsenceField.setPreferredSize(new Dimension(150, 35));

        // Add search functionality
        searchAbsenceField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterAbsenceStudents(searchAbsenceField, absenceStudentComboBox, absenceStudentComboModel, absenceStudentIdMap);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterAbsenceStudents(searchAbsenceField, absenceStudentComboBox, absenceStudentComboModel, absenceStudentIdMap);
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterAbsenceStudents(searchAbsenceField, absenceStudentComboBox, absenceStudentComboModel, absenceStudentIdMap);
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchAbsenceField);

        controlsPanel.add(dropdownPanel, BorderLayout.WEST);
        controlsPanel.add(searchPanel, BorderLayout.EAST);

        mainContent.add(controlsPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create the absences table panel
        JPanel absencesViewPanel = new JPanel(new BorderLayout());
        absencesViewPanel.setBackground(CARD_COLOR);
        absencesViewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        absencesViewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create table for absences
        String[] columns = {"ID","Absence Date", "Description", "Status"};
        DefaultTableModel absencesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable absencesTable = new JTable(absencesTableModel);
        absencesTable.setName("absencesTable"); // Give it a name for easier lookup
        absencesTable.setRowHeight(40);
        absencesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        absencesTable.setGridColor(new Color(240, 240, 240));
        absencesTable.setShowVerticalLines(false);

        // Style table header
        JTableHeader header = absencesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(TEXT_COLOR);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(218, 220, 224)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < absencesTable.getColumnCount(); i++) {
            absencesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }



        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(absencesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);

        absencesViewPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(absencesViewPanel);

        // Add student selection listener to update absences
        absenceStudentComboBox.addActionListener(e -> {
            if (absenceStudentComboBox.getSelectedIndex() > 0) { // Skip the "Select a student" prompt
                String selectedItem = (String) absenceStudentComboBox.getSelectedItem();
                if (selectedItem != null && absenceStudentIdMap.containsKey(selectedItem)) {
                    int studentId = absenceStudentIdMap.get(selectedItem);
                    loadAbsencesForStudent(studentId, absencesTableModel);
                }
            } else {
                // Clear the absences table when "Select a student" is chosen
                absencesTableModel.setRowCount(0);
            }
        });

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        addAbsenceButton = createActionButton("Record Absence", e -> {
            if (absenceStudentComboBox.getSelectedIndex() > 0) {
                // Get the student data to pass to the form
                String selectedItem = (String) absenceStudentComboBox.getSelectedItem();
                int studentId = absenceStudentIdMap.get(selectedItem);

                // Show AbsenceForm (you could modify this to create a custom form)
                AbsenceForm form = new AbsenceForm(classId);
                form.setTitle("Record Absence for Student ID: " + studentId);
                form.setVisible(true);

                // You could display a message about the current limitation
                JOptionPane.showMessageDialog(this,
                        "Recording absence for Student ID: " + studentId + "\n" +
                                "Note: The AbsenceForm needs modification to pre-select students.",
                        "Record Absence", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a student first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        viewAbsencesButton = createActionButton("Refresh Absences", e -> {
            if (absenceStudentComboBox.getSelectedIndex() > 0) {
                // Show loading indicator
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    // Get the selected student ID
                    String selectedItem = (String) absenceStudentComboBox.getSelectedItem();
                    int studentId = absenceStudentIdMap.get(selectedItem);

                    // Load absences for the student
                    loadAbsencesForStudent(studentId, absencesTableModel);
                } finally {
                    // Restore cursor
                    setCursor(Cursor.getDefaultCursor());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a student first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        deleteAbsenceButton = createActionButton("Delete Absence", e -> {
            if (absenceStudentComboBox.getSelectedIndex() > 0) {
                int selectedRow = absencesTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this,
                            "Please select an absence to delete",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String dateStr = (String) absencesTable.getValueAt(selectedRow, 0);
                int studentId = absenceStudentIdMap.get(absenceStudentComboBox.getSelectedItem());

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete absence on " + dateStr + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteAbsence(studentId, dateStr);
                    loadAbsencesForStudent(studentId, absencesTableModel);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a student first",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Add the button to the panel
        deleteAbsenceButton.setBackground(ACCENT_COLOR);
        deleteAbsenceButton.setForeground(Color.WHITE);
        buttonPanel.add(deleteAbsenceButton);
        buttonPanel.add(addAbsenceButton);
        buttonPanel.add(viewAbsencesButton);

        mainContent.add(buttonPanel);

        // Add all to the main absences panel
        absencesPanel.add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Filters students in the absence combo box based on search text
     */
    private void filterAbsenceStudents(JTextField searchField, JComboBox<String> comboBox,
                                       DefaultComboBoxModel<String> comboModel, Map<String, Integer> studentIdMap) {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            // Reload all students
            comboModel.removeAllElements();
            comboModel.addElement("Select a student...");
            for (String displayName : studentIdMap.keySet()) {
                comboModel.addElement(displayName);
            }
            return;
        }

        // Store currently selected item
        Object selectedItem = comboBox.getSelectedItem();

        // Clear combo box
        comboModel.removeAllElements();

        // Add default prompt
        comboModel.addElement("Select a student...");

        // Add matching students
        for (String displayName : studentIdMap.keySet()) {
            if (displayName.toLowerCase().contains(searchText)) {
                comboModel.addElement(displayName);
            }
        }

        // Restore selection if it exists in filtered results
        if (selectedItem != null) {
            for (int i = 0; i < comboModel.getSize(); i++) {
                if (comboModel.getElementAt(i).equals(selectedItem)) {
                    comboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Loads absences for a specific student into the table model
     *
     * @param studentId The student ID to load absences for
     * @param tableModel The table model to load absences into
     */
    private void loadAbsencesForStudent(int studentId, DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            // Get absences for the student from the database
            AbsenceDAO absenceDAO = new AbsenceDAO();
            List<Absence> absences = absenceDAO.getAbsencesByStudent(studentId);

            if (absences.isEmpty()) {
                // No absences found
                JOptionPane.showMessageDialog(this,
                        "No absences found for student ID: " + studentId,
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Format the date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                // Add each absence to the table
                for (Absence absence : absences) {
                    Object[] rowData = {
                            (Object) absence.getAbsenceId(),  // First column: ID
                            dateFormat.format(absence.getAbsenceDate()),  // Second column: Date
                            absence.getDescription(),  // Third column: Description
                            absence.isStatus() ? "Excused" : "Unexcused"  // Fourth column: Status
                    };
                    tableModel.addRow(rowData);
                }

                // Show success message
                JOptionPane.showMessageDialog(this,
                        "Loaded " + absences.size() + " absences for student ID: " + studentId,
                        "Absences Loaded", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading absences: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates the homework panel with table and controls
     */
    private void createHomeworkPanel() {
        homeworkPanel = new JPanel(new BorderLayout());
        homeworkPanel.setBackground(BACKGROUND_COLOR);
        homeworkPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Create main content panel with vertical BoxLayout
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Panel title
        JLabel titleLabel = new JLabel("Homework Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add vertical space after title
        mainContent.add(titleLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create controls panel
        JPanel controlsPanel = new JPanel(new BorderLayout(15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_COLOR);

        String[] statuses = {"All Assignments", "Active", "Completed", "Overdue"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusComboBox.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        statusComboBox.setBackground(Color.WHITE);
        statusComboBox.setPreferredSize(new Dimension(200, 35));

        filterPanel.add(statusLabel);
        filterPanel.add(statusComboBox);

        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);

        JTextField searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(150, 35));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        controlsPanel.add(filterPanel, BorderLayout.WEST);
        controlsPanel.add(searchPanel, BorderLayout.EAST);

        mainContent.add(controlsPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create homework table panel
        JPanel homeworkTablePanel = new JPanel(new BorderLayout());
        homeworkTablePanel.setBackground(CARD_COLOR);
        homeworkTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        homeworkTablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create table for homework
        String[] columns = {"ID", "Assignment Date", "Due Date", "Description", "Status"};
        DefaultTableModel homeworkTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        JTable homeworkTable = new JTable(homeworkTableModel);
        homeworkTable.setName("homeworkTable"); // For easier lookup
        homeworkTable.setRowHeight(40);
        homeworkTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        homeworkTable.setGridColor(new Color(240, 240, 240));
        homeworkTable.setShowVerticalLines(false);
        homeworkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer for status column (column 4)
        homeworkTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String status = (String) value;
                if ("Completed".equals(status)) {
                    c.setForeground(new Color(46, 204, 113)); // Green
                } else if ("Overdue".equals(status)) {
                    c.setForeground(new Color(231, 76, 60)); // Red
                } else {
                    c.setForeground(new Color(52, 152, 219)); // Blue
                }

                return c;
            }
        });

        // Set column widths
        homeworkTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        homeworkTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Assignment Date
        homeworkTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Due Date
        homeworkTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Description
        homeworkTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status

        // Style table header
        JTableHeader header = homeworkTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(TEXT_COLOR);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(218, 220, 224)));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center cell content for most columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < homeworkTable.getColumnCount(); i++) {
            if (i != 3) { // Skip description column
                homeworkTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(homeworkTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);

        homeworkTablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(homeworkTablePanel);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        addHomeworkButton = createActionButton("Add Homework", e -> {
            // Open a new form to add homework
            HomeworkForm form = new HomeworkForm(classId);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    // Refresh table when form is closed
                    loadHomeworkData(homeworkTableModel);
                }
            });
            form.setVisible(true);
        });

        JButton editHomeworkButton = createActionButton("Edit", e -> {
            int selectedRow = homeworkTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a homework assignment to edit",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get homework ID from the selected row
            int homeworkId = (int) homeworkTable.getValueAt(selectedRow, 0);

            // Load the homework and open the edit form
            HomeworkDAO homeworkDAO = new HomeworkDAO();
            try {
                Optional<Homework> homeworkOpt = homeworkDAO.getHomeworkById(homeworkId);
                if (homeworkOpt.isPresent()) {
                    Homework homework = homeworkOpt.get();
                    HomeworkForm form = new HomeworkForm(classId, homework);
                    form.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            // Refresh table when form is closed
                            loadHomeworkData(homeworkTableModel);
                        }
                    });
                    form.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not find the selected homework assignment",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error editing homework: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton deleteHomeworkButton = createActionButton("Delete", e -> {
            int selectedRow = homeworkTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a homework assignment to delete",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get homework ID and description
            int homeworkId = (int) homeworkTable.getValueAt(selectedRow, 0);
            String description = (String) homeworkTable.getValueAt(selectedRow, 3);

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this homework assignment?\n" + description,
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                HomeworkDAO homeworkDAO = new HomeworkDAO();
                try {
                    boolean success = homeworkDAO.deleteHomework(homeworkId);
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Homework assignment deleted successfully",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Refresh table
                        loadHomeworkData(homeworkTableModel);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to delete homework assignment",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error deleting homework: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton refreshButton = createActionButton("Refresh", e -> {
            loadHomeworkData(homeworkTableModel);
        });

        // Styling for delete button to use accent color
        deleteHomeworkButton.setBackground(ACCENT_COLOR);
        deleteHomeworkButton.setForeground(Color.WHITE);

        // Add buttons to panel
        buttonPanel.add(deleteHomeworkButton);
        buttonPanel.add(editHomeworkButton);
        buttonPanel.add(addHomeworkButton);
        buttonPanel.add(refreshButton);

        mainContent.add(buttonPanel);

        // Add filter functionality
        statusComboBox.addActionListener(e -> {
            filterHomeworkByStatus(homeworkTableModel, (String) statusComboBox.getSelectedItem());
        });

        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterHomeworkBySearch(homeworkTableModel, searchField.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterHomeworkBySearch(homeworkTableModel, searchField.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterHomeworkBySearch(homeworkTableModel, searchField.getText());
            }
        });

        // Add all to the main homework panel
        homeworkPanel.add(mainContent, BorderLayout.CENTER);

        // Load initial homework data
        loadHomeworkData(homeworkTableModel);
    }

    /**
     * Loads homework data for the current class into the table model
     *
     * @param tableModel The table model to load the data into
     */
    private void loadHomeworkData(DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            // Get homework for the current class from the database
            HomeworkDAO homeworkDAO = new HomeworkDAO();
            List<Homework> homeworkList = homeworkDAO.getHomeworkByClass(classId);

            if (homeworkList.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No homework assignments found for this class",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Format dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                // Get current date for status checking
                Date currentDate = new Date();

                // Add each homework to the table
                for (Homework homework : homeworkList) {
                    // Determine status
                    String status;
                    if (homework.isCompleted()) {
                        status = "Completed";
                    } else if (homework.getDueDate().before(currentDate)) {
                        status = "Overdue";
                    } else {
                        status = "Active";
                    }

                    Object[] rowData = {
                            (Object) homework.getHomeworkId(),
                            dateFormat.format(homework.getAssignmentDate()),
                            dateFormat.format(homework.getDueDate()),
                            homework.getDescription(),
                            status
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading homework: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filters homework table by status
     *
     * @param tableModel The table model to filter
     * @param status The status to filter by (All Assignments, Active, Completed, Overdue)
     */
    private void filterHomeworkByStatus(DefaultTableModel tableModel, String status) {
        if ("All Assignments".equals(status)) {
            // Reload all homework
            loadHomeworkData(tableModel);
        } else {
            // Filter by the selected status
            try {
                HomeworkDAO homeworkDAO = new HomeworkDAO();
                List<Homework> homeworkList = homeworkDAO.getHomeworkByClass(classId);

                // Clear table
                tableModel.setRowCount(0);

                // Format dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                // Get current date for status checking
                Date currentDate = new Date();

                // Add matching homework to the table
                for (Homework homework : homeworkList) {
                    // Determine homework status
                    String homeworkStatus;
                    if (homework.isCompleted()) {
                        homeworkStatus = "Completed";
                    } else if (homework.getDueDate().before(currentDate)) {
                        homeworkStatus = "Overdue";
                    } else {
                        homeworkStatus = "Active";
                    }

                    // Add to table if status matches
                    if (status.equals(homeworkStatus)) {
                        Object[] rowData = {
                                (Object) homework.getHomeworkId(),
                                dateFormat.format(homework.getAssignmentDate()),
                                dateFormat.format(homework.getDueDate()),
                                homework.getDescription(),
                                homeworkStatus
                        };
                        tableModel.addRow(rowData);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error filtering homework: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Filters homework table by search text
     *
     * @param tableModel The table model to filter
     * @param searchText The text to search for in the description
     */
    private void filterHomeworkBySearch(DefaultTableModel tableModel, String searchText) {
        if (searchText.trim().isEmpty()) {
            // Reload all homework
            loadHomeworkData(tableModel);
        } else {
            // Filter by search text
            try {
                HomeworkDAO homeworkDAO = new HomeworkDAO();
                List<Homework> homeworkList = homeworkDAO.getHomeworkByClass(classId);

                // Clear table
                tableModel.setRowCount(0);

                // Format dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                // Get current date for status checking
                Date currentDate = new Date();

                // Convert search text to lowercase for case-insensitive search
                String lowerSearchText = searchText.toLowerCase();

                // Add matching homework to the table
                for (Homework homework : homeworkList) {
                    // Check if description contains search text
                    if (homework.getDescription().toLowerCase().contains(lowerSearchText)) {
                        // Determine homework status
                        String status;
                        if (homework.isCompleted()) {
                            status = "Completed";
                        } else if (homework.getDueDate().before(currentDate)) {
                            status = "Overdue";
                        } else {
                            status = "Active";
                        }

                        Object[] rowData = {
                                (Object) homework.getHomeworkId(),
                                dateFormat.format(homework.getAssignmentDate()),
                                dateFormat.format(homework.getDueDate()),
                                homework.getDescription(),
                                status
                        };
                        tableModel.addRow(rowData);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error searching homework: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBackground(BACKGROUND_COLOR);
        settingsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Panel title
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);

        // Settings sections
        JPanel settingsContent = new JPanel();
        settingsContent.setLayout(new BoxLayout(settingsContent, BoxLayout.Y_AXIS));
        settingsContent.setBackground(CARD_COLOR);
        settingsContent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Profile section with just the two requested options
        JPanel profileSection = createSettingsSection("Profile Settings", new String[] {
                "Edit Profile",
                "Change Password"
        });

        // Add section to content
        settingsContent.add(profileSection);

        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(settingsContent, BorderLayout.CENTER);

        settingsPanel.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a settings section with options
     *
     * @param title Section title
     * @param options Array of option labels
     * @return The section panel
     */
    private JPanel createSettingsSection(String title, String[] options) {
        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(218, 220, 224)));
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(218, 220, 224)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        JPanel optionsPanel = new JPanel(new GridLayout(options.length, 1, 0, 10));
        optionsPanel.setOpaque(false);

        for (String option : options) {
            JPanel optionPanel = new JPanel(new BorderLayout());
            optionPanel.setOpaque(false);

            JButton optionButton = new JButton(option);
            optionButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            optionButton.setHorizontalAlignment(SwingConstants.LEFT);
            optionButton.setBackground(Color.WHITE);
            optionButton.setForeground(TEXT_COLOR);
            optionButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            optionButton.setFocusPainted(false);
            optionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hover effect
            optionButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    optionButton.setBackground(new Color(245, 245, 245));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    optionButton.setBackground(Color.WHITE);
                }
            });

            optionButton.addActionListener(e -> {
                if ("Edit Profile".equals(option)) {
                    showEditProfileDialog();
                } else if ("Change Password".equals(option)) {
                    showChangePasswordDialog();
                }
            });

            optionPanel.add(optionButton, BorderLayout.CENTER);
            optionsPanel.add(optionPanel);
        }

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(optionsPanel, BorderLayout.CENTER);

        return section;
    }
    /**
     * Shows the edit profile dialog with improved field sizing
     */
    private void showEditProfileDialog() {
        // Create dialog with larger size
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(500, 500); // Increased size
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Edit Your Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        // Form panel - using a different layout for better field sizing
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Full Name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);

        JTextField nameField = new JTextField(currentUser.getFullName());
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        nameField.setPreferredSize(new Dimension(300, 40)); // Explicit size

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(TEXT_COLOR);

        JTextField emailField = new JTextField(currentUser.getEmail());
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        emailField.setPreferredSize(new Dimension(300, 40)); // Explicit size

        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addressLabel.setForeground(TEXT_COLOR);

        JTextField addressField = new JTextField(currentUser.getAddress());
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        addressField.setPreferredSize(new Dimension(300, 40)); // Explicit size

        // Phone
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        phoneLabel.setForeground(TEXT_COLOR);

        JTextField phoneField = new JTextField(currentUser.getPhoneNumber());
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        phoneField.setPreferredSize(new Dimension(300, 40)); // Explicit size

        // Add fields to form using GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(phoneField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            // Update user info
            currentUser.setFullName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setAddress(addressField.getText());
            currentUser.setPhoneNumber(phoneField.getText());

            // In a real app, update the database here
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.updateUser(currentUser);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Profile updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to update profile. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add components to content panel
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    /**
     * Shows the change password dialog with BCrypt hashing and improved field sizing
     */
    private void showChangePasswordDialog() {
        // Create dialog with larger size
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(500, 400); // Increased size
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Change Your Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        // Form panel - using a different layout for better field sizing
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Current Password
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        currentPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        currentPasswordLabel.setForeground(TEXT_COLOR);

        JPasswordField currentPasswordField = new JPasswordField();
        currentPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        currentPasswordField.setPreferredSize(new Dimension(300, 40)); // Explicit size

        // New Password
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newPasswordLabel.setForeground(TEXT_COLOR);

        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        newPasswordField.setPreferredSize(new Dimension(300, 40)); // Explicit size

        // Confirm New Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmPasswordLabel.setForeground(TEXT_COLOR);

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        confirmPasswordField.setPreferredSize(new Dimension(300, 40)); // Explicit size

        // Add fields to form using GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(currentPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(currentPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Change Password");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(SECONDARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            // Get password values
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validate inputs
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the user's stored password hash from the database
            UserDAO userDAO = new UserDAO();
            Optional<User> userOpt = userDAO.getUserById(currentUser.getUserId());

            if (!userOpt.isPresent()) {
                JOptionPane.showMessageDialog(dialog,
                        "User not found in database.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String storedPassword = userOpt.get().getPassword();

            // Check if current password matches the stored hash
            boolean passwordMatches;

            // If the password is already hashed in the database
            if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
                // Use BCrypt to verify the password against the hash
                passwordMatches = BCrypt.checkpw(currentPassword, storedPassword);
            } else {
                // Legacy comparison (plaintext) - only for transition period
                passwordMatches = currentPassword.equals(storedPassword);
            }

            if (!passwordMatches) {
                JOptionPane.showMessageDialog(dialog,
                        "Current password is incorrect.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if new passwords match
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog,
                        "New passwords don't match.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Password strength validation
            if (newPassword.length() < 8) {
                JOptionPane.showMessageDialog(dialog,
                        "Password must be at least 8 characters long.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate a salt and hash the new password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

            // Update the user object with the hashed password
            currentUser.setPassword(hashedPassword);

            // Update the database
            boolean success = userDAO.updateUser(currentUser);

            if (success) {
                JOptionPane.showMessageDialog(dialog,
                        "Password changed successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to change password. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add components to content panel
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    /**
     * Helper method to find the grades table in a container
     *
     * @param container The container to search
     * @return The grades table or null if not found
     */
    private JTable findGradesTable(Container container) {
        Component[] components = container.getComponents();

        for (Component component : components) {
            if (component instanceof JTable && "gradesTable".equals(component.getName())) {
                return (JTable) component;
            } else if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTable && "gradesTable".equals(view.getName())) {
                    return (JTable) view;
                }
            } else if (component instanceof Container) {
                JTable table = findGradesTable((Container) component);
                if (table != null) {
                    return table;
                }
            }
        }

        return null;
    }
    /**
     * Starts a timer to update the clock
     */
    private void startClockTimer() {
        // Update the time now
        updateDateTime();

        // Create a timer that fires every second
        clockTimer = new javax.swing.Timer(1000, e -> updateDateTime());
        clockTimer.start();
    }

    /**
     * Updates the date time label
     */
    private void updateDateTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        String dateStr = dateFormat.format(now);
        String timeStr = timeFormat.format(now);

        dateTimeLabel.setText(dateStr + " | " + timeStr);
    }



    /**
     * Loads students data into the table
     */
    private void loadStudentsData() {
        // Clear existing data
        studentsTableModel.setRowCount(0);

        // Get students for the current class
        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students = studentDAO.getStudentsByClass(classId);

            // Add each student to the table
            for (Student student : students) {
                Object[] rowData = {
                        (Object) student.getStudentId(),
                        student.getFirstName(),
                        student.getLastName(),
                        (Object) student.getClassId(),
                        student.getAddress(),
                        (Object) student.getParentId()
                };
                studentsTableModel.addRow(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Navigation
        if (source == dashboardButton || "dashboard".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "dashboard");
            updateActiveTab("dashboard");
        } else if (source == studentsButton || "students".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "students");
            updateActiveTab("students");
        } else if (source == gradesButton || "grades".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "grades");
            updateActiveTab("grades");

            // If student is selected, update the grades view
            if (selectedStudent != null) {
                updateGradesView();
            }
        } else if (source == absencesButton || "absences".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "absences");
            updateActiveTab("absences");
        } else if (source == homeworkButton || "homework".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "homework");
            updateActiveTab("homework");
        } else if (source == settingsButton || "settings".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "settings");
            updateActiveTab("settings");
        } else if (source == logoutButton) {
            // Stop clock timer
            if (clockTimer != null) {
                clockTimer.stop();
            }

            // Log out and return to login screen
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            this.dispose();
        }

        // Student actions
        else if (source == refreshStudentsButton) {
            loadStudentsData();
        } else if (source == addStudentButton) {
            AddStudentForm addStudentForm = new AddStudentForm();
            addStudentForm.setVisible(true);
        } else if (source == editStudentButton) {
            int selectedRow = studentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a student to edit",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get student data from table
            int studentId = (int) studentsTable.getValueAt(selectedRow, 0);
            String firstName = (String) studentsTable.getValueAt(selectedRow, 1);
            String lastName = (String) studentsTable.getValueAt(selectedRow, 2);
            String address = (String) studentsTable.getValueAt(selectedRow, 4);
            int parentId = (int) studentsTable.getValueAt(selectedRow, 5);

            // Create Student object
            Student student = new Student(
                    studentId,
                    classId, // Use current class ID
                    firstName,
                    lastName,
                    address,
                    parentId
            );

            // Open edit form
            EditStudentForm editForm = new EditStudentForm(student);
            editForm.setVisible(true);

            // Refresh table after editing
            editForm.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    loadStudentsData();
                }
            });
        } else if (source == deleteStudentButton) {
            // Get selected student
            int selectedRow = studentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a student to delete",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get student data
            int studentId = (int) studentsTable.getValueAt(selectedRow, 0);
            String firstName = (String) studentsTable.getValueAt(selectedRow, 1);
            String lastName = (String) studentsTable.getValueAt(selectedRow, 2);

            // Confirm deletion
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the student: " + firstName + " " + lastName + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                // Delete student (placeholder)
                JOptionPane.showMessageDialog(this,
                        "Deleting student with ID: " + studentId + "\nThis feature is not yet implemented.",
                        "Delete Student", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    // Add a helper method to find the absences table
    private JTable findAbsencesTable(Container container) {
        Component[] components = container.getComponents();

        for (Component component : components) {
            if (component instanceof JTable && "absencesTable".equals(component.getName())) {
                return (JTable) component;
            } else if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTable && "absencesTable".equals(view.getName())) {
                    return (JTable) view;
                }
            } else if (component instanceof Container) {
                JTable table = findAbsencesTable((Container) component);
                if (table != null) {
                    return table;
                }
            }
        }

        return null;
    }
    private void deleteAbsence(int studentId, String dateStr) {
        try {
            // Get all absences for this student
            AbsenceDAO dao = new AbsenceDAO();
            List<Absence> absences = dao.getAbsencesByStudent(studentId);

            // Find the absence with the matching date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date targetDate = null;
            try {
                targetDate = sdf.parse(dateStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error parsing date: " + dateStr,
                        "Date Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Find the matching absence
            Absence targetAbsence = null;
            for (Absence absence : absences) {
                // Compare dates by formatting to string to avoid time component issues
                String absenceDateStr = sdf.format(absence.getAbsenceDate());
                if (absenceDateStr.equals(dateStr)) {
                    targetAbsence = absence;
                    break;
                }
            }

            if (targetAbsence == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not find absence record for date: " + dateStr,
                        "Record Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // First, delete related excuse records
            int absenceId = targetAbsence.getAbsenceId();
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM excuse WHERE absence_id = ?")) {
                ps.setInt(1, absenceId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException("Failed to delete related excuse records: " + e.getMessage(), e);
            }

            // Now delete the absence
            if (dao.deleteAbsence(absenceId)) {
                JOptionPane.showMessageDialog(this,
                        "Absence deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // Find the absences table
                JTable absencesTable = findAbsencesTable(absencesPanel);
                if (absencesTable != null) {
                    // Refresh the table
                    loadAbsencesForStudent(studentId, (DefaultTableModel) absencesTable.getModel());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete absence",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting absence: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
