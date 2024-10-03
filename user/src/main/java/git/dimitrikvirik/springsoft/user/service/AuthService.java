package git.dimitrikvirik.springsoft.user.service;

import git.dimitrikvirik.springsoft.user.model.dto.AuthDTO;
import git.dimitrikvirik.springsoft.user.model.param.UserLoginParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;


    public AuthDTO getToken(UserLoginParam loginParam) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginParam.getUsername());

        if (!passwordEncoder.matches(loginParam.getPassword(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return AuthDTO.builder().token(jwtService.generateToken(userDetails)).build();
    }

}
