package git.dimitrikvirik.springsoft.user.service;


import git.dimitrikvirik.springsoft.common.services.JwtTokenGenerator;
import git.dimitrikvirik.springsoft.common.services.JwtTokenReader;
import git.dimitrikvirik.springsoft.common.utils.SecretBasedRSAKeyGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService implements JwtTokenReader, JwtTokenGenerator {



    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private KeyPair keyPair;


    @Getter
    private String publicKeyString;

    @PostConstruct
    public void init() throws Exception {
        keyPair = SecretBasedRSAKeyGenerator.generateKeyPair(secretKey);
        publicKeyString = SecretBasedRSAKeyGenerator.publicKeyToString(keyPair.getPublic());
    }




    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {

        List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        Map<String, Object> extraClaimsMap = new HashMap<>();
        extraClaimsMap.put("id", userDetails.getUsername());
        extraClaimsMap.put("email", userDetails.getUsername());
        extraClaimsMap.put("authorities", authorities);
        extraClaimsMap.putAll(extraClaims);

        return Jwts
                .builder()
                .claims(extraClaimsMap)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith( keyPair.getPrivate())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Claims extractAllClaims(String token) {

        return (Claims) Jwts
                .parser()
                .verifyWith(  SecretBasedRSAKeyGenerator.stringToPublicKey(publicKeyString))
                .build()
                .parse(token).getPayload();
    }







}