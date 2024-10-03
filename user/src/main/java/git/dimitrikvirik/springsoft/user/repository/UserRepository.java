package git.dimitrikvirik.springsoft.user.repository;

import git.dimitrikvirik.springsoft.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.enabled = true")
    Page<User> findAllByEnabled( Pageable pageable);
}
