package gui;

import dao.StudentDAO;
import model.Student;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditStudentForm extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JSpinner parentIdSpinner;
    private Student student;

    public EditStudentForm(Student student) {
        this.student = student;
        initializeUI();
        loadStudentData();
    }

    private void initializeUI() {
        setTitle("Edit Student");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("Address:"));
        addressField = new JTextField();
        panel.add(addressField);

        panel.add(new JLabel("Parent ID:"));
        parentIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        panel.add(parentIdSpinner);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveStudent());
        panel.add(saveButton);

        add(panel);
    }

    private void loadStudentData() {
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        addressField.setText(student.getAddress());
        parentIdSpinner.setValue(student.getParentId());
    }

    private void saveStudent() {
        // Update student object
        student.setFirstName(firstNameField.getText());
        student.setLastName(lastNameField.getText());
        student.setAddress(addressField.getText());
        student.setParentId((Integer) parentIdSpinner.getValue());

        // Update in database
        StudentDAO studentDAO = new StudentDAO();
        studentDAO.updateStudent(student);

        JOptionPane.showMessageDialog(this, "Student updated successfully!");
        dispose();
    }
}