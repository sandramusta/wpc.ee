package ee.sandra.wpc.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private double priceAtOrder;

    @ManyToOne
    private ProductVariant variant;

    @ManyToOne
    private Order order; // see peab viitama ee.sandra.wpc.entity.Order, ILMA eraldi import reata
}