package git.dimitrikvirik.springsoft.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecretBasedRSAKeyGenerator {
    private static final int KEY_SIZE = 2048;
    private static final int ITERATIONS = 10000;
    private static final Logger log = LoggerFactory.getLogger(SecretBasedRSAKeyGenerator.class);

    public static KeyPair generateKeyPair(String secret) throws Exception {
        // Use the secret directly to seed the SecureRandom
        byte[] secretBytes = secret.getBytes();
        SecureRandom seededRandom = new SecureRandom(secretBytes);

        // Generate the RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(KEY_SIZE, seededRandom);
        return keyGen.generateKeyPair();
    }

    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey stringToPublicKey(String keyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Failed to create public key", e);
            throw new RuntimeException(e);
        }
    }

        public static PrivateKey stringToPrivateKey(String keyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }
}