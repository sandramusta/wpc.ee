package ee.sandra.wpc.dto;

import ee.sandra.wpc.entity.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long personId; // kui on olemas, kasutame seda (nt Swaggerist testides)
    private OrderType orderType; // kui tühi, eeldame INQUIRY
    private List<OrderItemRequest> items;
    private String source;
    private String message;

    // Kliendi andmed päringuvormist, kui personId puudub
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}