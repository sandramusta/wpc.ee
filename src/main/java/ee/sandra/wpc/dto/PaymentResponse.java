package ee.sandra.wpc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

// EveryPay vastus. Meid huvitavad ainult need väljad, ülejäänud jätame tähelepanuta.
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    private String order_reference;
    private String payment_reference;
    private String payment_link;
    private String payment_state;
}
