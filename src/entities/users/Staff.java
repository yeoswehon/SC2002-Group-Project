package entities.users;

import java.util.Objects;
/**
 * Class for Staff
 */
public final class Staff extends User {
    /**
     * Constructor for new staff
     */
    public Staff(String username, String passwordHash, String displayName) {
        super(Role.STAFF, passwordHash, displayName);
        setUsername(Objects.requireNonNull(username, "username must not be null"));
    }
    /**
     * Constructor for loading staff from serialized file
     */
    public Staff(String id, String username, String passwordHash, String displayName) {
        super(id, Role.STAFF, passwordHash, displayName);
        setUsername(Objects.requireNonNull(username, "username must not be null"));
    }
}
