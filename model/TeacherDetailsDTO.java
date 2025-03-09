package model;

public class TeacherDetailsDTO {
    private final Teacher teacher;
    private final User user;

    public TeacherDetailsDTO(Teacher teacher, User user) {
        this.teacher = teacher;
        this.user = user;
    }

    public Teacher getTeacher() { return teacher; }
    public User getUser() { return user; }

    @Override
    public String toString() {
        return String.format(
                "Teacher ID: %d\nName: %s\nEmail: %s\nClass: %d",
                teacher.getTeacherId(),
                user.getFullName(),
                user.getEmail(),
                teacher.getClassId()
        );
    }
}