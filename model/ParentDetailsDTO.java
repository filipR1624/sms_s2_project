package model;

/**
 * Data Transfer Object (DTO) for parent details including user information.
 * Used to transfer parent and user data together.
 */
public class ParentDetailsDTO {
    private final Parent parent;
    private final User user;

    /**
     * Constructor for ParentDetailsDTO.
     *
     * @param parent The parent object
     * @param user The user object associated with the parent
     */
    public ParentDetailsDTO(Parent parent, User user) {
        this.parent = parent;
        this.user = user;
    }

    /**
     * Gets the parent object.
     *
     * @return The parent
     */
    public Parent getParent() {
        return parent;
    }

    /**
     * Gets the user object.
     *
     * @return The user
     */
    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return String.format(
                "Parent ID: %d\nName: %s\nEmail: %s\nNumber of Children: %d",
                parent.getParentId(),
                user.getFullName(),
                user.getEmail(),
                parent.getNumberOfChildren()
        );
    }
}