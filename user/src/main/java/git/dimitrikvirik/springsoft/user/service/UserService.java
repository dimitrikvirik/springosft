package git.dimitrikvirik.springsoft.user.service;

import git.dimitrikvirik.springsoft.user.model.UserRoleName;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import git.dimitrikvirik.springsoft.user.model.entity.UserRole;
import git.dimitrikvirik.springsoft.user.repository.RoleRepository;
import git.dimitrikvirik.springsoft.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        if(!roleRepository.existsByName(UserRoleName.ADMIN)) {
            log.info("Create ADMIN role");
            UserRole userRole = new UserRole();
            userRole.setName(UserRoleName.ADMIN);
            userRole.setAuthorities(List.of("GET_USERS", "DELETE_USER", "UPDATE_USER", "DELETE_ORDER", "UPDATE_ORDER", "GET_ORDERS"));
        }
    }

    public UserRole getRole(UserRoleName userRoleName){
        return roleRepository.findByName(userRoleName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Role not found")
        );
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllByEnabled(pageable);
    }


    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

}
