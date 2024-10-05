package git.dimitrikvirik.springsoft.order.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
