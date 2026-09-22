package ee.sandra.wpc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    private Long variantId;
    private Integer quantity;
}