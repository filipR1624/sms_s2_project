package gui;

import dao.*;
import model.*;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.List;

/**
 * Horizontal parent dashboard with navigation buttons at the bottom.
 * Features a clean, modern design with improved visual hierarchy.
 */
public class ParentDashboard extends JFrame implements ActionListener {
    // Theme colors
    private static final Color PRIMARY_COLOR = new Color(46, 134, 193); // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Lighter blue
    private static final Color ACCENT_COLOR = new Color(231, 76, 60); // Red accent
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(44, 62, 80); // Dark blue-gray
    private static final Color TEXT_SECONDARY_COLOR = new Color(127, 140, 141); // Gray

    private User currentUser;
    private Parent parent;
    private Student selectedChild;

    // Main components
    private JPanel contentPanel;
    private JPanel mainPanel;
    private JLabel userInfoLabel;
    private JLabel dateTimeLabel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private javax.swing.Timer clockTimer;

    // Navigation buttons
    private JButton dashboardButton;
    private JButton childrenButton;
    private JButton gradesButton;
    private JButton absencesButton;
    private JButton homeworkButton;
    private JButton settingsButton;
    private JButton logoutButton;

    // Dashboard components
    private JPanel dashboardPanel;
    private JLabel childrenCountLabel;
    private JLabel upcomingEventsLabel;

    // Children tab components
    private JPanel childrenPanel;
    private JTable childrenTable;
    private DefaultTableModel childrenTableModel;
    private JButton viewChildButton;
    private JButton refreshChildrenButton;

    // Grades tab components
    private JPanel gradesPanel;
    private JComboBox<String> childComboBox;
    private JTextField searchGradeField;
    private DefaultComboBoxModel<String> childComboModel;
    private Map<String, Integer> childIdMap; // Maps display names to student IDs

    // Absences tab components
    private JPanel absencesPanel;
    private JButton viewAbsencesButton;
    private JButton addExcuseButton;

    // Homework tab components
    private JPanel homeworkPanel;
    private JButton viewHomeworkButton;

    // Settings components
    private JPanel settingsPanel;

