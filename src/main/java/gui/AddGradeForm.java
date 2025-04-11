package gui;

import dao.GradeDAO;
import dao.StudentDAO;
import model.Grade;
import model.Student;
import model.Teacher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Form for adding a new grade with input validation
 */
public class AddGradeForm extends JFrame implements ActionListener {
    private JComboBox<String> studentCombo;
    private JComboBox<String> subjectCombo;
    private JComboBox<String> markCombo;
    private JTextArea commentArea;
    private JSpinner teacherIdSpinner;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    // Store student IDs corresponding to combo box index
    private int[] studentIds;
    public AddGradeForm(){};
    /**
     * Constructor - initializes the form
     */
    public AddGradeForm(int classGroupId) {
        // Set up the frame
        setTitle("Add New Grade");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create title
        JLabel titleLabel = new JLabel("Add New Grade", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));

        // Student selection
        JLabel studentLabel = new JLabel("Student:");
        studentCombo = new JComboBox<>();
        loadStudents(classGroupId);
        formPanel.add(studentLabel);
        formPanel.add(studentCombo);

        // Subject selection
        JLabel subjectLabel = new JLabel("Subject:");
        String[] subjects = {"Mathematics", "English", "Science", "History", "Geography", "Art", "Music", "Physical Education"};
        subjectCombo = new JComboBox<>(subjects);
        formPanel.add(subjectLabel);
        formPanel.add(subjectCombo);

        // Mark selection
        JLabel markLabel = new JLabel("Grade:");
        String[] marks = {"A", "B", "C", "D", "F"};
        markCombo = new JComboBox<>(marks);
        formPanel.add(markLabel);
        formPanel.add(markCombo);

        // Teacher ID spinner
        JLabel teacherIdLabel = new JLabel("Teacher ID:");
        SpinnerNumberModel teacherModel = new SpinnerNumberModel(1, 1, 999, 1);
        teacherIdSpinner = new JSpinner(teacherModel);
        formPanel.add(teacherIdLabel);
        formPanel.add(teacherIdSpinner);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Comment area
        JLabel commentLabel = new JLabel("Comments:");
        commentArea = new JTextArea(5, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);

        JPanel commentPanel = new JPanel(new BorderLayout(5, 5));
        commentPanel.add(commentLabel, BorderLayout.NORTH);
        commentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(commentPanel, BorderLayout.CENTER);

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

    public AddGradeForm(int classGroupId, Teacher teacher) {
        // Call the existing constructor first
        this(classGroupId);

        // Now set the teacher ID and disable editing
        if (teacher != null) {
            teacherIdSpinner.setValue(teacher.getTeacherId());
            teacherIdSpinner.setEnabled(false); // Make it read-only
        }
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
        if (e.getSource() == saveButton) {
            saveGrade();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }

    /**
     * Validates and saves a new grade
     */
    private void saveGrade() {
        // Get form data
        int selectedIndex = studentCombo.getSelectedIndex();

        // Validate student selection
        if (selectedIndex < 0 || studentIds[selectedIndex] == -1) {
            statusLabel.setText("Please select a valid student");
            return;
        }

        int studentId = studentIds[selectedIndex];
        String subject = (String) subjectCombo.getSelectedItem();
        char mark = ((String) markCombo.getSelectedItem()).charAt(0);
        String comment = commentArea.getText().trim();
        int teacherId = (Integer) teacherIdSpinner.getValue();

        // Validate fields
        if (!validateInputs(comment)) {
            return;
        }

        // Create grade object with current date
        Grade grade = new Grade(mark, subject, studentId, new Date(), comment, teacherId);

        // Save to database
        try {
            GradeDAO gradeDAO = new GradeDAO();
            int gradeId = gradeDAO.addGrade(grade);
            grade.setGradeId(gradeId);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Grade added successfully for " + studentCombo.getSelectedItem(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear fields
            clearFields();
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Validates the input fields
     *
     * @param comment Comments for the grade
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs(String comment) {
        // Check that comment isn't too long (database might have a limit)
        if (comment.length() > 255) {
            statusLabel.setText("Comment is too long (maximum 255 characters)");
            commentArea.requestFocus();
            return false;
        }

        // All validations passed
        statusLabel.setText("");
        return true;
    }

    /**
     * Clears comment field and resets selections
     */
    private void clearFields() {
        commentArea.setText("");
        markCombo.setSelectedIndex(0);
        teacherIdSpinner.setValue(1);
        statusLabel.setText("");
    }
}