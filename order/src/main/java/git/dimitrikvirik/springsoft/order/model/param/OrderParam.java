package git.dimitrikvirik.springsoft.order.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.order.model.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderParam {


    @JsonProperty("product")
    @NotBlank
    private String product;

    @JsonProperty("quantity")
    @Min(1)
    private Long quantity;

    @JsonProperty("price")
    @Range(min = 0, max = 1000000)
    private Double price;

    @JsonProperty("status")
    @NotNull
    private OrderStatus status;

}