    /**
     * Constructor - initializes the parent dashboard
     *
     * @param user The current user
     */
    public ParentDashboard(User user) {
        this.currentUser = user;

        // Get parent information
        ParentDAO parentDAO = new ParentDAO();
        Optional<Parent> parentOptional = parentDAO.getParentByUserId(user.getUserId());

        if (parentOptional.isPresent()) {
            this.parent = parentOptional.get();
        } else {
            // Handle case where parent not found
            JOptionPane.showMessageDialog(this,
                    "Error: Parent profile not found for this user account.",
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        loadParentData();
        loadChildrenData();

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

        userInfoLabel = new JLabel("Parent");
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userInfoLabel.setForeground(new Color(255, 255, 255, 200));

        userInfoPanel.add(nameLabel);
        userInfoPanel.add(userInfoLabel);

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
        createChildrenPanel();
        createGradesPanel();
        createAbsencesPanel();
        createHomeworkPanel();
        createSettingsPanel();

        // Add panels to card layout
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(childrenPanel, "children");
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
        childrenButton = createNavButton("My Children", "children");
        gradesButton = createNavButton("Grades", "grades");
        absencesButton = createNavButton("Absences", "absences");
        homeworkButton = createNavButton("Homework", "homework");
        settingsButton = createNavButton("Settings", "settings");
        logoutButton = createLogoutButton();

        // Add buttons to panel
        navigationPanel.add(dashboardButton);
        navigationPanel.add(childrenButton);
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
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
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
        } else if (childrenButton.getBackground().equals(new Color(245, 245, 245))) {
            activeTab = "children";
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

        childrenButton.setBackground(Color.WHITE);
        childrenButton.setBorder(BorderFactory.createCompoundBorder(
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
            case "children":
                activeButton = childrenButton;
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

        // Stats cards
        JPanel childrenCountCard = createStatCard("My Children", "0", "users");
        JPanel eventsCard = createStatCard("School Events", "No events", "calendar");

        // Get reference to labels for updating later
        childrenCountLabel = (JLabel) ((JPanel) ((JPanel) childrenCountCard.getComponent(0)).getComponent(1)).getComponent(0);
        upcomingEventsLabel = (JLabel) ((JPanel) ((JPanel) eventsCard.getComponent(0)).getComponent(1)).getComponent(0);

        dashboardContent.add(childrenCountCard);
        dashboardContent.add(eventsCard);

        // Quick Actions panel
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

        actionsPanel.add(createActionButton("View My Children", e -> {
            cardLayout.show(cardPanel, "children");
            updateActiveTab("children");
        }));

        actionsPanel.add(createActionButton("Check Grades", e -> {
            cardLayout.show(cardPanel, "grades");
            updateActiveTab("grades");
        }));

        actionsPanel.add(createActionButton("Manage Absences", e -> {
            cardLayout.show(cardPanel, "absences");
            updateActiveTab("absences");
        }));

        actionsPanel.add(createActionButton("View Homework", e -> {
            cardLayout.show(cardPanel, "homework");
            updateActiveTab("homework");
        }));

        activityPanel.add(activityTitle, BorderLayout.NORTH);
        activityPanel.add(actionsPanel, BorderLayout.CENTER);

        // School information panel (placeholder)
        JPanel calendarPanel = new JPanel(new BorderLayout(0, 15));
        calendarPanel.setBackground(CARD_COLOR);
        calendarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel calendarTitle = new JLabel("School Information");
        calendarTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        calendarTitle.setForeground(TEXT_COLOR);

        JLabel schoolInfoLabel = new JLabel("<html>School Hours: 8:00 AM - 3:30 PM<br><br>" +
                "Phone: (555) 123-4567<br><br>" +
                "Email: school@example.com<br><br>" +
                "Next Holiday: Spring Break (April 10-17)</html>");
        schoolInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        schoolInfoLabel.setForeground(TEXT_COLOR);

        calendarPanel.add(calendarTitle, BorderLayout.NORTH);
        calendarPanel.add(schoolInfoLabel, BorderLayout.CENTER);

        // Create bottom panel with activity and calendar
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(activityPanel);
        bottomPanel.add(calendarPanel);

        // Add to dashboard panel
        dashboardPanel.add(welcomePanel, BorderLayout.NORTH);
        dashboardPanel.add(dashboardContent, BorderLayout.CENTER);
        dashboardPanel.add(bottomPanel, BorderLayout.SOUTH);
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

        // Icon placeholder (empty now)
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
     * Creates the children panel with table and controls
     */
    private void createChildrenPanel() {
        childrenPanel = new JPanel(new BorderLayout(0, 20));
        childrenPanel.setBackground(BACKGROUND_COLOR);
        childrenPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Panel title
        JLabel titleLabel = new JLabel("My Children");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);

        // Search and button panel
        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setOpaque(false);



        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);



        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        viewChildButton = createControlButton("View Details", "view");
        refreshChildrenButton = createControlButton("Refresh", "refresh");

        buttonPanel.add(viewChildButton);
        buttonPanel.add(refreshChildrenButton);
        controlPanel.add(buttonPanel, BorderLayout.WEST);

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
        String[] columns = {"ID", "First Name", "Last Name", "Class", "Address"};
        childrenTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create table
        childrenTable = new JTable(childrenTableModel);
        childrenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        childrenTable.setRowHeight(40);
        childrenTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        childrenTable.setGridColor(new Color(240, 240, 240));
        childrenTable.setShowVerticalLines(false);

        // Add selection listener to track selected child
        childrenTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = childrenTable.getSelectedRow();
                if (selectedRow >= 0) {
                    // Get student data from the selected row
                    int studentId = (int) childrenTable.getValueAt(selectedRow, 0);
                    String firstName = (String) childrenTable.getValueAt(selectedRow, 1);
                    String lastName = (String) childrenTable.getValueAt(selectedRow, 2);
                    int classId = Integer.parseInt(childrenTable.getValueAt(selectedRow, 3).toString());
                    String address = (String) childrenTable.getValueAt(selectedRow, 4);

                    // Create a Student object from the selected row data
                    selectedChild = new Student(studentId, classId, firstName, lastName, address, parent.getParentId());
                }
            }
        });

        // Style table header
        JTableHeader header = childrenTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(TEXT_COLOR);

        // Center cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < childrenTable.getColumnCount(); i++) {
            childrenTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(childrenTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add all components to children panel
        childrenPanel.add(topPanel, BorderLayout.NORTH);
        childrenPanel.add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Creates a control button for tables
     *
     * @param text Button text
     * @param type Button type (view, refresh)
     * @return The button
     */
    private JButton createControlButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        // Style based on type
        if ("view".equals(type)) {
            button.setBackground(SECONDARY_COLOR);
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

        // Create main content panel with vertical BoxLayout
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Panel title
        JLabel titleLabel = new JLabel("Grades");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add some vertical space after title
        mainContent.add(titleLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create controls panel for child selection and search
        JPanel controlsPanel = new JPanel(new BorderLayout(15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Child dropdown
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dropdownPanel.setOpaque(false);

        JLabel childLabel = new JLabel("Child:");
        childLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        childLabel.setForeground(TEXT_COLOR);

        childIdMap = new HashMap<>();
        childComboModel = new DefaultComboBoxModel<>();
        childComboBox = new JComboBox<>(childComboModel);
        childComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        childComboBox.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        childComboBox.setBackground(Color.WHITE);
        childComboBox.setPreferredSize(new Dimension(250, 35));

        // Load children into combo box
        loadChildrenIntoComboBox();

        // Add change listener to combo box
        childComboBox.addActionListener(e -> {
            if (childComboBox.getSelectedIndex() > 0) { // Skip the "Select a child" prompt
                String selectedItem = (String) childComboBox.getSelectedItem();
                if (selectedItem != null && childIdMap.containsKey(selectedItem)) {
                    int studentId = childIdMap.get(selectedItem);
                    StudentDAO studentDAO = new StudentDAO();
                    Optional<Student> studentOpt = studentDAO.getStudentById(studentId);
                    if (studentOpt.isPresent()) {
                        selectedChild = studentOpt.get();

                        // Get the table component and update it
                        Component[] components = gradesPanel.getComponents();
                        for (Component comp : components) {
                            if (comp instanceof JPanel) {
                                findAndUpdateGradesTable((JPanel) comp, selectedChild);
                            }
                        }
                    }
                }
            } else {
                // Clear the grades table when "Select a child" is chosen
                Component[] components = gradesPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        findAndClearGradesTable((JPanel) comp);
                    }
                }
            }
        });

        dropdownPanel.add(childLabel);
        dropdownPanel.add(childComboBox);

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
                filterChildren();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterChildren();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterChildren();
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

        JButton refreshGradesButton = createActionButton("Refresh Grades", e -> {
            if (childComboBox.getSelectedIndex() > 0) {
                // Show loading indicator
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    // Get the selected child
                    String selectedItem = (String) childComboBox.getSelectedItem();
                    int studentId = childIdMap.get(selectedItem);
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
                        "Please select a child first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(refreshGradesButton);
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
     * Loads all children into the combo box
     */
    private void loadChildrenIntoComboBox() {
        childComboModel.removeAllElements();
        childIdMap.clear();

        try {
            if (parent != null) {
                StudentDAO studentDAO = new StudentDAO();
                List<Student> children = studentDAO.getStudentsByParent(parent.getParentId());

                // Add default prompt
                childComboModel.addElement("Select a child...");

                // Sort children by last name, then first name
                Collections.sort(children, (s1, s2) -> {
                    int lastNameComparison = s1.getLastName().compareTo(s2.getLastName());
                    if (lastNameComparison != 0) {
                        return lastNameComparison;
                    }
                    return s1.getFirstName().compareTo(s2.getFirstName());
                });

                // Add each child to the combo box
                for (Student child : children) {
                    String displayName = child.getLastName() + ", " + child.getFirstName() + " (ID: " + child.getStudentId() + ")";
                    childComboModel.addElement(displayName);
                    childIdMap.put(displayName, Integer.valueOf(child.getStudentId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading children: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filters children in the combo box based on search text
     */
    private void filterChildren() {
        String searchText = searchGradeField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            // Reload all children
            loadChildrenIntoComboBox();
            return;
        }

        // Store currently selected item
        Object selectedItem = childComboBox.getSelectedItem();

        // Clear combo box
        childComboModel.removeAllElements();

        // Add default prompt
        childComboModel.addElement("Select a child...");

        // Add matching children
        for (String displayName : childIdMap.keySet()) {
            if (displayName.toLowerCase().contains(searchText)) {
                childComboModel.addElement(displayName);
            }
        }

        // Restore selection if it exists in filtered results
        if (selectedItem != null) {
            for (int i = 0; i < childComboModel.getSize(); i++) {
                if (childComboModel.getElementAt(i).equals(selectedItem)) {
                    childComboBox.setSelectedIndex(i);
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
    // For the loadGradesForStudent method in ParentDashboard.java
// We need to enhance it to include grade statistics and better data presentation

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

                // Calculate statistics
                double average = 0;
                char bestGrade = 'F';
                char worstGrade = 'A';

                // Add each grade to the table and calculate statistics
                for (Grade grade : grades) {
                    Object[] rowData = {
                            grade.getSubject(),
                            String.valueOf(grade.getMark()),
                            dateFormat.format(grade.getGradeDate()),
                            grade.getComment()
                    };
                    tableModel.addRow(rowData);

                    // Update statistics
                    char mark = grade.getMark();
                    if (mark < bestGrade) bestGrade = mark; // A is "better" than F
                    if (mark > worstGrade) worstGrade = mark;

                    // Calculate numeric value for average (A=5, B=4, etc.)
                    int numericValue = calculateNumericGrade(mark);
                    average += numericValue;
                }

                // Calculate final average
                average = average / grades.size();

                // Show success message with statistics
                JOptionPane.showMessageDialog(this,
                        String.format("Loaded %d grades for %s %s\nAverage: %.1f\nBest Grade: %c\nWorst Grade: %c",
                                grades.size(), student.getFirstName(), student.getLastName(),
                                average, bestGrade, worstGrade),
                        "Grades Loaded", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading grades: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculateNumericGrade(char grade) {
        switch (grade) {
            case 'A': return 5;
            case 'B': return 4;
            case 'C': return 3;
            case 'D': return 2;
            case 'F': return 1;
            default: return 0;
        }
    }

    /**
     * Creates the absences panel
     */
    private void createAbsencesPanel() {
        absencesPanel = new JPanel(new BorderLayout());
        absencesPanel.setBackground(BACKGROUND_COLOR);
        absencesPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Create main content panel with vertical BoxLayout
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Panel title
        JLabel titleLabel = new JLabel("Absences");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add some vertical space after title
        mainContent.add(titleLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create controls panel for child selection and search
        JPanel controlsPanel = new JPanel(new BorderLayout(15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Child dropdown
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dropdownPanel.setOpaque(false);

        JLabel childLabel = new JLabel("Child:");
        childLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        childLabel.setForeground(TEXT_COLOR);

        // Reuse or create new combo model and map for absences
        JComboBox<String> absenceChildComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        absenceChildComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        absenceChildComboBox.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        absenceChildComboBox.setBackground(Color.WHITE);
        absenceChildComboBox.setPreferredSize(new Dimension(250, 35));

        // Load children into combo box
        DefaultComboBoxModel<String> absenceChildComboModel = (DefaultComboBoxModel<String>)absenceChildComboBox.getModel();
        absenceChildComboModel.removeAllElements();
        absenceChildComboModel.addElement("Select a child...");

        // We'll use the same child map as in the grades panel
        Map<String, Integer> absenceChildIdMap = new HashMap<>();

        if (parent != null) {
            try {
                StudentDAO studentDAO = new StudentDAO();
                List<Student> children = studentDAO.getStudentsByParent(parent.getParentId());

                // Sort children by last name, then first name
                Collections.sort(children, (s1, s2) -> {
                    int lastNameComparison = s1.getLastName().compareTo(s2.getLastName());
                    if (lastNameComparison != 0) {
                        return lastNameComparison;
                    }
                    return s1.getFirstName().compareTo(s2.getFirstName());
                });

                // Add each child to the combo box
                for (Student child : children) {
                    String displayName = child.getLastName() + ", " + child.getFirstName() + " (ID: " + child.getStudentId() + ")";
                    absenceChildComboModel.addElement(displayName);
                    absenceChildIdMap.put(displayName, Integer.valueOf(child.getStudentId()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error loading children: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        dropdownPanel.add(childLabel);
        dropdownPanel.add(absenceChildComboBox);

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
                filterAbsenceChildren(searchAbsenceField, absenceChildComboBox, absenceChildComboModel, absenceChildIdMap);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterAbsenceChildren(searchAbsenceField, absenceChildComboBox, absenceChildComboModel, absenceChildIdMap);
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterAbsenceChildren(searchAbsenceField, absenceChildComboBox, absenceChildComboModel, absenceChildIdMap);
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
        String[] columns = {"Absence Date", "Description", "Status"};
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

        // Add child selection listener to update absences
        absenceChildComboBox.addActionListener(e -> {
            if (absenceChildComboBox.getSelectedIndex() > 0) { // Skip the "Select a child" prompt
                String selectedItem = (String) absenceChildComboBox.getSelectedItem();
                if (selectedItem != null && absenceChildIdMap.containsKey(selectedItem)) {
                    int studentId = absenceChildIdMap.get(selectedItem);
                    loadAbsencesForStudent(studentId, absencesTableModel);
                }
            } else {
                // Clear the absences table when "Select a child" is chosen
                absencesTableModel.setRowCount(0);
            }
        });

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        viewAbsencesButton = createActionButton("View Absences", e -> {
            if (absenceChildComboBox.getSelectedIndex() > 0) {
                // Show loading indicator
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    // Get the selected student ID
                    String selectedItem = (String) absenceChildComboBox.getSelectedItem();
                    int studentId = absenceChildIdMap.get(selectedItem);

                    // Load absences for the student
                    loadAbsencesForStudent(studentId, absencesTableModel);
                } finally {
                    // Restore cursor
                    setCursor(Cursor.getDefaultCursor());
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a child first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        addExcuseButton = createActionButton("Add Excuse", e -> {
            if (absenceChildComboBox.getSelectedIndex() > 0) {
                int selectedRow = absencesTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this,
                            "Please select an absence to add an excuse for",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String dateStr = (String) absencesTable.getValueAt(selectedRow, 0);
                String status = (String) absencesTable.getValueAt(selectedRow, 2);

                if (status.equalsIgnoreCase("Excused")) {
                    JOptionPane.showMessageDialog(this,
                            "This absence is already excused",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Get selected child ID
                String selectedItem = (String) absenceChildComboBox.getSelectedItem();
                int studentId = absenceChildIdMap.get(selectedItem);

                // Open excuse form dialog
                showAddExcuseDialog(studentId, dateStr);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a child first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(addExcuseButton);
        buttonPanel.add(viewAbsencesButton);

        mainContent.add(buttonPanel);

        // Add all to the main absences panel
        absencesPanel.add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Shows the add excuse dialog for a specific absence
     *
     * @param studentId The student ID
     * @param dateStr The absence date string
     */
    // Enhanced showAddExcuseDialog method in ParentDashboard.java
    private void showAddExcuseDialog(int studentId, String dateStr) {
        // Create dialog with improved UI
        JDialog dialog = new JDialog(this, "Add Excuse", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Create content panel with better styling
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Info label with improved visibility
        JLabel infoLabel = new JLabel("Add excuse for absence on " + dateStr);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setForeground(TEXT_COLOR);

        // Excuse type selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setOpaque(false);
        JLabel typeLabel = new JLabel("Excuse Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        String[] excuseTypes = {"Illness", "Family Emergency", "Medical Appointment", "Other"};
        JComboBox<String> typeCombo = new JComboBox<>(excuseTypes);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeCombo.setPreferredSize(new Dimension(200, 30));

        typePanel.add(typeLabel);
        typePanel.add(typeCombo);

        // Excuse description with improved UI
        JLabel descriptionLabel = new JLabel("Excuse details:");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descriptionLabel.setForeground(TEXT_COLOR);

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(450, 150));

        // Documentation upload option (would need additional implementation)
        JPanel docPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        docPanel.setOpaque(false);
        JLabel docLabel = new JLabel("Attach document (optional):");
        docLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton browseButton = new JButton("Browse...");
        browseButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        browseButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                    "Document upload functionality would be implemented here",
                    "Feature Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });

        docPanel.add(docLabel);
        docPanel.add(browseButton);

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

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(SECONDARY_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> {
            String excuseText = descriptionArea.getText().trim();
            if (excuseText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter an excuse description",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Here's where we'd implement the actual excuse submission logic
            try {
                // Example of updating absence status in the database
                AbsenceDAO absenceDAO = new AbsenceDAO();

                // In a real implementation, we would find the specific absence by date
                List<Absence> absences = absenceDAO.getAbsencesByStudent(studentId);
                boolean found = false;

                for (Absence absence : absences) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (sdf.format(absence.getAbsenceDate()).equals(dateStr)) {
                        // Update the absence to excused
                        absence.setStatus(true);
                        absenceDAO.updateAbsence(absence);
                        found = true;

                        // In a real application, we would also save the excuse text
                        // to a related table

                        break;
                    }
                }

                if (found) {
                    JOptionPane.showMessageDialog(dialog,
                            "Excuse submitted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();

                    // Refresh absences table
                    loadAbsencesForStudent(studentId, (DefaultTableModel) ((JTable) findAbsencesTable(absencesPanel)).getModel());
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Could not find the specific absence record",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error submitting excuse: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        // Add components to content panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(infoLabel, BorderLayout.NORTH);

        // Create form panel for all inputs
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.add(typePanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(descriptionLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(scrollPane);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(docPanel);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    /**
     * Filters students in the absence combo box based on search text
     */
    private void filterAbsenceChildren(JTextField searchField, JComboBox<String> comboBox,
                                       DefaultComboBoxModel<String> comboModel, Map<String, Integer> childIdMap) {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            // Reload all children
            comboModel.removeAllElements();
            comboModel.addElement("Select a child...");
            for (String displayName : childIdMap.keySet()) {
                comboModel.addElement(displayName);
            }
            return;
        }

        // Store currently selected item
        Object selectedItem = comboBox.getSelectedItem();

        // Clear combo box
        comboModel.removeAllElements();

        // Add default prompt
        comboModel.addElement("Select a child...");

        // Add matching children
        for (String displayName : childIdMap.keySet()) {
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
    // Method to load homework data for a student
    private void loadHomeworkData(int studentId, DefaultTableModel tableModel) {
        // Clear existing data
        tableModel.setRowCount(0);

        try {
            // Get class ID for the student
            StudentDAO studentDAO = new StudentDAO();
            Optional<Student> studentOpt = studentDAO.getStudentById(studentId);

            if (!studentOpt.isPresent()) {
                JOptionPane.showMessageDialog(this,
                        "Student not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int classId = studentOpt.get().getClassId();

            // Get homework for the class
            HomeworkDAO homeworkDAO = new HomeworkDAO();
            List<Homework> homeworkList = homeworkDAO.getHomeworkByClass(classId);

            if (homeworkList.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No homework found for this student",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Current date for calculating days remaining
            Date now = new Date();

            // Format for displaying dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Add homework to table
            for (Homework homework : homeworkList) {
                // Calculate days remaining
                long diffInMillies = homework.getDueDate().getTime() - now.getTime();
                int daysRemaining = (int) (diffInMillies / (1000 * 60 * 60 * 24));

                // Determine status
                String status;
                if (homework.isStatus()) {
                    status = "Completed";
                } else if (homework.getDueDate().before(now)) {
                    status = "Overdue";
                } else {
                    status = "Pending";
                }

                // Add to table
                Object[] rowData = {
                        homework.getDescription(),
                        dateFormat.format(homework.getDueDate()),
                        "Class " + classId,
                        status,
                        daysRemaining
                };

                tableModel.addRow(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading homework: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Creates the settings panel with only Edit Profile, Change Password, and Manage Child Information
     */
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

        // Settings section
        JPanel settingsContent = new JPanel();
        settingsContent.setLayout(new BoxLayout(settingsContent, BoxLayout.Y_AXIS));
        settingsContent.setBackground(CARD_COLOR);
        settingsContent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Profile section with only the three requested options
        JPanel profileSection = createSettingsSection("Profile Settings", new String[] {
                "Edit Profile",
                "Change Password",
                "Manage Child Information"
        });

        // Add section to content
        settingsContent.add(profileSection);

        // Add components to panels
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(settingsContent, BorderLayout.CENTER);

        settingsPanel.add(contentPanel, BorderLayout.CENTER);
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
                            dateFormat.format(absence.getAbsenceDate()),
                            absence.getDescription(),
                            absence.isStatus() ? "Excused" : "Unexcused"
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
     * Helper method to find the absences table in a container
     *
     * @param container The container to search
     * @return The absences table or null if not found
     */
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

    /**
     * Creates the homework panel
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
        JLabel titleLabel = new JLabel("Homework");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add some vertical space after title
        mainContent.add(titleLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create controls panel for child selection and search
        JPanel controlsPanel = new JPanel(new BorderLayout(15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Child dropdown
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dropdownPanel.setOpaque(false);

        JLabel childLabel = new JLabel("Child:");
        childLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        childLabel.setForeground(TEXT_COLOR);

        // Create combo box for child selection
        JComboBox<String> homeworkChildComboBox = new JComboBox<>();
        homeworkChildComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        homeworkChildComboBox.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        homeworkChildComboBox.setBackground(Color.WHITE);
        homeworkChildComboBox.setPreferredSize(new Dimension(250, 35));

        // Load children names into combo box
        DefaultComboBoxModel<String> homeworkChildModel = new DefaultComboBoxModel<>();
        homeworkChildModel.addElement("Select a child...");

        // Use the same mapping approach as in other panels
        Map<String, Integer> homeworkChildMap = new HashMap<>();

        if (parent != null) {
            try {
                StudentDAO studentDAO = new StudentDAO();
                List<Student> children = studentDAO.getStudentsByParent(parent.getParentId());

                for (Student child : children) {
                    String displayName = child.getLastName() + ", " + child.getFirstName();
                    homeworkChildModel.addElement(displayName);
                    homeworkChildMap.put(displayName, child.getStudentId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        homeworkChildComboBox.setModel(homeworkChildModel);
        dropdownPanel.add(childLabel);
        dropdownPanel.add(homeworkChildComboBox);

        // Status filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(TEXT_COLOR);

        String[] statuses = {"All", "Pending", "Completed", "Overdue"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusComboBox.setPreferredSize(new Dimension(150, 35));

        filterPanel.add(statusLabel);
        filterPanel.add(statusComboBox);

        controlsPanel.add(dropdownPanel, BorderLayout.WEST);
        controlsPanel.add(filterPanel, BorderLayout.EAST);

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
        String[] columns = {"Assignment", "Due Date", "Class", "Status", "Days Remaining"};
        DefaultTableModel homeworkTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable homeworkTable = new JTable(homeworkTableModel);
        homeworkTable.setName("homeworkTable");
        homeworkTable.setRowHeight(40);
        homeworkTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        homeworkTable.setGridColor(new Color(240, 240, 240));
        homeworkTable.setShowVerticalLines(false);

        // Style table header
        JTableHeader header = homeworkTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Add a custom renderer for Status column to color-code statuses
        homeworkTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                String status = (String) value;
                if ("Completed".equals(status)) {
                    c.setForeground(new Color(40, 167, 69)); // Green
                } else if ("Overdue".equals(status)) {
                    c.setForeground(new Color(220, 53, 69)); // Red
                } else {
                    c.setForeground(new Color(0, 123, 255)); // Blue
                }

                return c;
            }
        });

        // Add renderer for Days Remaining column
        homeworkTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (value instanceof Integer) {
                    int days = (Integer) value;
                    if (days < 0) {
                        c.setForeground(new Color(220, 53, 69)); // Red for overdue
                    } else if (days <= 2) {
                        c.setForeground(new Color(255, 193, 7)); // Yellow for soon
                    } else {
                        c.setForeground(new Color(40, 167, 69)); // Green for plenty of time
                    }
                }

                return c;
            }
        });

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(homeworkTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);

        homeworkTablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(homeworkTablePanel);

        // Add action listener to child combo box
        homeworkChildComboBox.addActionListener(e -> {
            if (homeworkChildComboBox.getSelectedIndex() > 0) {
                String selectedName = (String) homeworkChildComboBox.getSelectedItem();
                int studentId = homeworkChildMap.get(selectedName);
                loadHomeworkData(studentId, homeworkTableModel);
            } else {
                // Clear table if "Select a child" is chosen
                homeworkTableModel.setRowCount(0);
            }
        });

        // Add action listener to status combo box
        statusComboBox.addActionListener(e -> {
            if (homeworkChildComboBox.getSelectedIndex() > 0) {
                String selectedName = (String) homeworkChildComboBox.getSelectedItem();
                int studentId = homeworkChildMap.get(selectedName);
                String status = (String) statusComboBox.getSelectedItem();
                filterHomeworkByStatus(studentId, status, homeworkTableModel);
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Refresh button
        viewHomeworkButton = createActionButton("Refresh", e -> {
            if (homeworkChildComboBox.getSelectedIndex() > 0) {
                String selectedName = (String) homeworkChildComboBox.getSelectedItem();
                int studentId = homeworkChildMap.get(selectedName);
                loadHomeworkData(studentId, homeworkTableModel);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a child first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Mark as completed button (for parent to verify homework is complete)
        JButton markCompletedButton = createActionButton("Mark as Completed", e -> {
            int selectedRow = homeworkTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a homework assignment",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String description = (String) homeworkTable.getValueAt(selectedRow, 0);
            String dueDateStr = (String) homeworkTable.getValueAt(selectedRow, 1);

// Confirm with the user
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Mark homework \"" + description + "\" as completed?",
                    "Confirm Status Update", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Get the student ID from the child selection
                    String selectedName = (String) homeworkChildComboBox.getSelectedItem();
                    int studentId = homeworkChildMap.get(selectedName);

                    // Get the student and class info
                    StudentDAO studentDAO = new StudentDAO();
                    Optional<Student> studentOpt = studentDAO.getStudentById(studentId);

                    if (!studentOpt.isPresent()) {
                        throw new Exception("Student not found");
                    }

                    int classId = studentOpt.get().getClassId();

                    // Find the homework in the database
                    HomeworkDAO homeworkDAO = new HomeworkDAO();
                    List<Homework> homeworkList = homeworkDAO.getHomeworkByClass(classId);

                    // Find the specific homework by description and due date
                    Homework targetHomework = null;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    for (Homework hw : homeworkList) {
                        if (hw.getDescription().equals(description) &&
                                dateFormat.format(hw.getDueDate()).equals(dueDateStr)) {
                            targetHomework = hw;
                            break;
                        }
                    }

                    if (targetHomework == null) {
                        throw new Exception("Homework assignment not found");
                    }

                    // Update the status to completed
                    boolean success = homeworkDAO.updateHomeworkStatus(targetHomework.getHomeworkId(), true);

                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Homework marked as completed successfully!",
                                "Status Updated", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh the homework table
                        loadHomeworkData(studentId, (DefaultTableModel) homeworkTable.getModel());
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to update homework status",
                                "Update Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error updating homework status: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        buttonPanel.add(viewHomeworkButton);

        mainContent.add(buttonPanel);

        homeworkPanel.add(mainContent, BorderLayout.CENTER);
    }
    // Method to filter homework by status
    private void filterHomeworkByStatus(int studentId, String statusFilter, DefaultTableModel tableModel) {
        // If "All" is selected, just reload all homework
        if ("All".equals(statusFilter)) {
            loadHomeworkData(studentId, tableModel);
            return;
        }

        // Otherwise, clear the table and add only matching homework
        tableModel.setRowCount(0);

        try {
            // Get class ID for the student
            StudentDAO studentDAO = new StudentDAO();
            Optional<Student> studentOpt = studentDAO.getStudentById(studentId);

            if (!studentOpt.isPresent()) {
                return;
            }

            int classId = studentOpt.get().getClassId();

            // Get homework for the class
            HomeworkDAO homeworkDAO = new HomeworkDAO();
            List<Homework> homeworkList = homeworkDAO.getHomeworkByClass(classId);

            // Current date for calculating days remaining and status
            Date now = new Date();

            // Format for displaying dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Add matching homework to table
            for (Homework homework : homeworkList) {
                // Calculate days remaining
                long diffInMillies = homework.getDueDate().getTime() - now.getTime();
                int daysRemaining = (int) (diffInMillies / (1000 * 60 * 60 * 24));

                // Determine status
                String status;
                if (homework.isStatus()) {
                    status = "Completed";
                } else if (homework.getDueDate().before(now)) {
                    status = "Overdue";
                } else {
                    status = "Pending";
                }

                // Add to table if status matches filter
                if (status.equals(statusFilter)) {
                    Object[] rowData = {
                            homework.getDescription(),
                            dateFormat.format(homework.getDueDate()),
                            "Class " + classId,
                            status,
                            daysRemaining
                    };

                    tableModel.addRow(rowData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

            // Different action listeners based on option
            optionButton.addActionListener(e -> {
                switch (option) {
                    case "Edit Profile":
                        showEditProfileDialog();
                        break;
                    case "Change Password":
                        showChangePasswordDialog();
                        break;
                    case "Update Contact Information":
                        showUpdateContactDialog();
                        break;
                    case "Manage Child Information":
                        showManageChildInfoDialog();
                        break;
                    default:
                        JOptionPane.showMessageDialog(this,
                                option + " feature coming soon!",
                                "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            optionPanel.add(optionButton, BorderLayout.CENTER);
            optionsPanel.add(optionPanel);
        }

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(optionsPanel, BorderLayout.CENTER);

        return section;
    }

    // Method to show child information management dialog
    // Method to show child information management dialog
    private void showManageChildInfoDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Manage Child Information", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Manage Child Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        // Child selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setOpaque(false);

        JLabel childLabel = new JLabel("Select Child:");
        childLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JComboBox<String> childCombo = new JComboBox<>();
        childCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        childCombo.setPreferredSize(new Dimension(250, 30));

        // Load children into combo box
        Map<String, Integer> childSelectionMap = new HashMap<>();
        if (parent != null) {
            try {
                StudentDAO studentDAO = new StudentDAO();
                List<Student> children = studentDAO.getStudentsByParent(parent.getParentId());

                for (Student child : children) {
                    String displayName = child.getFirstName() + " " + child.getLastName();
                    childCombo.addItem(displayName);
                    childSelectionMap.put(displayName, child.getStudentId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        selectionPanel.add(childLabel);
        selectionPanel.add(childCombo);

        // Child details form
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);

        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();

        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField();

        JLabel dobLabel = new JLabel("Date of Birth:");
        JTextField dobField = new JTextField();

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();

        JLabel allergiesLabel = new JLabel("Allergies/Medical Notes:");
        JTextArea allergiesArea = new JTextArea();
        allergiesArea.setLineWrap(true);
        JScrollPane allergiesScroll = new JScrollPane(allergiesArea);

        // Style form components
        firstNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lastNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dobLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        allergiesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dobField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        allergiesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add form components
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);
        formPanel.add(dobLabel);
        formPanel.add(dobField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(allergiesLabel);
        formPanel.add(allergiesScroll);

        // Load initial child data if available
        if (childCombo.getItemCount() > 0) {
            String selectedChild = (String) childCombo.getSelectedItem();
            int studentId = childSelectionMap.get(selectedChild);

            try {
                StudentDAO studentDAO = new StudentDAO();
                Optional<Student> studentOpt = studentDAO.getStudentById(studentId);

                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    firstNameField.setText(student.getFirstName());
                    lastNameField.setText(student.getLastName());
                    addressField.setText(student.getAddress());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Add change listener to combo box
        childCombo.addActionListener(e -> {
            if (childCombo.getSelectedItem() != null) {
                String selectedChild = (String) childCombo.getSelectedItem();
                int studentId = childSelectionMap.get(selectedChild);

                try {
                    StudentDAO studentDAO = new StudentDAO();
                    Optional<Student> studentOpt = studentDAO.getStudentById(studentId);

                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        firstNameField.setText(student.getFirstName());
                        lastNameField.setText(student.getLastName());
                        addressField.setText(student.getAddress());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

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
            if (childCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select a child",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get form data
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String address = addressField.getText().trim();

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Name fields cannot be empty",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected student
            String selectedChild = (String) childCombo.getSelectedItem();
            int studentId = childSelectionMap.get(selectedChild);

            try {
                StudentDAO studentDAO = new StudentDAO();
                Optional<Student> studentOpt = studentDAO.getStudentById(studentId);

                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();

                    // Update student data
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    student.setAddress(address);

                    // Save changes
                    boolean success = studentDAO.updateStudent(student);

                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Student information updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();

                        // Refresh data in the children table if visible
                        loadChildrenData();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Failed to update student information",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error updating student information: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Assemble dialog
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(selectionPanel, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    // Method to show contact information update dialog
    private void showUpdateContactDialog() {
        // Create dialog with a form similar to Edit Profile but focused on contact info
        JDialog dialog = new JDialog(this, "Update Contact Information", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Update Your Contact Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);

        // Address fields
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addressLabel.setForeground(TEXT_COLOR);

        JTextField addressField = new JTextField(currentUser.getAddress());
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(addressLabel);
        formPanel.add(addressField);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        phoneLabel.setForeground(TEXT_COLOR);

        JTextField phoneField = new JTextField(currentUser.getPhoneNumber());
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(phoneLabel);
        formPanel.add(phoneField);

        // Emergency contact field
        JLabel emergencyLabel = new JLabel("Emergency Contact:");
        emergencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emergencyLabel.setForeground(TEXT_COLOR);

        JTextField emergencyField = new JTextField(""); // This would be loaded from a separate table in a real app
        emergencyField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emergencyField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(emergencyLabel);
        formPanel.add(emergencyField);

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
            // Update user object
            currentUser.setAddress(addressField.getText().trim());
            currentUser.setPhoneNumber(phoneField.getText().trim());

            // Save to database
            try {
                UserDAO userDAO = new UserDAO();
                boolean success = userDAO.updateUser(currentUser);

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            "Contact information updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Failed to update contact information",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error updating contact information: " + ex.getMessage(),
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

    private void showChangePasswordDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(450, 320);
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

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);

        // Current password field
        JLabel currentPassLabel = new JLabel("Current Password:");
        currentPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        currentPassLabel.setForeground(TEXT_COLOR);

        JPasswordField currentPassField = new JPasswordField();
        currentPassField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentPassField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(currentPassLabel);
        formPanel.add(currentPassField);

        // New password field
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newPassLabel.setForeground(TEXT_COLOR);

        JPasswordField newPassField = new JPasswordField();
        newPassField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPassField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(newPassLabel);
        formPanel.add(newPassField);

        // Confirm password field
        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        confirmPassLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmPassLabel.setForeground(TEXT_COLOR);

        JPasswordField confirmPassField = new JPasswordField();
        confirmPassField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPassField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(confirmPassLabel);
        formPanel.add(confirmPassField);

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
            // Validate inputs
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "All fields are required",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog,
                        "New passwords don't match",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPass.length() < 8) {
                JOptionPane.showMessageDialog(dialog,
                        "New password must be at least 8 characters long",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verify current password
            try {
                // For this example, we'll use direct comparison
                // In a real application, you'd use a secure password verification method
                if (!currentPass.equals(currentUser.getPassword())) {
                    JOptionPane.showMessageDialog(dialog,
                            "Current password is incorrect",
                            "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update password
                String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(newPass, org.mindrot.jbcrypt.BCrypt.gensalt(12));
                currentUser.setPassword(hashedPassword);

                // Save to database
                UserDAO userDAO = new UserDAO();
                boolean success = userDAO.updateUser(currentUser);

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            "Password changed successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Failed to change password",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error changing password: " + ex.getMessage(),
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

    private void showEditProfileDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(500, 400);
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

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);

        // Full name field
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);

        JTextField nameField = new JTextField(currentUser.getFullName());
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(nameLabel);
        formPanel.add(nameField);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(TEXT_COLOR);

        JTextField emailField = new JTextField(currentUser.getEmail());
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // Address field
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addressLabel.setForeground(TEXT_COLOR);

        JTextField addressField = new JTextField(currentUser.getAddress());
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(addressLabel);
        formPanel.add(addressField);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        phoneLabel.setForeground(TEXT_COLOR);

        JTextField phoneField = new JTextField(currentUser.getPhoneNumber());
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        formPanel.add(phoneLabel);
        formPanel.add(phoneField);

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
            // Validate inputs
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String phoneNumber = phoneField.getText().trim();

            if (fullName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Name and email cannot be empty",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Email format validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(dialog,
                        "Invalid email format",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update user object
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setAddress(address);
            currentUser.setPhoneNumber(phoneNumber);

            // Save to database
            try {
                UserDAO userDAO = new UserDAO();
                boolean success = userDAO.updateUser(currentUser);

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            "Profile updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();

                    // Update header with new name if needed
                    updateUserInfo();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Failed to update profile",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error updating profile: " + ex.getMessage(),
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

    private void updateUserInfo() {
        // Update header label with user's full name
        Component header = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (header instanceof JPanel) {
            Component nameComp = ((BorderLayout) ((JPanel) header).getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (nameComp instanceof JPanel) {
                Component nameLabel = ((JPanel) nameComp).getComponent(0);
                if (nameLabel instanceof JLabel) {
                    ((JLabel) nameLabel).setText(currentUser.getFullName());
                }
            }
        }
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
     * Loads parent data
     */
    private void loadParentData() {
        try {
            if (parent != null) {
                userInfoLabel.setText("Parent - ID: " + parent.getParentId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            userInfoLabel.setText("Parent");
        }
    }

    /**
     * Loads children data into the table
     */
    private void loadChildrenData() {
        // Clear existing data
        childrenTableModel.setRowCount(0);

        if (parent != null) {
            // Get children for the parent
            try {
                StudentDAO studentDAO = new StudentDAO();
                List<Student> children = studentDAO.getStudentsByParent(parent.getParentId());

                // Update children count on dashboard
                childrenCountLabel.setText(String.valueOf(children.size()));

                // Add each child to the table
                for (Student child : children) {
                    Object[] rowData = {
                            child.getStudentId(),
                            child.getFirstName(),
                            child.getLastName(),
                            child.getClassId(),
                            child.getAddress()
                    };
                    childrenTableModel.addRow(rowData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error loading children: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates the actionPerformed method to handle all button clicks and navigation
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // Navigation
        if (source == dashboardButton || "dashboard".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "dashboard");
            updateActiveTab("dashboard");
        } else if (source == childrenButton || "children".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "children");
            updateActiveTab("children");

            // Refresh children data when navigating to this tab
            loadChildrenData();
        } else if (source == gradesButton || "grades".equals(e.getActionCommand())) {
            cardLayout.show(cardPanel, "grades");
            updateActiveTab("grades");
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

        // Children panel actions
        else if (source == refreshChildrenButton) {
            loadChildrenData();
        } else if (source == viewChildButton) {
            int selectedRow = childrenTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a child to view",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get child data from table
            int studentId = (int) childrenTable.getValueAt(selectedRow, 0);
            String firstName = (String) childrenTable.getValueAt(selectedRow, 1);
            String lastName = (String) childrenTable.getValueAt(selectedRow, 2);
            Object classIdObj = childrenTable.getValueAt(selectedRow, 3);
            int classId = classIdObj instanceof Integer ? (Integer) classIdObj :
                    Integer.parseInt(classIdObj.toString());

            // Show child details
            showChildDetails(studentId, firstName, lastName, classId);
        }
    }

    /**
     * Shows details for a selected child in a dialog
     *
     * @param studentId The student ID
     * @param firstName The student's first name
     * @param lastName The student's last name
     * @param classId The class ID
     */
    /**
     * Enhanced method to show child details with a more informative dialog
     */
    private void showChildDetails(int studentId, String firstName, String lastName, int classId) {
        // Create dialog
        JDialog dialog = new JDialog(this, firstName + " " + lastName + " Details", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Create student info panel (left side)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        // Student photo placeholder
        JPanel photoPanel = new JPanel();
        photoPanel.setBackground(new Color(230, 230, 230));
        photoPanel.setPreferredSize(new Dimension(150, 150));
        photoPanel.setMaximumSize(new Dimension(150, 150));
        photoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel photoLabel = new JLabel("Student Photo");
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setVerticalAlignment(JLabel.CENTER);
        photoPanel.add(photoLabel);

        // Student name
        JLabel nameLabel = new JLabel(firstName + " " + lastName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(10, 0, 5, 0));

        // Student ID
        JLabel idLabel = new JLabel("Student ID: " + studentId);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Class info
        JLabel classLabel = new JLabel("Class: " + classId);
        classLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        classLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        // Get teacher name for this class
        String teacherName = "Not assigned";
        try {
            TeacherDAO teacherDAO = new TeacherDAO();
            List<Teacher> teachers = teacherDAO.getTeachersByClassId(classId);
            if (!teachers.isEmpty()) {
                Teacher teacher = teachers.get(0);
                UserDAO userDAO = new UserDAO();
                Optional<User> teacherUser = userDAO.getUserById(teacher.getUserId());
                if (teacherUser.isPresent()) {
                    teacherName = teacherUser.get().getFullName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Teacher info
        JLabel teacherNameLabel = new JLabel("Teacher: " + teacherName);
        teacherNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        teacherNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        teacherNameLabel.setBorder(new EmptyBorder(5, 0, 20, 0));

        // Quick stats panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(250, 100));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Quick Stats"));

        // Get grade average
        String gradeAvg = "N/A";
        try {
            GradeDAO gradeDAO = new GradeDAO();
            double avg = gradeDAO.getAverageGradeForStudent(studentId);
            if (avg > 0) {
                gradeAvg = String.format("%.1f", avg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get absence count
        String absenceCount = "0";
        try {
            AbsenceDAO absenceDAO = new AbsenceDAO();
            int count = absenceDAO.countAbsencesByStudent(studentId);
            absenceCount = String.valueOf(count);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add stats to panel
        addStatRow(statsPanel, "Grades Avg:", gradeAvg);
        addStatRow(statsPanel, "Absences:", absenceCount);
        addStatRow(statsPanel, "Homework:", "2 pending"); // Would calculate from real data

        // Add components to info panel
        infoPanel.add(photoPanel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(nameLabel);
        infoPanel.add(idLabel);
        infoPanel.add(classLabel);
        infoPanel.add(teacherNameLabel);
        infoPanel.add(statsPanel);
        infoPanel.add(Box.createVerticalGlue());

        // Create details panel (right side)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(BACKGROUND_COLOR);

        // Recent activity section
        JLabel activityTitle = new JLabel("Recent Activity");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tabs for different activity types
        JTabbedPane activityTabs = new JTabbedPane();
        activityTabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Grades tab
        JPanel gradesTab = new JPanel(new BorderLayout());
        gradesTab.setBackground(Color.WHITE);

        String[] gradeColumns = {"Subject", "Grade", "Date"};
        DefaultTableModel gradesModel = new DefaultTableModel(gradeColumns, 0);
        JTable gradesTable = new JTable(gradesModel);
        gradesTable.setRowHeight(30);

        // Load recent grades - in a real app, would limit to latest few
        try {
            GradeDAO gradeDAO = new GradeDAO();
            List<Grade> grades = gradeDAO.getGradesByStudent(studentId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Grade grade : grades) {
                Object[] rowData = {
                        grade.getSubject(),
                        String.valueOf(grade.getMark()),
                        dateFormat.format(grade.getGradeDate())
                };
                gradesModel.addRow(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        gradesTab.add(new JScrollPane(gradesTable), BorderLayout.CENTER);

        // Absences tab
        JPanel absencesTab = new JPanel(new BorderLayout());
        absencesTab.setBackground(Color.WHITE);

        String[] absenceColumns = {"Date", "Status", "Description"};
        DefaultTableModel absencesModel = new DefaultTableModel(absenceColumns, 0);
        JTable absencesTable = new JTable(absencesModel);
        absencesTable.setRowHeight(30);

        // Load recent absences
        try {
            AbsenceDAO absenceDAO = new AbsenceDAO();
            List<Absence> absences = absenceDAO.getAbsencesByStudent(studentId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Absence absence : absences) {
                Object[] rowData = {
                        dateFormat.format(absence.getAbsenceDate()),
                        absence.isStatus() ? "Excused" : "Unexcused",
                        absence.getDescription()
                };
                absencesModel.addRow(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        absencesTab.add(new JScrollPane(absencesTable), BorderLayout.CENTER);

        // Homework tab - would load real data in a complete implementation
        JPanel homeworkTab = new JPanel(new BorderLayout());
        homeworkTab.setBackground(Color.WHITE);

        String[] homeworkColumns = {"Assignment", "Due Date", "Status"};
        DefaultTableModel homeworkModel = new DefaultTableModel(homeworkColumns, 0);
        JTable homeworkTable = new JTable(homeworkModel);
        homeworkTable.setRowHeight(30);

        // Add sample data
        homeworkModel.addRow(new Object[]{"Math Problems", "2023-04-15", "Completed"});
        homeworkModel.addRow(new Object[]{"Science Project", "2023-04-20", "Pending"});
        homeworkModel.addRow(new Object[]{"History Essay", "2023-04-18", "Pending"});

        homeworkTab.add(new JScrollPane(homeworkTable), BorderLayout.CENTER);

        // Add tabs to tabbed pane
        activityTabs.addTab("Grades", gradesTab);
        activityTabs.addTab("Absences", absencesTab);
        activityTabs.addTab("Homework", homeworkTab);

        // Set preferred size to make sure it expands properly
        activityTabs.setPreferredSize(new Dimension(400, 250));

        // Add components to details panel
        detailsPanel.add(activityTitle);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(activityTabs);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton printButton = new JButton("Print Report");
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printButton.setBackground(SECONDARY_COLOR);
        printButton.setForeground(Color.WHITE);
        printButton.setBorderPainted(false);
        printButton.setFocusPainted(false);
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                    "Print functionality would be implemented here",
                    "Print Report", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(Color.LIGHT_GRAY);
        closeButton.setForeground(Color.BLACK);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        // Split panel for info and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoPanel, detailsPanel);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);
        splitPane.setOpaque(false);

        // Add components to content panel
        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    /**
     * Helper method to add a stat row to the stats panel
     */
    private void addStatRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(labelComp);
        panel.add(valueComp);
    }
    /**
     * Helper method to add a label-value row to an info grid
     *
     * @param grid The grid panel
     * @param label The label text
     * @param value The value text
     */
    private void addInfoRow(JPanel grid, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(TEXT_COLOR);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(TEXT_COLOR);

        grid.add(labelComponent);
        grid.add(valueComponent);
    }
}