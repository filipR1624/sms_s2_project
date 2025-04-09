package model;

import java.util.Date;

/**
 * Represents a student absence in the school management system.
 */
public class Absence {
    private int absenceId;
    private int studentId;
    private Date absenceDate;
    private String description;
    private boolean status; // true = excused, false = unexcused

    /**
     * Constructor for creating a new absence record.
     *
     * @param studentId The ID of the student who was absent
     * @param absenceDate The date of the absence
     * @param description A description of the absence
     * @param status The status of the absence (true = excused, false = unexcused)
     */
    public Absence(int studentId, Date absenceDate, String description, boolean status) {
        this.studentId = studentId;
        this.absenceDate = absenceDate;
        this.description = description;
        this.status = status;
    }

    /**
     * Constructor for existing absence records.
     *
     * @param absenceId The unique ID of the absence
     * @param studentId The ID of the student who was absent
     * @param absenceDate The date of the absence
     * @param description A description of the absence
     * @param status The status of the absence (true = excused, false = unexcused)
     */
    public Absence(int absenceId, int studentId, Date absenceDate, String description, boolean status) {
        this.absenceId = absenceId;
        this.studentId = studentId;
        this.absenceDate = absenceDate;
        this.description = description;
        this.status = status;
    }

    /**
     * Gets the absence ID.
     *
     * @return The absence's unique ID
     */
    public int getAbsenceId() {
        return absenceId;
    }

    /**
     * Sets the absence ID.
     *
     * @param absenceId The new absence ID
     */
    public void setAbsenceId(int absenceId) {
        this.absenceId = absenceId;
    }

    /**
     * Gets the student ID.
     *
     * @return The student ID
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     *
     * @param studentId The new student ID
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * Gets the absence date.
     *
     * @return The absence date
     */
    public Date getAbsenceDate() {
        return absenceDate;
    }

    /**
     * Sets the absence date.
     *
     * @param absenceDate The new absence date
     */
    public void setAbsenceDate(Date absenceDate) {
        this.absenceDate = absenceDate;
    }

    /**
     * Gets the description.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description The new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the status.
     *
     * @return The status (true = excused, false = unexcused)
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status The new status (true = excused, false = unexcused)
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Checks if this absence is excused.
     *
     * @return true if the absence is excused, false otherwise
     */
    public boolean isExcused() {
        return status;
    }

    @Override
    public String toString() {
        return "Absence{" +
                "absenceId=" + absenceId +
                ", studentId=" + studentId +
                ", absenceDate=" + absenceDate +
                ", description='" + description + '\'' +
                ", status=" + (status ? "excused" : "unexcused") +
                '}';
    }
}