package ee.sandra.wpc.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Serializable: et toodet saaks Redise vahemällu salvestada
public class ProductVariant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer lengthMm;
    private String color;
    private double price;
    private Double weightKg;
    private Integer stock;
    private LocalDate expectedDate; // eeldatav tarneaeg, kui kaupa pole laos

    @ManyToOne
    @JsonIgnore // väldib lõputut tsüklit product -> variants -> product -> ...
    private Product product;
}