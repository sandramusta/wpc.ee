package ee.sandra.wpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

// Üks arve rida. Read salvestatakse arve juurde eraldi, et arve jääks samaks ka siis,
// kui projekti või kataloogi hinnad hiljem muutuvad.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;      // nt "WPC aialippide komplekt" või "Paigaldustöö"
    private Integer lengthMm; // tühi, kui pole toode
    private String color;
    private int quantity;
    private Double weightKg;  // ühe tüki kaal
    private double unitPrice; // ühiku hind ilma KM-ta

    @ManyToOne
    @JsonIgnore // väldib lõputut tsüklit invoice -> lines -> invoice -> ...
    private Invoice invoice;
}
