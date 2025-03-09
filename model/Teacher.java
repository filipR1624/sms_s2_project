package model;

public class Teacher {
    private int teacherId;
    private int userId;
    private int classId;

    // Constructors
    public Teacher(int userId, int classId) {
        this.userId = userId;
        this.classId = classId;
    }
    public Teacher(int classId) {
        this.userId = userId;
        this.classId = classId;
    }
    public Teacher(int teacherId, int userId, int classId) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.classId = classId;
    }

    // Getters and Setters
    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", userId=" + userId +
                ", classId=" + classId +
                '}';
    }
}