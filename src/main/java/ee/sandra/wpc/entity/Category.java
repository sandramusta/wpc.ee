package ee.sandra.wpc.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String slug; // URL-sõbralik versioon, nt "piirdelauad" (SEO jaoks oluline)

    private String description; // lühikirjeldus kategooria lehele
}