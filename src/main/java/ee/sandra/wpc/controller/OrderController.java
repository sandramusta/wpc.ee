package ee.sandra.wpc.controller;

import ee.sandra.wpc.dto.OrderRequest;
import ee.sandra.wpc.dto.PaymentLink;
import ee.sandra.wpc.entity.Order;
import ee.sandra.wpc.repository.OrderRepository;
import ee.sandra.wpc.service.OrderService;
import ee.sandra.wpc.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://lvh.me:4200"})
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping("orders")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("my-orders/{personId}")
    public List<Order> getMyOrders(@PathVariable Long personId) {
        return orderRepository.findByPerson_Id(personId);
    }

    @PostMapping("orders")
    public Order saveOrder(@RequestBody OrderRequest request) {
        return orderService.saveOrder(request);
    }

    // Loob EveryPays makse ja tagastab lingi, kuhu klient suunata
    @PostMapping("orders/{id}/payment")
    public PaymentLink makePayment(@PathVariable Long id) {
        return paymentService.makePayment(id);
    }

    // Kontrollib pärast maksmist, kas makse õnnestus
    @GetMapping("check-payment")
    public Order checkPayment(@RequestParam String paymentReference) {
        return paymentService.checkPayment(paymentReference);
    }
}
