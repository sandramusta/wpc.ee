package ee.sandra.wpc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 4000)
    private String description;

    private String material; // nt "WPC puitplastkomposiit (60% puit, 30% plast, 10% lisandid)"

    @Column(length = 2000)
    private String kitContents; // "Komplekti sisu" tekst, nt "50 WPC lippi, PVC V-profiilid..."

    private String image;

    private Boolean active;

    @ElementCollection
    private List<String> colors; // nt ["pruun", "tumehall"]

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants;

    // SEO
    private String metaTitle;
    private String metaDescription;
}
