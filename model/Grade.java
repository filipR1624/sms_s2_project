package model;

import java.util.Date;

/**
 * Represents a grade in the school management system.
 */
public class Grade {
    private int gradeId;
    private char mark;
    private String subject;
    private int studentId;
    private Date gradeDate;
    private String comment;
    private int teacherId;

    /**
     * Constructor for creating a new grade record.
     *
     * @param mark The letter grade (A-F)
     * @param subject The subject for which the grade was given
     * @param studentId The ID of the student who received the grade
     * @param gradeDate The date when the grade was recorded
     * @param comment Additional comments about the grade
     * @param teacherId The ID of the teacher who gave the grade
     */
    public Grade(char mark, String subject, int studentId, Date gradeDate, String comment, int teacherId) {
        this.mark = mark;
        this.subject = subject;
        this.studentId = studentId;
        this.gradeDate = gradeDate;
        this.comment = comment;
        this.teacherId = teacherId;
    }

    /**
     * Constructor for existing grade records.
     *
     * @param gradeId The unique ID of the grade
     * @param mark The letter grade (A-F)
     * @param subject The subject for which the grade was given
     * @param studentId The ID of the student who received the grade
     * @param gradeDate The date when the grade was recorded
     * @param comment Additional comments about the grade
     * @param teacherId The ID of the teacher who gave the grade
     */
    public Grade(int gradeId, char mark, String subject, int studentId, Date gradeDate, String comment, int teacherId) {
        this.gradeId = gradeId;
        this.mark = mark;
        this.subject = subject;
        this.studentId = studentId;
        this.gradeDate = gradeDate;
        this.comment = comment;
        this.teacherId = teacherId;
    }

    /**
     * Gets the grade ID.
     *
     * @return The grade's unique ID
     */
    public int getGradeId() {
        return gradeId;
    }

    /**
     * Sets the grade ID.
     *
     * @param gradeId The new grade ID
     */
    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    /**
     * Gets the mark.
     *
     * @return The letter grade
     */
    public char getMark() {
        return mark;
    }

    /**
     * Sets the mark.
     *
     * @param mark The new letter grade
     */
    public void setMark(char mark) {
        this.mark = mark;
    }

    /**
     * Gets the subject.
     *
     * @return The subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject The new subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
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
     * Gets the grade date.
     *
     * @return The grade date
     */
    public Date getGradeDate() {
        return gradeDate;
    }

    /**
     * Sets the grade date.
     *
     * @param gradeDate The new grade date
     */
    public void setGradeDate(Date gradeDate) {
        this.gradeDate = gradeDate;
    }

    /**
     * Gets the comment.
     *
     * @return The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment.
     *
     * @param comment The new comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the teacher ID.
     *
     * @return The teacher ID
     */
    public int getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the teacher ID.
     *
     * @param teacherId The new teacher ID
     */
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", mark=" + mark +
                ", subject='" + subject + '\'' +
                ", studentId=" + studentId +
                ", gradeDate=" + gradeDate +
                ", comment='" + comment + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}