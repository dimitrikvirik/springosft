package git.dimitrikvirik.springsoft.user.repository;

import git.dimitrikvirik.springsoft.user.model.UserRoleName;
import git.dimitrikvirik.springsoft.user.model.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<UserRole, String> {

    boolean existsByName(UserRoleName name);

    Optional<UserRole> findByName(UserRoleName name);
}
