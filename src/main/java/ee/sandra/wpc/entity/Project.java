package ee.sandra.wpc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Partneri projekt: tema klient, objekt, vajalikud materjalid ja paigalduse tähtaeg
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore // partnerit pole vaja iga projektiga kaasa saata
    private Person partner;

    // Partneri klient
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String address; // objekti aadress

    private LocalDate installDate; // paigalduse tähtaeg

    @Column(length = 2000)
    private String notes;

    private double installationPrice; // paigaldustöö hind ilma KM-ta, mille partner kliendile paneb
    private Boolean installationManual; // true = partner kirjutas hinna ise. Boolean (mitte boolean), et vanadel projektidel võiks väli tühi olla
    private double discountPercent;   // allahindlus, mille partner kliendile annab

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private LocalDateTime createdAt;

    private Long orderId; // meie tellimuse number, kui partner on tellinud
    private Long invoiceId; // kliendile tehtud arve, kui see on loodud

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectItem> items;
}
