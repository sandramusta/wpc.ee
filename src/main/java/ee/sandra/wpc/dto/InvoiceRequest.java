package ee.sandra.wpc.dto;

import ee.sandra.wpc.entity.InvoiceLine;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

// Mida frontend saadab arve koostamisel: partner võib enne ridu, allahindlust ja maksetähtaega muuta
@Getter
@Setter
public class InvoiceRequest {
    private LocalDate dueDate;
    private double discountPercent;
    private List<InvoiceLine> lines;
}
