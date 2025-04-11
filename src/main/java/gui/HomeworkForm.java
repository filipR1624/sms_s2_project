package gui;

import dao.HomeworkDAO;
import model.Homework;
import model.Teacher; // Import for Teacher class
import model.User; // Added import for User class

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Form for adding or editing homework assignments
 */
public class HomeworkForm extends JFrame implements ActionListener {
    // UI colors for consistent look with main dashboard
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    private int classId;
    private Teacher currentTeacher; // Field for currently logged-in teacher
    private User currentUser; // Added field for the User object associated with the teacher
    private Homework homework; // Null for new homework, non-null for editing
    private boolean isEditMode;

    private JTextField assignmentDateField;
    private JTextField dueDateField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    /**
     * Constructor for creating a new homework assignment
     *
     * @param classId The class ID for the homework
     * @param currentTeacher The currently logged-in teacher
     * @param currentUser The User object associated with the teacher
     */
    public HomeworkForm(int classId, Teacher currentTeacher, User currentUser) {
        this.classId = classId;
        this.currentTeacher = currentTeacher;
        this.currentUser = currentUser;
        this.isEditMode = false;

        initializeUI("Add New Homework Assignment");
    }

    /**
     * Constructor for editing an existing homework assignment
     *
     * @param classId The class ID
     * @param homework The homework to edit
     * @param currentTeacher The currently logged-in teacher
     * @param currentUser The User object associated with the teacher
     */
    public HomeworkForm(int classId, Homework homework, Teacher currentTeacher, User currentUser) {
        this.classId = classId;
        this.homework = homework;
        this.currentTeacher = currentTeacher;
        this.currentUser = currentUser;
        this.isEditMode = true;

        initializeUI("Edit Homework Assignment");
    }

    // Add this constructor to HomeworkForm.java to maintain backward compatibility
    /**
     * Constructor for creating a new homework assignment (backward compatibility)
     *
     * @param classId The class ID for the homework
     */
    public HomeworkForm(int classId) {
        this.classId = classId;
        this.currentTeacher = null; // Will need to be set manually if needed
        this.currentUser = null;    // Will need to be set manually if needed
        this.isEditMode = false;

        initializeUI("Add New Homework Assignment");
    }

    /**
     * Constructor for editing an existing homework assignment (backward compatibility)
     *
     * @param classId The class ID
     * @param homework The homework to edit
     */
    public HomeworkForm(int classId, Homework homework) {
        this.classId = classId;
        this.homework = homework;
        this.currentTeacher = null; // Will need to be set manually if needed
        this.currentUser = null;    // Will need to be set manually if needed
        this.isEditMode = true;

        initializeUI("Edit Homework Assignment");
    }

