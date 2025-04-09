package model;

/**
 * Represents a teacher in the school management system.
 *
 * This class captures the details and relationships of a teacher including their
 * unique identifier, associated user information, and assigned class.
 */
public class Teacher {

    private int teacherId; // Unique identifier for the teacher
    private int userId;    // Identifier for the user associated with the teacher
    private int classId;   // Identifier for the class assigned to the teacher

    /**
     * Creates a new Teacher object with the specified user and class identifiers.
     *
     * @param userId  the identifier for the user associated with the teacher
     * @param classId the identifier for the class assigned to the teacher
     */
    public Teacher(int userId, int classId) {
        this.userId = userId;
        this.classId = classId;
    }

    /**
     * Creates a new Teacher object with the specified class identifier. The `userId`
     * will need to be set separately.
     *
     * @param classId the identifier for the class assigned to the teacher
     */
    public Teacher(int classId) {
        this.classId = classId;
    }

    /**
     * Creates a new Teacher object with the specified teacher, user, and class identifiers.
     *
     * @param teacherId the unique identifier for the teacher
     * @param userId    the identifier for the user associated with the teacher
     * @param classId   the identifier for the class assigned to the teacher
     */
    public Teacher(int teacherId, int userId, int classId) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.classId = classId;
    }

    /**
     * Returns the unique identifier for the teacher.
     *
     * @return the unique identifier for the teacher
     */
    public int getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the unique identifier for the teacher.
     *
     * @param teacherId the new unique identifier for the teacher
     */
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Returns the identifier for the user associated with the teacher.
     *
     * @return the identifier for the user associated with the teacher
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the identifier for the user associated with the teacher.
     *
     * @param userId the new identifier for the user associated with the teacher
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the identifier for the class assigned to the teacher.
     *
     * @return the identifier for the class assigned to the teacher
     */
    public int getClassId() {
        return classId;
    }

    /**
     * Sets the identifier for the class assigned to the teacher.
     *
     * @param classId the new identifier for the class assigned to the teacher
     */
    public void setClassId(int classId) {
        this.classId = classId;
    }

    /**
     * Returns a string representation of the Teacher object, including
     * the teacherId, userId, and classId fields.
     *
     * @return a string representation of the Teacher object
     */
    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", userId=" + userId +
                ", classId=" + classId +
                '}';
    }
}

