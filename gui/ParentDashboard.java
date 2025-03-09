package gui;

import dao.ParentDAO;
import dao.StudentDAO;
import model.Parent;
import model.Student;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

/**
 * Parent dashboard for viewing students, grades, and absences.
 */
public class ParentDashboard extends JFrame implements ActionListener {
    private User currentUser;
    private Parent parent;

    private JTabbedPane tabbedPane;
    private JTable childrenTable;
    private DefaultTableModel childrenTableModel;
    private JButton viewGradesButton;
    private JButton viewAbsencesButton;
    private JButton viewHomeworkButton;
    private JButton refreshButton;
    private JButton logoutButton;

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
        setTitle("School Management System - Parent Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Children tab
        JPanel childrenPanel = createChildrenPanel();
        tabbedPane.addTab("My Children", childrenPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);

        // Load initial data
        loadChildrenData();
    }

    /**
     * Creates the panel for viewing children
     */
    private JPanel createChildrenPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Create table model with columns
        String[] columns = {"ID", "First Name", "Last Name", "Class ID", "Address"};
        childrenTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Create table
        childrenTable = new JTable(childrenTableModel);
        childrenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(childrenTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // View grades button
        viewGradesButton = new JButton("View Grades");
        viewGradesButton.addActionListener(this);
        buttonPanel.add(viewGradesButton);

        // View absences button
        viewAbsencesButton = new JButton("View Absences");
        viewAbsencesButton.addActionListener(this);
        buttonPanel.add(viewAbsencesButton);

        // View homework button
        viewHomeworkButton = new JButton("View Homework");
        viewHomeworkButton.addActionListener(this);
        buttonPanel.add(viewHomeworkButton);

        // Refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads children data into the table
     */
    private void loadChildrenData() {
        // Clear existing data
        childrenTableModel.setRowCount(0);

        if (parent != null) {
            // Get children for the parent
            StudentDAO studentDAO = new StudentDAO();
            List<Student> children = studentDAO.getStudentsByParent(parent.getParentId());

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
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            // Log out and return to login screen
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            this.dispose();
        } else if (e.getSource() == refreshButton) {
            // Refresh data
            loadChildrenData();
        } else if (e.getSource() == viewGradesButton) {
            // Get selected child
            int selectedRow = childrenTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a child to view grades for",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = (int) childrenTable.getValueAt(selectedRow, 0);
            String firstName = (String) childrenTable.getValueAt(selectedRow, 1);
            String lastName = (String) childrenTable.getValueAt(selectedRow, 2);

            // Open grades view form (read-only)
            openGradesView(studentId, firstName + " " + lastName);
        } else if (e.getSource() == viewAbsencesButton) {
            // Get selected child
            int selectedRow = childrenTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a child to view absences for",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = (int) childrenTable.getValueAt(selectedRow, 0);
            String firstName = (String) childrenTable.getValueAt(selectedRow, 1);
            String lastName = (String) childrenTable.getValueAt(selectedRow, 2);

            // Open absences view form (read-only with ability to add excuses)
            openAbsencesView(studentId, firstName + " " + lastName);
        } else if (e.getSource() == viewHomeworkButton) {
            // Get selected child
            int selectedRow = childrenTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a child to view homework for",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = (int) childrenTable.getValueAt(selectedRow, 0);
            int classId = (int) childrenTable.getValueAt(selectedRow, 3);
            String firstName = (String) childrenTable.getValueAt(selectedRow, 1);
            String lastName = (String) childrenTable.getValueAt(selectedRow, 2);

            // Open homework view form (read-only)
            openHomeworkView(classId, firstName + " " + lastName);
        }
    }

    /**
     * Opens the grades view for a student
     */
    private void openGradesView(int studentId, String studentName) {
        // In a complete implementation, this would open a GradesViewForm
        // Since we don't have that yet, show a dialog as a placeholder
        JOptionPane.showMessageDialog(this,
                "Viewing grades for " + studentName + " (ID: " + studentId + ")",
                "View Grades", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Opens the absences view for a student
     */
    private void openAbsencesView(int studentId, String studentName) {
        // In a complete implementation, this would open an AbsencesViewForm
        // For now, just show the absences form filtered to this student
        AbsenceForm absenceForm = new AbsenceForm();
        absenceForm.setTitle("Absences for " + studentName);
        absenceForm.setVisible(true);
    }

    /**
     * Opens the homework view for a class
     */
    private void openHomeworkView(int classId, String studentName) {
        // In a complete implementation, this would open a HomeworkViewForm
        // Since we don't have that yet, show a dialog as a placeholder
        JOptionPane.showMessageDialog(this,
                "Viewing homework for " + studentName + " (Class ID: " + classId + ")",
                "View Homework", JOptionPane.INFORMATION_MESSAGE);
    }
}