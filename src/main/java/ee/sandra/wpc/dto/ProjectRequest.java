package ee.sandra.wpc.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

// Mida frontend saadab projekti loomiseks või muutmiseks
@Getter
@Setter
public class ProjectRequest {
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String address;
    private LocalDate installDate; // kujul "2026-11-05"
    private String notes;
    private double installationPrice;     // loetakse ainult siis, kui installationManual = true
    private boolean installationManual;
    private double discountPercent;
    private List<OrderItemRequest> items; // variantId + quantity, sama mis tellimusel
}
