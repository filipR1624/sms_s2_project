package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

/**
 * Utility class for secure password hashing and verification.
 * Uses PBKDF2 (Password-Based Key Derivation Function 2) with HMAC-SHA1.
 */
public class PasswordHasher {
    // Algorithm parameters
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes

    /**
     * Hashes a plaintext password using PBKDF2 with a random salt.
     *
     * @param password The plaintext password to hash
     * @return A formatted, Base64-encoded string containing iterations, salt, and hash
     * @throws NoSuchAlgorithmException If the algorithm is not available
     * @throws InvalidKeySpecException If the key specification is invalid
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        // Format: iterations:salt:hash
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a plaintext password against a stored hash.
     *
     * @param password The plaintext password to verify
     * @param hashedPassword The stored hash to verify against
     * @return true if the password matches the hash, false otherwise
     * @throws NoSuchAlgorithmException If the algorithm is not available
     * @throws InvalidKeySpecException If the key specification is invalid
     */
    public static boolean verifyPassword(String password, String hashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Check if the stored password is already in the hashed format
        if (!hashedPassword.contains(":")) {
            // This is a plaintext password (backward compatibility)
            return password.equals(hashedPassword);
        }

        // Extract parts from stored hash
        String[] parts = hashedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] storedHash = Base64.getDecoder().decode(parts[2]);

        // Compute hash of the provided password
        byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations, storedHash.length * 8);

        // Compare hashes
        int diff = storedHash.length ^ testHash.length;
        for (int i = 0; i < storedHash.length && i < testHash.length; i++) {
            diff |= storedHash[i] ^ testHash[i];
        }

        return diff == 0;
    }

    /**
     * Implements PBKDF2 password-based key derivation function.
     *
     * @param password The password to hash
     * @param salt The salt to use
     * @param iterations The number of iterations to perform
     * @param keyLength The desired key length in bits
     * @return The derived key
     * @throws NoSuchAlgorithmException If the algorithm is not available
     * @throws InvalidKeySpecException If the key specification is invalid
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return skf.generateSecret(spec).getEncoded();
    }
}