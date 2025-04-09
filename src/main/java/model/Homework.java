package model;

import java.util.Date;

/**
 * Represents a homework assignment in the school management system.
 */
public class Homework {
    private int homeworkId;
    private Date assignmentDate;
    private Date dueDate;
    private int classId;
    private String description;
    private boolean status; // true = completed, false = not completed

    /**
     * Constructor for creating a new homework record.
     *
     * @param assignmentDate The date the homework was assigned
     * @param dueDate The due date for the homework
     * @param classId The ID of the class the homework is for
     * @param description A description of the homework
     * @param status The status of the homework (true = completed, false = not completed)
     */
    public Homework(Date assignmentDate, Date dueDate, int classId, String description, boolean status) {
        this.assignmentDate = assignmentDate;
        this.dueDate = dueDate;
        this.classId = classId;
        this.description = description;
        this.status = status;
    }

    /**
     * Constructor for existing homework records.
     *
     * @param homeworkId The unique ID of the homework
     * @param assignmentDate The date the homework was assigned
     * @param dueDate The due date for the homework
     * @param classId The ID of the class the homework is for
     * @param description A description of the homework
     * @param status The status of the homework (true = completed, false = not completed)
     */
    public Homework(int homeworkId, Date assignmentDate, Date dueDate, int classId, String description, boolean status) {
        this.homeworkId = homeworkId;
        this.assignmentDate = assignmentDate;
        this.dueDate = dueDate;
        this.classId = classId;
        this.description = description;
        this.status = status;
    }

    /**
     * Gets the homework ID.
     *
     * @return The homework's unique ID
     */
    public int getHomeworkId() {
        return homeworkId;
    }

    /**
     * Sets the homework ID.
     *
     * @param homeworkId The new homework ID
     */
    public void setHomeworkId(int homeworkId) {
        this.homeworkId = homeworkId;
    }

    /**
     * Gets the assignment date.
     *
     * @return The assignment date
     */
    public Date getAssignmentDate() {
        return assignmentDate;
    }

    /**
     * Sets the assignment date.
     *
     * @param assignmentDate The new assignment date
     */
    public void setAssignmentDate(Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    /**
     * Gets the due date.
     *
     * @return The due date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate The new due date
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the class ID.
     *
     * @return The class ID
     */
    public int getClassId() {
        return classId;
    }

    /**
     * Sets the class ID.
     *
     * @param classId The new class ID
     */
    public void setClassId(int classId) {
        this.classId = classId;
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
     * @return The status (true = completed, false = not completed)
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status The new status (true = completed, false = not completed)
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Checks if this homework is completed.
     *
     * @return true if the homework is completed, false otherwise
     */
    public boolean isCompleted() {
        return status;
    }

    /**
     * Checks if this homework is overdue.
     *
     * @return true if the due date has passed and the homework is not completed, false otherwise
     */
    public boolean isOverdue() {
        return !status && dueDate.before(new Date());
    }

    @Override
    public String toString() {
        return "Homework{" +
                "homeworkId=" + homeworkId +
                ", assignmentDate=" + assignmentDate +
                ", dueDate=" + dueDate +
                ", classId=" + classId +
                ", description='" + description + '\'' +
                ", status=" + (status ? "completed" : "not completed") +
                '}';
    }
}