    /**
     * Initializes the UI components
     *
     * @param title The title for the form
     */
    private void initializeUI(String title) {
        // Set up the frame
        setTitle(title);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setBackground(BACKGROUND_COLOR);

        // Create main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        formPanel.setBackground(BACKGROUND_COLOR);

        // Display teacher name (read-only)
        JLabel teacherLabel = new JLabel("Teacher:");
        teacherLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        teacherLabel.setForeground(TEXT_COLOR);

        JLabel teacherNameLabel = new JLabel(currentUser.getFullName());
        teacherNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        teacherNameLabel.setForeground(TEXT_COLOR);

        formPanel.add(teacherLabel);
        formPanel.add(teacherNameLabel);

        // Assignment date field
        JLabel assignmentDateLabel = new JLabel("Assignment Date (yyyy-MM-dd):");
        assignmentDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        assignmentDateLabel.setForeground(TEXT_COLOR);

        assignmentDateField = new JTextField(10);
        assignmentDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        assignmentDateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // Set default assignment date to today
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        assignmentDateField.setText(dateFormat.format(new Date()));

        formPanel.add(assignmentDateLabel);
        formPanel.add(assignmentDateField);

        // Due date field
        JLabel dueDateLabel = new JLabel("Due Date (yyyy-MM-dd):");
        dueDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dueDateLabel.setForeground(TEXT_COLOR);

        dueDateField = new JTextField(10);
        dueDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dueDateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // Set default due date to one week from today
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        dueDateField.setText(dateFormat.format(calendar.getTime()));

        formPanel.add(dueDateLabel);
        formPanel.add(dueDateField);

        // Status checkbox
        JLabel completedLabel = new JLabel("Completed:");
        completedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        completedLabel.setForeground(TEXT_COLOR);

        completedCheckBox = new JCheckBox();
        completedCheckBox.setBackground(BACKGROUND_COLOR);

        formPanel.add(completedLabel);
        formPanel.add(completedCheckBox);

        // Description label and text area
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descriptionLabel.setForeground(TEXT_COLOR);

        // Add description label to form panel, but handle text area separately
        formPanel.add(descriptionLabel);
        formPanel.add(new JLabel()); // Empty placeholder

        // Create description text area with scroll pane (spanning both columns)
        descriptionArea = new JTextArea(10, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Status label for validation errors
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(ACCENT_COLOR);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // Save button
        saveButton = new JButton("Save");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(this);

        // Cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(this);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Assemble the layout
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // If editing existing homework, load the data
        if (isEditMode && homework != null) {
            loadHomeworkData();
        }
    }

    /**
     * Loads the homework data into the form fields
     */
    private void loadHomeworkData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        assignmentDateField.setText(dateFormat.format(homework.getAssignmentDate()));
        dueDateField.setText(dateFormat.format(homework.getDueDate()));
        descriptionArea.setText(homework.getDescription());
        completedCheckBox.setSelected(homework.isStatus());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            try {
                saveHomework();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    /**
     * Validates and saves the homework assignment
     */
    private void saveHomework() throws SQLException {
        // Get form data
        String assignmentDateStr = assignmentDateField.getText().trim();
        String dueDateStr = dueDateField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();

        // Validate input
        if (!validateInput(assignmentDateStr, dueDateStr, description)) {
            return;
        }

        try {
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date assignmentDate = dateFormat.parse(assignmentDateStr);
            Date dueDate = dateFormat.parse(dueDateStr);

            // Create or update homework
            HomeworkDAO homeworkDAO = new HomeworkDAO();

            if (isEditMode && homework != null) {
                // Update existing homework
                homework.setAssignmentDate(assignmentDate);
                homework.setDueDate(dueDate);
                homework.setDescription(description);
                homework.setStatus(completed);
                // Set teacher ID if your Homework model has a teacherId field
                // homework.setTeacherId(currentTeacher.getId());

                boolean updated = homeworkDAO.updateHomework(homework);

                if (updated) {
                    JOptionPane.showMessageDialog(this,
                            "Homework assignment updated successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    statusLabel.setText("Failed to update homework assignment");
                }
            } else {
                // Create new homework with current teacher
                Homework newHomework = new Homework(assignmentDate, dueDate, classId, description, completed);
                // Set teacher ID if your Homework model has a teacherId field
                // newHomework.setTeacherId(currentTeacher.getId());

                int homeworkId = homeworkDAO.addHomework(newHomework);

                if (homeworkId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Homework assignment created successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    statusLabel.setText("Failed to create homework assignment");
                }
            }
        } catch (ParseException e) {
            statusLabel.setText("Invalid date format. Please use YYYY-MM-DD");
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates the form input
     *
     * @param assignmentDateStr The assignment date string
     * @param dueDateStr The due date string
     * @param description The homework description
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput(String assignmentDateStr, String dueDateStr, String description) {
        // Check for empty fields
        if (assignmentDateStr.isEmpty()) {
            statusLabel.setText("Assignment date is required");
            assignmentDateField.requestFocus();
            return false;
        }

        if (dueDateStr.isEmpty()) {
            statusLabel.setText("Due date is required");
            dueDateField.requestFocus();
            return false;
        }

        if (description.isEmpty()) {
            statusLabel.setText("Description is required");
            descriptionArea.requestFocus();
            return false;
        }

        // Validate date formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            Date assignmentDate = dateFormat.parse(assignmentDateStr);
        } catch (ParseException e) {
            statusLabel.setText("Invalid assignment date format. Please use YYYY-MM-DD");
            assignmentDateField.requestFocus();
            return false;
        }

        try {
            Date dueDate = dateFormat.parse(dueDateStr);
        } catch (ParseException e) {
            statusLabel.setText("Invalid due date format. Please use YYYY-MM-DD");
            dueDateField.requestFocus();
            return false;
        }

        // Validate date logic (due date should be after or equal to assignment date)
        try {
            Date assignmentDate = dateFormat.parse(assignmentDateStr);
            Date dueDate = dateFormat.parse(dueDateStr);

            if (dueDate.before(assignmentDate)) {
                statusLabel.setText("Due date cannot be before assignment date");
                dueDateField.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            // Already validated format above, this shouldn't happen
            e.printStackTrace();
        }

        // Validate description length
        if (description.length() > 255) {
            statusLabel.setText("Description is too long (maximum 255 characters)");
            descriptionArea.requestFocus();
            return false;
        }

        // All validations passed
        statusLabel.setText("");
        return true;
    }
}