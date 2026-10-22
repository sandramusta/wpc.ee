package ee.sandra.wpc.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Serializable: et toodet saaks Redise vahemällu salvestada
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String slug; // URL-sõbralik versioon, nt "piirdelauad" (SEO jaoks oluline)

    private String description; // lühikirjeldus kategooria lehele
}