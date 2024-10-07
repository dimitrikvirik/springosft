package git.dimitrikvirik.springsoft.common.services;

import io.jsonwebtoken.Claims;

/**
 * Interface for reading claims from JWT tokens.
 */
public interface JwtTokenReader {
    /**
     * Extracts all claims from a JWT token.
     * @param token The JWT token string
     * @return Claims object containing all claims
     */
    Claims extractAllClaims(String token);


}
