package ee.sandra.wpc.dto;

import ee.sandra.wpc.entity.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long personId;
    private OrderType orderType; // kui tühi, eeldame INQUIRY
    private List<OrderItemRequest> items;
}