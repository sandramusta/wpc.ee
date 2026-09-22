package ee.sandra.wpc.service;

import ee.sandra.wpc.dto.OrderItemRequest;
import ee.sandra.wpc.dto.OrderRequest;
import ee.sandra.wpc.entity.*;
import ee.sandra.wpc.repository.OrderRepository;
import ee.sandra.wpc.repository.PersonRepository;
import ee.sandra.wpc.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final ProductVariantRepository productVariantRepository;

    public Order saveOrder(OrderRequest request) {
        Person person = personRepository.findById(request.getPersonId()).orElseThrow();

        Order order = new Order();
        order.setPerson(person);
        order.setOrderType(request.getOrderType() != null ? request.getOrderType() : OrderType.INQUIRY);
        order.setPaymentState(PaymentState.INITIAL);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductVariant variant = productVariantRepository.findById(itemRequest.getVariantId()).orElseThrow();

            OrderItem item = new OrderItem();
            item.setVariant(variant);
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceAtOrder(variant.getPrice());
            item.setOrder(order);
            items.add(item);

            total += variant.getPrice() * itemRequest.getQuantity();

            variant.setStock(variant.getStock() - itemRequest.getQuantity());
            productVariantRepository.save(variant);
        }

        order.setItems(items);
        order.setTotal(total);

        return orderRepository.save(order);
    }
}