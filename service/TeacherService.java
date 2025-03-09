package service;

import dao.TeacherDAO;
import dao.UserDAO;
import model.Teacher;
import model.User;
import util.DatabaseConnection;

public class TeacherService {
    public static int createNewTeacher(User user, int classId) throws Exception {
        UserDAO userDAO = new UserDAO();
        TeacherDAO teacherDAO = new TeacherDAO();

        try {
            DatabaseConnection.beginTransaction();

            // 1. Validate class exists first
            if (!teacherDAO.classExists(classId)) {
                throw new IllegalArgumentException("Class ID " + classId + " does not exist");
            }

            // 2. Create user
            int userId = userDAO.addUser(user);

            // 3. Create teacher profile
            Teacher teacher = new Teacher(userId, classId);
            int teacherId = teacherDAO.addTeacher(teacher);

            DatabaseConnection.commitTransaction();
            return teacherId;
        } catch (Exception e) {
            DatabaseConnection.rollbackTransaction();
            throw new Exception("Teacher creation failed: " + e.getMessage());
        }
    }
}