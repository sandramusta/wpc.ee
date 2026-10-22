package ee.sandra.wpc.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // WRITE_ONLY: parooli saab saata, aga backend ei näita seda kunagi vastuses tagasi
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // hiljem lisame Spring Security + BCrypt

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    // B2B: ettevõtte nimi ja partneri tase. Tavakliendil on partnerLevel tühi (null).
    private String company;

    // Partneri ettevõtte andmed, mis lähevad tema hinnapakkumistele
    private String companyRegCode;  // registrikood
    private String vatNumber;       // KMKR number
    private String companyAddress;
    private String website;
    private String bankName;        // nt "SWEDBANK AS SWIFT HABAEE2X"
    private String bankAccount;     // IBAN

    // Partneri logo pildina tekstikujul ("data:image/png;base64,...").
    // Väike logo mahub nii otse andmebaasi ja eraldi failiserverit pole vaja.
    @Column(columnDefinition = "TEXT")
    private String logo;

    // Paigalduse hinna arvutamiseks: tunnihind (km-ta) ja mitu tundi kulub ühe komplekti paigaldamiseks
    private Double installationHourlyRate;
    private Double installationHoursPerKit;

    @Enumerated(EnumType.STRING)
    private PartnerLevel partnerLevel;

    // Allahindlus protsentides, tavakliendil 0. Läheb JSON-i kaasa, aga andmebaasi seda ei salvestata.
    public int getDiscountPercent() {
        return partnerLevel == null ? 0 : partnerLevel.getDiscountPercent();
    }
}
