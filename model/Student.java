package model;

/**
 * The Student class represents a student with properties such as
 * student ID, class ID, first name, last name, address, and parent ID.
 * It provides constructors for creating new or existing students and
 * getter and setter methods for accessing and modifying the properties.
 */

public class Student {
    private int studentId;
    private int classId;
    private String firstName;
    private String lastName;
    private String address;
    private int parentId;

    /**
     * Constructor for creating new students.
     *
     * @param classId   the ID of the class the student belongs to
     * @param firstName the first name of the student
     * @param lastName  the last name of the student
     * @param address   the address of the student
     * @param parentId  the ID of the parent of the student
     */
    public Student(int classId, String firstName, String lastName,
                   String address, int parentId) {
        this.classId = classId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.parentId = parentId;
    }

    /**
     * Constructor for creating existing students.
     *
     * @param studentId the unique ID of the student
     * @param classId   the ID of the class the student belongs to
     * @param firstName the first name of the student
     * @param lastName  the last name of the student
     * @param address   the address of the student
     * @param parentId  the ID of the parent of the student
     */
    public Student(int studentId, int classId, String firstName,
                   String lastName, String address, int parentId) {
        this.studentId = studentId;
        this.classId = classId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.parentId = parentId;
    }


    /**
     * Gets the unique student ID.
     *
     * @return the student ID
     */
    public int getStudentId() { return studentId; }
    /**
     * Sets the unique student ID.
     *
     * @param studentId the new student ID
     */
    public void setStudentId(int studentId) { this.studentId = studentId; }

    /**
     * Gets the ID of the class the student belongs to.
     *
     * @return the class ID
     */
    public int getClassId() { return classId; }
    /**
     * Sets the ID of the class the student belongs to.
     *
     * @param classId the new class ID
     */
    public void setClassId(int classId) { this.classId = classId; }

    /**
     * Gets the first name of the student.
     *
     * @return the first name
     */
    public String getFirstName() { return firstName; }
    /**
     * Sets the first name of the student.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Gets the last name of the student.
     *
     * @return the last name
     */
    public String getLastName() { return lastName; }
    /**
     * Sets the last name of the student.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Gets the address of the student.
     *
     * @return the address
     */
    public String getAddress() { return address; }
    /**
     * Sets the address of the student.
     *
     * @param address the new address
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Gets the ID of the parent of the student.
     *
     * @return the parent's ID
     */
    public int getParentId() { return parentId; }
    /**
     * Sets the ID of the parent of the student.
     *
     * @param parentId the new parent's ID
     */
    public void setParentId(int parentId) { this.parentId = parentId; }

    /**
     * Returns a string representation of the student,
     * including the ID, first name, and last name.
     *
     * @return a string representation of the student
     */
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}