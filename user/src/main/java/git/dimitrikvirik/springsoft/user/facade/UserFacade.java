package git.dimitrikvirik.springsoft.user.facade;

import git.dimitrikvirik.springsoft.user.model.dto.UserDTO;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import git.dimitrikvirik.springsoft.user.model.param.UserCreateParam;
import git.dimitrikvirik.springsoft.user.model.param.UserUpdateParam;
import git.dimitrikvirik.springsoft.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable)
                .map(UserDTO::fromEntity);
    }

    @PostAuthorize("returnObject.username == authentication.name or hasAuthority('GET_USERS')")
    public UserDTO getUserById(Long id) {
        return UserDTO.fromEntity(userService.getUserById(id));
    }

    public UserDTO createUser(UserCreateParam userCreateParam){
        User user = new User();
        user.setFirstname(userCreateParam.getFirstname());
        user.setLastname(userCreateParam.getLastname());
        user.setUsername(userCreateParam.getUsername());
        user.setEmail(userCreateParam.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateParam.getPassword()));
        return UserDTO.fromEntity(userService.save(user));
    }

    public UserDTO updateUser(Long id, UserUpdateParam userUpdateParam) {
        User user = userService.getUserById(id);
        user.setFirstname(userUpdateParam.getFirstname());
        user.setLastname(userUpdateParam.getLastname());
        user.setUsername(userUpdateParam.getUsername());
        user.setEmail(userUpdateParam.getEmail());
        return UserDTO.fromEntity(userService.save(user));
    }

    public void deleteUser(Long id) {
        User user = userService.getUserById(id);
        user.setEnabled(false);
        userService.save(user);
    }

}
