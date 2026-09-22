package ee.sandra.wpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String color;
    private double price;
    private Double weightKg;
    private Integer stock;

    @ManyToOne
    @JsonIgnore // väldib lõputut tsüklit product -> variants -> product -> ...
    private Product product;
}