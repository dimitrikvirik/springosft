package git.dimitrikvirik.springsoft.user.service;

import git.dimitrikvirik.springsoft.user.model.dto.AuthDTO;
import git.dimitrikvirik.springsoft.user.model.dto.PublicKeyDTO;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import git.dimitrikvirik.springsoft.user.model.param.UserLoginParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;


    public AuthDTO getToken(UserLoginParam loginParam) {

        User userDetails = (User) userDetailsService.loadUserByUsername(loginParam.getUsername());

        if (!passwordEncoder.matches(loginParam.getPassword(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return AuthDTO.builder().token(jwtService.generateToken(
                Map.of("id", userDetails.getId(), "email", userDetails.getEmail()), userDetails)).build();
    }


    public PublicKeyDTO getPublicKey(){

        return PublicKeyDTO.builder().key(jwtService.getPublicKeyString()).build();
    }

}
