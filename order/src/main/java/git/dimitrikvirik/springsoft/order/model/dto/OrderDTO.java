package git.dimitrikvirik.springsoft.order.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.order.model.OrderStatus;
import git.dimitrikvirik.springsoft.order.model.entity.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("product")
    private String product;

    @JsonProperty("quantity")
    private Long quantity;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("createdAt")
    private String createdAt;


    public static OrderDTO fromEntity(Order order) {

        return OrderDTO.builder()
                .userId(order.getUserId())
                .product(order.getProduct())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt().toString())
                .build();

    }

}
