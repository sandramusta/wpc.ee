package ee.sandra.wpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

// Partneri arve tema kliendile. Tehakse kinnitatud projektist. Read ja summad salvestatakse arve juurde,
// et arve ei muutuks ka siis, kui hinnad hiljem muutuvad.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number; // partneri arvete järjekorranumber, nt "2026-001"

    private LocalDate invoiceDate;
    private LocalDate dueDate; // maksetähtaeg

    @ManyToOne
    @JsonIgnore
    private Person partner;

    @OneToOne
    private Project project; // projektist tulevad kliendi andmed

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines;

    // Double (mitte double), et need veerud saaks lisada ka siis, kui tabelis on juba vanu arveid
    private Double discountPercent;
    private Double total;           // ridade summa ilma KM-ta
    private Double discount;        // allahindlus eurodes
    private double totalWithoutVat; // pärast allahindlust
    private double vat;
    private double grandTotal;

    private Boolean paid; // Boolean, et tühi väärtus ei segaks andmebaasi
}
