package model;

 /**
 * Data Transfer Object (DTO) for providing detailed information about a teacher.
 *
 * This class encapsulates a {@link Teacher} object along with the associated {@link User}
 * object, combining the teacher's details and user-specific information.
 */
public class TeacherDetailsDTO {

    private final Teacher teacher; // The teacher details
    private final User user;       // The user associated with the teacher

    /**
     * Constructs a new TeacherDetailsDTO with the specified teacher and user objects.
     *
     * @param teacher the {@link Teacher} object containing details about the teacher
     * @param user    the {@link User} object containing user-specific information for the teacher
     */
    public TeacherDetailsDTO(Teacher teacher, User user) {
        if (teacher == null || user == null) {
            throw new IllegalArgumentException("Teacher and User cannot be null");
        }
        this.teacher = teacher;
        this.user = user;
    }

    /**
     * Returns the {@link Teacher} object containing detailed information about the teacher.
     *
     * @return the {@link Teacher} object
     */
    public Teacher getTeacher() {
        return teacher;
    }

    /**
     * Returns the {@link User} object containing user-specific information for the teacher.
     *
     * @return the {@link User} object
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns a string representation of the TeacherDetailsDTO object,
     * including the details of the associated {@link Teacher} and {@link User}.
     *
     * @return a string representation of the TeacherDetailsDTO object
     */
    @Override
    public String toString() {
        return "TeacherDetailsDTO{" +
                "teacher=" + teacher +
                ", user=" + user +
                '}';
    }
}