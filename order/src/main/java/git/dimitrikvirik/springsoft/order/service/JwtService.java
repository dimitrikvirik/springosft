package git.dimitrikvirik.springsoft.order.service;


import git.dimitrikvirik.springsoft.common.services.JwtTokenReader;
import git.dimitrikvirik.springsoft.common.utils.SecretBasedRSAKeyGenerator;
import git.dimitrikvirik.springsoft.order.client.AuthApiClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JwtService implements JwtTokenReader {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private volatile String publicKeyString;

    private final AuthApiClient authApiClient;



    @Override
    public Claims extractAllClaims(String token) {
       return  ( (Claims) Jwts
                .parser()
               .verifyWith(SecretBasedRSAKeyGenerator.stringToPublicKey(getPublicKeyString()))
                .build()
                .parse(token).getPayload());
    }


    private String getPublicKeyString() {
        if(publicKeyString == null) {
            synchronized (this) {
                if(publicKeyString == null) {
                    log.info("Retrieving public key");
                    publicKeyString = Objects.requireNonNull(authApiClient.getPublicKey().getBody()).getKey();
                }
            }
        }
        return publicKeyString;
    }
}