package git.dimitrikvirik.springsoft.order.service;


import git.dimitrikvirik.springsoft.order.client.AuthApiClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private volatile String publicKeyString;

    private final AuthApiClient authApiClient;






    public Map<String, Object> extractAllClaims(String token) {
       return  ( (Claims) Jwts
                .parser()
                .verifyWith(getPublicKey(getPublicKeyString()))
                .build()
                .parse(token).getPayload());
    }



    private   PublicKey getPublicKey(String publicKey) {
        try {

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create public key", e);
        }
    }

    public String getPublicKeyString() {
        if(publicKeyString == null) {
            synchronized (this) {
                if(publicKeyString == null) {
                    publicKeyString = Objects.requireNonNull(authApiClient.getPublicKey().getBody()).getKey();
                }
            }
        }
        return publicKeyString;
    }
}