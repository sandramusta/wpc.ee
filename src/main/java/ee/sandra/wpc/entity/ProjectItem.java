package ee.sandra.wpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

// Üks rida projekti materjalidest: milline variant ja mitu komplekti
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProjectItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ProductVariant variant;

    private Integer quantity;

    @ManyToOne
    @JsonIgnore // väldib lõputut tsüklit project -> items -> project -> ...
    private Project project;
}
