package git.dimitrikvirik.springsoft.user.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecretBasedRSAKeyGenerator {
    private static final int KEY_SIZE = 2048;
    private static final int ITERATIONS = 10000;
    private static final int SALT_SIZE = 32;

    public static KeyPair generateKeyPair(String secret) throws Exception {
        // Generate a salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);

        // Derive a seed from the secret
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        byte[] seed = factory.generateSecret(spec).getEncoded();

        // Use the seed to initialize a SecureRandom
        SecureRandom seededRandom = new SecureRandom(seed);

        // Generate the RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(KEY_SIZE, seededRandom);
        return keyGen.generateKeyPair();
    }

    // Utility methods to convert keys to/from Base64 strings
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

}