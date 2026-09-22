package ee.sandra.wpc.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer lengthMm;

    private String color; // "pruun" või "tumehall" — peab kattuma Product.colors väärtustega

    private double price;

    private Double weightKg;

    private Integer stock;

    @ManyToOne
    private Product product;
}