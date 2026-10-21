package ee.sandra.wpc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Mida saadame frontendile: link, kuhu klient maksma suunata
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLink {
    private String link;
}
