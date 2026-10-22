package ee.sandra.wpc.dto;

import lombok.Getter;
import lombok.Setter;

// Partneri ettevõtte andmed, mida ta saab oma kontol muuta
@Getter
@Setter
public class PartnerProfileRequest {
    private String company;
    private String companyRegCode;
    private String vatNumber;
    private String companyAddress;
    private String website;
    private String bankName;
    private String bankAccount;
    private String phone;
    private String email;
    private String logo; // "data:image/png;base64,..." või tühi
    private Double installationHourlyRate;
    private Double installationHoursPerKit;
}
