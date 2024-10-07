package git.dimitrikvirik.springsoft.common.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
/**
 * Interface for generating JWT tokens.
 */
public interface JwtTokenGenerator {
    /**
     * Generates a JWT token with extra claims for a user.
     * @param extraClaims Additional claims to include in the token
     * @param userDetails User details for whom the token is generated
     * @return The generated JWT token string
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Gets the public key string used for token verification.
     * @return The public key as a string
     */
    String getPublicKeyString();
}
