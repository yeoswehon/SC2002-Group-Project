package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Class for PasswordHasher
 */
public final class Sha256PasswordHasher implements PasswordHasher {
    /**
     * Hash password
     */
    @Override public String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Check if hashed password matches hash
     */
    @Override public boolean matches(String raw, String hashed) {
        return hash(raw).equals(hashed);
    }
}