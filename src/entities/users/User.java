package entities.users;

import java.util.Objects;
import java.util.UUID;
/**
 * Class for User
 */
public abstract class User {
    private final String id;
    private final Role role;
    private String username;
    private String passwordHash;
    private String displayName;
    private boolean active = true;
    /**
     * Constructor for new user
     */
    protected User(Role role, String passwordHash, String displayName) {
        this(UUID.randomUUID().toString(), role, passwordHash, displayName);
    }
    /**
     * Constructor for loading user from serialized file
     */
    protected User(String id, Role role, String passwordHash, String displayName) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
    }
    /**
     * Accessor for user ID
     */
    public String getId() { return id; }
    /**
     * Accessor for user role
     */
    public Role getRole() { return role; }
    /**
     * Accessor for username
     */
    public String getUsername() { return username; }
    /**
     * Accessor for password hash
     */
    public String getPasswordHash() { return passwordHash; }
    /**
     * Accessor for user display name
     */
    public String getDisplayName() { return displayName; }
    /**
     * Accessor for user active
     */
    public boolean isActive() { return active; }
    /**
     * Mutator to change username
     */
    public void setUsername(String username) { this.username = username; }
    /**
     * Mutator to change password hash
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    /**
     * Mutator to change display name
     */
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    /**
     * Mutator to deactivate user account
     */
    public void deactivate() { this.active = false; }
}
