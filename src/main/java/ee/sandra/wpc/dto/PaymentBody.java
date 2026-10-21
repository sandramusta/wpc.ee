package ee.sandra.wpc.dto;

import lombok.Getter;
import lombok.Setter;

// Mida saadame EveryPayle makse loomiseks. Väljade nimed peavad olema täpselt sellised, nagu EveryPay ootab.
@Getter
@Setter
public class PaymentBody {
    private String account_name;
    private String nonce;
    private String timestamp;
    private Double amount;
    private String order_reference;
    private String customer_url;
    private String api_username;
}
