package git.dimitrikvirik.springsoft.order.repository;

import git.dimitrikvirik.springsoft.order.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByDeleted(Boolean deleted, Pageable pageable);

    Page<Order> findAllByUserIdAndDeleted(Long userId, Boolean deleted, Pageable pageable);
}
