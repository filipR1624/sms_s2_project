package model;

/**
 * Represents a parent in the school management system.
 */
public class Parent {
    private int parentId;
    private int userId;
    private int numberOfChildren;

    /**
     * Constructor for creating a new parent record.
     *
     * @param userId The ID of the associated user
     * @param numberOfChildren The number of children this parent has
     */
    public Parent(int userId, int numberOfChildren) {
        this.userId = userId;
        this.numberOfChildren = numberOfChildren;
    }

    /**
     * Constructor for existing parent records.
     *
     * @param parentId The unique ID of the parent
     * @param userId The ID of the associated user
     * @param numberOfChildren The number of children this parent has
     */
    public Parent(int parentId, int userId, int numberOfChildren) {
        this.parentId = parentId;
        this.userId = userId;
        this.numberOfChildren = numberOfChildren;
    }

    /**
     * Gets the parent ID.
     *
     * @return The parent's unique ID
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * Sets the parent ID.
     *
     * @param parentId The new parent ID
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the user ID associated with this parent.
     *
     * @return The user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with this parent.
     *
     * @param userId The new user ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the number of children this parent has.
     *
     * @return The number of children
     */
    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    /**
     * Sets the number of children this parent has.
     *
     * @param numberOfChildren The new number of children
     */
    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "parentId=" + parentId +
                ", userId=" + userId +
                ", numberOfChildren=" + numberOfChildren +
                '}';
    }
}