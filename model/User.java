package model;

/**
 * Represents a user in the system, which can either be a parent or a teacher.
 * This class provides the attributes and methods to define and manage a user's details.
 */

public class User {
    /**
     * The unique identifier for the user.
     */
    private int userId;

    /**
     * The full name of the user.
     */
    private String fullName;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The password of the user. It should be stored securely.
     */
    private String password;

    /**
     * The type of account for the user (e.g., Parent or Teacher).
     */
    private AccountType accountType;

    /**
     * The physical address of the user.
     */
    private String address;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    // Enum for account types
    /**
     * Enum representing the different types of user accounts.
     * PARENT: Represents a parent user.
     * TEACHER: Represents a teacher user.
     */
    public enum AccountType {
        PARENT,
        TEACHER,
        ADMIN
    }

    // Constructor for creating new users (without ID)
    /**
     * Constructor to create a new User without a user ID.
     *
     * @param fullName The full name of the user.
     * @param email The email of the user.
     * @param password The password for the user's account.
     * @param accountType The account type for the user (Parent or Teacher).
     * @param address The address of the user.
     * @param phoneNumber The user's phone number.
     */
    public User(String fullName, String email, String password,
                AccountType accountType, String address, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Constructor for retrieving existing users (with all fields)
    /**
     * Constructor to create a User with all fields, including user ID.
     *
     * @param userId The unique identifier of the user.
     * @param fullName The full name of the user.
     * @param email The email of the user.
     * @param password The password for the user's account.
     * @param accountType The account type for the user (Parent or Teacher).
     * @param address The address of the user.
     * @param phoneNumber The user's phone number.
     */
    public User(int userId, String fullName, String email, String password,
                AccountType accountType, String address, String phoneNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    /**
     * Gets the unique identifier for the user.
     *
     * @return The unique identifier of the user.
     */
    public int getUserId() { return userId; }
    /**
     * Sets the unique identifier for the user.
     *
     * @param userId The unique identifier for the user.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Gets the full name of the user.
     *
     * @return The full name of the user.
     */
    public String getFullName() { return fullName; }
    /**
     * Sets the full name of the user.
     *
     * @param fullName The full name of the user.
     */
    public void setFullName(String fullName) { this.fullName = fullName; }

    /**
     * Gets the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() { return email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", accountType=" + accountType +
                '}';
    }
}