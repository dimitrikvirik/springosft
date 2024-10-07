package git.dimitrikvirik.springsoft.order.facade;

import git.dimitrikvirik.springsoft.common.model.dto.PrincipalDTO;
import git.dimitrikvirik.springsoft.common.model.dto.UserKafkaDTO;
import git.dimitrikvirik.springsoft.order.model.dto.OrderDTO;
import git.dimitrikvirik.springsoft.order.model.entity.Order;
import git.dimitrikvirik.springsoft.order.model.param.OrderParam;
import git.dimitrikvirik.springsoft.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private static final Logger log = LoggerFactory.getLogger(OrderFacade.class);
    private final OrderService orderService;

    @Cacheable(value = "orders", key = "'orders_' + #pageable.pageSize + '_' + #pageable.pageNumber")
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        if (getPrincipal().authorities().contains("GET_ORDERS")) {
            log.info("Get all orders");
            return orderService.getOrders(pageable).map(OrderDTO::fromEntity);
        }

        log.info("Get user orders");
        return orderService.getUserOrders(getPrincipal().id(), pageable).map(OrderDTO::fromEntity);
    }

    @PostAuthorize("returnObject.userId == authentication.principal.id or hasAuthority('GET_ORDERS')")
    @Cacheable(value = "orders", key = "#id")
    public OrderDTO getOrderById(Long id) {
        log.info("Get order by id: {}", id);
        return OrderDTO.fromEntity(orderService.getById(id));
    }

    @CacheEvict(value = "orders", allEntries = true)
    public OrderDTO createOrder(OrderParam orderParam) {
        log.info("Create order: {}", orderParam);
        PrincipalDTO principal = getPrincipal();

        Order order = new Order();
        order.setProduct(orderParam.getProduct());
        order.setQuantity(orderParam.getQuantity());
        order.setPrice(orderParam.getPrice());
        order.setUserId(principal.id());
        order.setStatus(orderParam.getStatus());
        order.setDeleted(false);

        return OrderDTO.fromEntity(orderService.save(order));
    }

    @CacheEvict(value = "orders", allEntries = true)
    public OrderDTO updateOrder(Long id, OrderParam orderParam) {
        log.info("Update order: {}", orderParam);
        PrincipalDTO principal = getPrincipal();

        Order order = orderService.getById(id);
        if (!Objects.equals(principal.id(), order.getUserId()) && !principal.authorities().contains("UPDATE_ORDER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        order.setProduct(orderParam.getProduct());
        order.setQuantity(orderParam.getQuantity());
        order.setPrice(orderParam.getPrice());
        order.setStatus(orderParam.getStatus());

        return OrderDTO.fromEntity(orderService.save(order));
    }

    @CacheEvict(value = "orders", allEntries = true)
    public void deleteOrder(Long id) {
        log.info("Delete order: {}", id);
        PrincipalDTO principal = getPrincipal();

        Order order = orderService.getById(id);
        if (!Objects.equals(principal.id(), order.getUserId()) && !principal.authorities().contains("DELETE_ORDER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        order.setDeleted(true);
        orderService.save(order);
    }


    private PrincipalDTO getPrincipal() {
        return (PrincipalDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @KafkaListener(topics = "user-topic", groupId = "order-group")
    public void userConsume(UserKafkaDTO userKafkaDTO) {
        log.info("User consume: {}", userKafkaDTO);
        if (!userKafkaDTO.enabled()) {
            orderService.deleteAllUserOrders(userKafkaDTO.userId());
        }
    }

}
