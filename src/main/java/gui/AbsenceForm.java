package gui;

import dao.AbsenceDAO;
import dao.StudentDAO;
import model.Absence;
import model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Form for managing student absences with input validation
 */
public class AbsenceForm extends JFrame implements ActionListener {
    private JComboBox<String> studentCombo;
    private JTextField dateField;
    private JTextField descriptionField;
    private JCheckBox excusedCheckBox;
    private JButton addButton;
    private JButton closeButton;
    private JLabel statusLabel;

    // Store student IDs corresponding to combo box index
    private int[] studentIds;

    /**
     * Constructor - initializes the form
     */
    public AbsenceForm(int classGroupId) {
        // Set up the frame
        setTitle("Manage Student Absences");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create title
        JLabel titleLabel = new JLabel("Student Absence Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));

        // Student selection
        JLabel studentLabel = new JLabel("Student:");
        studentCombo = new JComboBox<>();
        loadStudents(classGroupId);
        studentCombo.addActionListener(this);
        formPanel.add(studentLabel);
        formPanel.add(studentCombo);

        // Date field
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateField = new JTextField();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(sdf.format(new Date())); // Set today's date as default
        formPanel.add(dateLabel);
        formPanel.add(dateField);

        // Description field
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField();
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);

        // Excused checkbox
        JLabel excusedLabel = new JLabel("Excused:");
        excusedCheckBox = new JCheckBox();
        formPanel.add(excusedLabel);
        formPanel.add(excusedCheckBox);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Add button
        addButton = new JButton("Add Absence");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        // Close button
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);

        // Create top panel with form and buttons
        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(statusLabel, BorderLayout.SOUTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create a panel for status and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Add main panel to frame
        add(mainPanel);
    }

    /**
     * Loads student data into the combo box
     */
    private void loadStudents(int classGroupId) {
        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students = studentDAO.getStudentsByClass(classGroupId);

            if (students.isEmpty()) {
                studentCombo.addItem("No students available");
                studentIds = new int[1];
                studentIds[0] = -1;
            } else {
                studentIds = new int[students.size()];
                for (int i = 0; i < students.size(); i++) {
                    Student student = students.get(i);
                    studentCombo.addItem(student.getFirstName() + " " + student.getLastName());
                    studentIds[i] = student.getStudentId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            studentCombo.addItem("Error loading students");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addAbsence();
        } else if (e.getSource() == closeButton) {
            dispose();
        }
    }

    /**
     * Validates and adds a new absence record
     */
    private void addAbsence() {
        // Get form data
        int selectedIndex = studentCombo.getSelectedIndex();

        // Validate student selection
        if (selectedIndex < 0 || studentIds[selectedIndex] == -1) {
            statusLabel.setText("Please select a valid student");
            return;
        }

        int studentId = studentIds[selectedIndex];
        String dateString = dateField.getText().trim();
        String description = descriptionField.getText().trim();
        boolean excused = excusedCheckBox.isSelected();

        // Validate inputs
        if (!validateInputs(dateString, description)) {
            return;
        }

        try {
            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date absenceDate = sdf.parse(dateString);

            // Create absence object
            Absence absence = new Absence(studentId, absenceDate, description, excused);

            // Save to database
            AbsenceDAO absenceDAO = new AbsenceDAO();
            int absenceId = absenceDAO.addAbsence(absence);
            absence.setAbsenceId(absenceId);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Absence recorded successfully",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (ParseException e) {
            statusLabel.setText("Invalid date format. Please use YYYY-MM-DD");
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates the input fields
     *
     * @param dateString Date string to validate
     * @param description Description to validate
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs(String dateString, String description) {
        // Check for empty fields
        if (dateString.isEmpty()) {
            statusLabel.setText("Date is required");
            dateField.requestFocus();
            return false;
        }

        if (description.isEmpty()) {
            statusLabel.setText("Description is required");
            descriptionField.requestFocus();
            return false;
        }

        // Validate date format
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateString);
        } catch (ParseException e) {
            statusLabel.setText("Invalid date format. Please use YYYY-MM-DD");
            dateField.requestFocus();
            return false;
        }

        // Check description length
        if (description.length() > 255) {
            statusLabel.setText("Description is too long (maximum 255 characters)");
            descriptionField.requestFocus();
            return false;
        }

        // All validations passed
        statusLabel.setText("");
        return true;
    }

}