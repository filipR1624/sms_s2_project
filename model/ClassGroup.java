package model;

/**
 * Represents a class group in the school management system.
 */
public class ClassGroup {
    private int classId;
    private int size;
    private int year;
    private int roomNumber;
    private int teacherId;

    /**
     * Constructor for creating a new class record.
     *
     * @param size The maximum size of the class
     * @param year The academic year
     * @param roomNumber The room number assigned to the class
     */
    public ClassGroup(int size, int year, int roomNumber) {
        this.size = size;
        this.year = year;
        this.roomNumber = roomNumber;
        this.teacherId = 0; // Default value indicating no teacher assigned
    }

    /**
     * Constructor for existing class records.
     *
     * @param classId The unique ID of the class
     * @param size The maximum size of the class
     * @param year The academic year
     * @param roomNumber The room number assigned to the class
     */
    public ClassGroup(int classId, int size, int year, int roomNumber) {
        this.classId = classId;
        this.size = size;
        this.year = year;
        this.roomNumber = roomNumber;
        this.teacherId = 0; // Default value indicating no teacher assigned
    }

    /**
     * Constructor for existing class records with a teacher.
     *
     * @param classId The unique ID of the class
     * @param size The maximum size of the class
     * @param year The academic year
     * @param roomNumber The room number assigned to the class
     * @param teacherId The ID of the teacher assigned to this class
     */
    public ClassGroup(int classId, int size, int year, int roomNumber, int teacherId) {
        this.classId = classId;
        this.size = size;
        this.year = year;
        this.roomNumber = roomNumber;
        this.teacherId = teacherId;
    }

    /**
     * Gets the class ID.
     *
     * @return The class's unique ID
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
     * Gets the maximum size of the class.
     *
     * @return The class size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the maximum size of the class.
     *
     * @param size The new class size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gets the academic year.
     *
     * @return The year
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the academic year.
     *
     * @param year The new year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Gets the room number.
     *
     * @return The room number
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets the room number.
     *
     * @param roomNumber The new room number
     */
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
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

    /**
     * Checks if this class has a teacher assigned.
     *
     * @return true if a teacher is assigned, false otherwise
     */
    public boolean hasTeacher() {
        return teacherId > 0;
    }

    @Override
    public String toString() {
        return "ClassGroup{" +
                "classId=" + classId +
                ", size=" + size +
                ", year=" + year +
                ", roomNumber=" + roomNumber +
                ", teacherId=" + teacherId +
                '}';
    }
}