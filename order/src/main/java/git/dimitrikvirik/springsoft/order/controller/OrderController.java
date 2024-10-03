package git.dimitrikvirik.springsoft.order.controller;

import git.dimitrikvirik.springsoft.order.model.dto.OrderDTO;
import git.dimitrikvirik.springsoft.order.model.param.OrderParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "Order management APIs")
@Validated
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;



    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @PageableAsQueryParam
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@PageableDefault(size = 50, sort="id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(orderFacade.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Returns details of a specific order")
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        OrderDTO order = orderFacade.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Adds a new order to the system")
    public ResponseEntity<OrderDTO> createOrder(
            @Parameter(description = "Order details") @RequestBody @Valid OrderParam orderParam) {
        OrderDTO createdOrder = orderFacade.createOrder(orderParam);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order", description = "Updates the details of a specific order")
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Parameter(description = "Updated order details") @RequestBody OrderParam orderParam) {
        OrderDTO updatedOrder = orderFacade.updateOrder(id, orderParam);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Removes an order from the system")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        orderFacade.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}