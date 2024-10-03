package git.dimitrikvirik.springsoft.user.facade;

import git.dimitrikvirik.springsoft.user.model.dto.UserDTO;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import git.dimitrikvirik.springsoft.user.model.param.UserCreateParam;
import git.dimitrikvirik.springsoft.user.model.param.UserUpdateParam;
import git.dimitrikvirik.springsoft.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private static final Logger log = LoggerFactory.getLogger(UserFacade.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;



    @Cacheable(value = "users", key = "'users_' + #pageable.pageSize + '_' + #pageable.pageNumber")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.info("Get all users");
        return userService.getAllUsers(pageable)
                .map(UserDTO::fromEntity);
    }

    @PostAuthorize("returnObject.username == authentication.name or hasAuthority('GET_USERS')")
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        log.info("Get user by id: {}", id);
        return UserDTO.fromEntity(userService.getUserById(id));
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(UserCreateParam userCreateParam){
        log.info("Create user: {}", userCreateParam);
        User user = new User();
        user.setFirstname(userCreateParam.getFirstname());
        user.setLastname(userCreateParam.getLastname());
        user.setUsername(userCreateParam.getUsername());
        user.setEmail(userCreateParam.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateParam.getPassword()));
        user.setEnabled(true);
        return UserDTO.fromEntity(userService.save(user));
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserDTO updateUser(Long id, UserUpdateParam userUpdateParam) {
        log.info("Update user: {}", id);
        User user = userService.getUserById(id);
        user.setFirstname(userUpdateParam.getFirstname());
        user.setLastname(userUpdateParam.getLastname());
        user.setUsername(userUpdateParam.getUsername());
        user.setEmail(userUpdateParam.getEmail());
        return UserDTO.fromEntity(userService.save(user));
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        log.info("Delete user: {}", id);
        User user = userService.getUserById(id);
        user.setEnabled(false);
        userService.save(user);
    }

}
