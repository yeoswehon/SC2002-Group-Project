package security;
/**
 * Interface for PasswordHasher
 */
public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hashed);
}