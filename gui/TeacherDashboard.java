package gui;

import dao.ClassGroupDAO;
import dao.StudentDAO;
import dao.TeacherDAO;
import model.Student;
import model.Teacher;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

/**
 * Teacher dashboard for managing classes, students, grades, and absences.
 */
public class TeacherDashboard extends JFrame implements ActionListener {
    private User currentUser;
    private Teacher teacher;
    private int classId;

    private JTabbedPane tabbedPane;

    // Students tab components
    private JTable studentsTable;
    private DefaultTableModel studentsTableModel;
    private JButton addStudentButton;
    private JButton editStudentButton;
    private JButton deleteStudentButton;
    private JButton refreshStudentsButton;

    // Grades tab components
    private JButton addGradeButton;
    private JButton viewGradesButton;

    // Absences tab components
    private JButton addAbsenceButton;
    private JButton viewAbsencesButton;

    // Homework tab components
    private JButton addHomeworkButton;
    private JButton viewHomeworkButton;

    // Common components
    private JButton logoutButton;
    private JLabel statusLabel;

    /**
     * Constructor - initializes the teacher dashboard
     *
     * @param user The current user
     */
    public TeacherDashboard(User user) {
        this.currentUser = user;

        // Get teacher information and class ID
        TeacherDAO teacherDAO = new TeacherDAO();
        Optional<Teacher> teacherOptional = teacherDAO.getTeacherByUserId(user.getUserId());

        if (teacherOptional.isPresent()) {
            this.teacher = teacherOptional.get();
            this.classId = teacher.getClassId();
        } else {
            // Handle case where teacher not found
            JOptionPane.showMessageDialog(this,
                    "Error: Teacher profile not found for this user account.",
                    "Error", JOptionPane.ERROR_MESSAGE);

            // Use default class ID for demonstration
            this.classId = 1;
        }

        // Set up the frame
        setTitle("School Management System - Teacher Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // Class information
        JLabel classLabel = new JLabel("Class ID: " + classId);
        classLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add labels to a panel
        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.setOpaque(false);
        labelPanel.add(welcomeLabel);
        labelPanel.add(classLabel);
        headerPanel.add(labelPanel, BorderLayout.WEST);

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(logoutButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Students tab
        JPanel studentsPanel = createStudentsPanel();
        tabbedPane.addTab("Students", studentsPanel);

        // Grades tab
        JPanel gradesPanel = createGradesPanel();
        tabbedPane.addTab("Grades", gradesPanel);

        // Absences tab
        JPanel absencesPanel = createAbsencesPanel();
        tabbedPane.addTab("Absences", absencesPanel);

        // Homework tab
        JPanel homeworkPanel = createHomeworkPanel();
        tabbedPane.addTab("Homework", homeworkPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(mainPanel);

        // Load initial data
        loadStudentsData();
    }

    /**
     * Creates the students panel
     */
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

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

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Add student button
        addStudentButton = new JButton("Add Student");
        addStudentButton.addActionListener(this);
        buttonPanel.add(addStudentButton);

        // Edit student button
        editStudentButton = new JButton("Edit Student");
        editStudentButton.addActionListener(this);
        buttonPanel.add(editStudentButton);

        // Delete student button
        deleteStudentButton = new JButton("Delete Student");
        deleteStudentButton.addActionListener(this);
        buttonPanel.add(deleteStudentButton);

        // Refresh button
        refreshStudentsButton = new JButton("Refresh");
        refreshStudentsButton.addActionListener(this);
        buttonPanel.add(refreshStudentsButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the grades panel
     */
    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Placeholder content
        JLabel label = new JLabel("Manage student grades here", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Add grade button
        addGradeButton = new JButton("Add Grade");
        addGradeButton.addActionListener(this);
        buttonPanel.add(addGradeButton);

        // View grades button
        viewGradesButton = new JButton("View Grades");
        viewGradesButton.addActionListener(this);
        buttonPanel.add(viewGradesButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the absences panel
     */
    private JPanel createAbsencesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Placeholder content
        JLabel label = new JLabel("Manage student absences here", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Add absence button
        addAbsenceButton = new JButton("Add Absence");
        addAbsenceButton.addActionListener(this);
        buttonPanel.add(addAbsenceButton);

        // View absences button
        viewAbsencesButton = new JButton("View Absences");
        viewAbsencesButton.addActionListener(this);
        buttonPanel.add(viewAbsencesButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the homework panel
     */
    private JPanel createHomeworkPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Placeholder content
        JLabel label = new JLabel("Manage homework assignments here", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Add homework button
        addHomeworkButton = new JButton("Add Homework");
        addHomeworkButton.addActionListener(this);
        buttonPanel.add(addHomeworkButton);

        // View homework button
        viewHomeworkButton = new JButton("View Homework");
        viewHomeworkButton.addActionListener(this);
        buttonPanel.add(viewHomeworkButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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
                        student.getStudentId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getClassId(),
                        student.getAddress(),
                        student.getParentId()
                };
                studentsTableModel.addRow(rowData);
            }

            // Update status
            statusLabel.setText("Loaded " + students.size() + " students");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading students: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            // Log out and return to login screen
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            this.dispose();
        } else if (e.getSource() == refreshStudentsButton) {
            // Refresh students data
            loadStudentsData();
        } else if (e.getSource() == addStudentButton) {
            // Open add student form
            AddStudentForm addStudentForm = new AddStudentForm();
            addStudentForm.setVisible(true);
        } else if (e.getSource() == editStudentButton) {
            // Get selected student
            int selectedRow = studentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a student to edit",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get student data
            int studentId = (int) studentsTable.getValueAt(selectedRow, 0);

            // Open student form in edit mode (placeholder)
            JOptionPane.showMessageDialog(this,
                    "Editing student with ID: " + studentId + "\nThis feature is not yet implemented.",
                    "Edit Student", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == deleteStudentButton) {
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
        } else if (e.getSource() == addGradeButton) {
            // Open add grade form
            AddGradeForm addGradeForm = new AddGradeForm();
            addGradeForm.setVisible(true);
        } else if (e.getSource() == viewGradesButton) {
            // View grades for selected student
            int selectedRow = studentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a student to view grades",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = (int) studentsTable.getValueAt(selectedRow, 0);
            String firstName = (String) studentsTable.getValueAt(selectedRow, 1);
            String lastName = (String) studentsTable.getValueAt(selectedRow, 2);

            // Open grades view (placeholder)
            JOptionPane.showMessageDialog(this,
                    "Viewing grades for: " + firstName + " " + lastName + "\nThis feature is not yet implemented.",
                    "View Grades", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == addAbsenceButton) {
            // Open add absence form
            AbsenceForm absenceForm = new AbsenceForm();
            absenceForm.setVisible(true);
        } else if (e.getSource() == viewAbsencesButton) {
            // View absences for selected student
            int selectedRow = studentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a student to view absences",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = (int) studentsTable.getValueAt(selectedRow, 0);
            String firstName = (String) studentsTable.getValueAt(selectedRow, 1);
            String lastName = (String) studentsTable.getValueAt(selectedRow, 2);

            // Open absences form with student filter
            AbsenceForm absenceForm = new AbsenceForm();
            absenceForm.setTitle("Absences for " + firstName + " " + lastName);
            absenceForm.setVisible(true);
        } else if (e.getSource() == addHomeworkButton || e.getSource() == viewHomeworkButton) {
            // Placeholder for homework functionality
            JOptionPane.showMessageDialog(this,
                    "Homework management functionality is not yet implemented.",
                    "Feature Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}