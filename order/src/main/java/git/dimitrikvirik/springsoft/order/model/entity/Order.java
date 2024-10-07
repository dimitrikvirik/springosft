package git.dimitrikvirik.springsoft.order.model.entity;

import git.dimitrikvirik.springsoft.common.model.entity.BaseDomain;
import git.dimitrikvirik.springsoft.order.model.OrderStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseDomain {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product")
    private String product;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;


}
