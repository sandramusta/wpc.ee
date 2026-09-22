package ee.sandra.wpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore // väldib lõputut tsüklit order -> items -> order -> ...
    private Order order;
}