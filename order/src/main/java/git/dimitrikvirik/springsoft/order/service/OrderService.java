package git.dimitrikvirik.springsoft.order.service;

import git.dimitrikvirik.springsoft.order.model.entity.Order;
import git.dimitrikvirik.springsoft.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order getById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        );
        if(order.getDeleted()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        return order;
    }

    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAllByDeleted(false,pageable);
    }

    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserIdAndDeleted(userId, false, pageable);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public void deleteAllUserOrders(Long userId) {
        orderRepository.deleteAllByUserId(userId);
    }
}
