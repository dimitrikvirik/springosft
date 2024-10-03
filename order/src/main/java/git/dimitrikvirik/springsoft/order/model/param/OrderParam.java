package git.dimitrikvirik.springsoft.order.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.order.model.OrderStatus;
import lombok.Data;

@Data
public class OrderParam {


    @JsonProperty("product")
    private String product;

    @JsonProperty("quantity")
    private Long quantity;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("status")
    private OrderStatus status;

}
