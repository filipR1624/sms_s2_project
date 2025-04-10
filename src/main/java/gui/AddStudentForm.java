package gui;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Form for adding a new student with input validation
 */
public class AddStudentForm extends JFrame implements ActionListener {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JSpinner classIdSpinner;
    private JSpinner parentIdSpinner;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    /**
     * Constructor - initializes the form
     */
    public AddStudentForm() {
        // Set up the frame
        setTitle("Add New Student");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create title
        JLabel titleLabel = new JLabel("Add New Student", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));

        // First name field
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField(20);
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);

        // Last name field
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField(20);
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);

        // Address field
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(20);
        formPanel.add(addressLabel);
        formPanel.add(addressField);

        // Class ID spinner
        JLabel classIdLabel = new JLabel("Class ID:");
        SpinnerNumberModel classModel = new SpinnerNumberModel(1, 1, 999, 1);
        classIdSpinner = new JSpinner(classModel);
        formPanel.add(classIdLabel);
        formPanel.add(classIdSpinner);

        // Parent ID spinner
        JLabel parentIdLabel = new JLabel("Parent ID:");
        SpinnerNumberModel parentModel = new SpinnerNumberModel(1, 1, 999, 1);
        parentIdSpinner = new JSpinner(parentModel);
        formPanel.add(parentIdLabel);
        formPanel.add(parentIdSpinner);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Create status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.add(statusLabel);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Save button
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        buttonPanel.add(saveButton);

        // Cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        // Create bottom panel for status and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveStudent();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    /**
     * Validates and saves a new student
     */
    private void saveStudent() {
        // Get form data
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        int classId = (Integer) classIdSpinner.getValue();
        int parentId = (Integer) parentIdSpinner.getValue();

        // Validate fields
        if (!validateInputs(firstName, lastName, address)) {
            return;
        }

        // Create student object
        Student student = new Student(classId, firstName, lastName, address, parentId);

        // Save to database
        try {
            StudentDAO studentDAO = new StudentDAO();
            int studentId = studentDAO.addStudentWithValidation(student);
            student.setStudentId(studentId);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Student added successfully with ID: " + studentId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear fields
            clearFields();
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            statusLabel.setText("Validation error: " + ex.getMessage());
        }
    }

    /**
     * Validates the input fields
     *
     * @param firstName First name of the student
     * @param lastName Last name of the student
     * @param address Address of the student
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs(String firstName, String lastName, String address) {
        // Check for empty fields
        if (firstName.isEmpty()) {
            statusLabel.setText("First name is required");
            firstNameField.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            statusLabel.setText("Last name is required");
            lastNameField.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            statusLabel.setText("Address is required");
            addressField.requestFocus();
            return false;
        }

        // Check name formats (letters and spaces only)
        if (!firstName.matches("[a-zA-Z\\s]+")) {
            statusLabel.setText("First name can only contain letters and spaces");
            firstNameField.requestFocus();
            return false;
        }

        if (!lastName.matches("[a-zA-Z\\s]+")) {
            statusLabel.setText("Last name can only contain letters and spaces");
            lastNameField.requestFocus();
            return false;
        }

        // All validations passed
        statusLabel.setText("");
        return true;
    }

    /**
     * Clears all input fields
     */
    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        classIdSpinner.setValue(1);
        parentIdSpinner.setValue(1);
        statusLabel.setText("");
    }
}