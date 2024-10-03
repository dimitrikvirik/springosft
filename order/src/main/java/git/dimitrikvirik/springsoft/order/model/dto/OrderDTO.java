package git.dimitrikvirik.springsoft.order.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import git.dimitrikvirik.springsoft.order.model.OrderStatus;
import git.dimitrikvirik.springsoft.order.model.entity.Order;
import lombok.*;

import java.io.IOException;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OrderDTO implements IdentifiedDataSerializable {


    public static final int FACTORY_ID = 2000;
    public static final int CLASS_ID = 2;

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

    @Override
    public int getFactoryId() {
        return FACTORY_ID;
    }

    @Override
    public int getClassId() {
        return CLASS_ID;
    }

    @Override
    public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
        objectDataOutput.writeLong(userId);
        objectDataOutput.writeString(product);
        objectDataOutput.writeLong(quantity);
        objectDataOutput.writeDouble(price);
        objectDataOutput.writeString(status.name());
        objectDataOutput.writeString(createdAt);
    }

    @Override
    public void readData(ObjectDataInput objectDataInput) throws IOException {

        userId = objectDataInput.readLong();
        product = objectDataInput.readString();
        quantity = objectDataInput.readLong();
        price = objectDataInput.readDouble();
        status = OrderStatus.valueOf(objectDataInput.readString());
        createdAt = objectDataInput.readString();

    }
}
