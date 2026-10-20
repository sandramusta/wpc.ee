package ee.sandra.wpc.service;

import ee.sandra.wpc.dto.OrderItemRequest;
import ee.sandra.wpc.dto.OrderRequest;
import ee.sandra.wpc.entity.*;
import ee.sandra.wpc.repository.OrderRepository;
import ee.sandra.wpc.repository.PersonRepository;
import ee.sandra.wpc.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public Order saveOrder(OrderRequest request) {
        Person person = findOrCreatePerson(request);

        Order order = new Order();
        order.setPerson(person);
        order.setOrderType(request.getOrderType() != null ? request.getOrderType() : OrderType.INQUIRY);
        order.setPaymentState(PaymentState.INITIAL);
        order.setCreatedAt(LocalDateTime.now());
        order.setSource(request.getSource());
        order.setMessage(request.getMessage());

        List<OrderItem> items = new ArrayList<>();
        double total = 0;
        for (OrderItemRequest itemRequest : request.getItems()) {
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new RuntimeException("Quantity must be at least 1");
            }

            ProductVariant variant = productVariantRepository.findById(itemRequest.getVariantId()).orElseThrow();

            OrderItem item = new OrderItem();
            item.setVariant(variant);
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceAtOrder(variant.getPrice());
            item.setOrder(order);
            items.add(item);

            total += variant.getPrice() * itemRequest.getQuantity();

            // Laoseis väheneb ainult päris ostu puhul, päring laoseisu ei muuda
            if (order.getOrderType() == OrderType.PURCHASE) {
                if (variant.getStock() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Not enough stock for variant " + variant.getId());
                }
                variant.setStock(variant.getStock() - itemRequest.getQuantity());
                productVariantRepository.save(variant);
            }
        }

        order.setItems(items);
        order.setTotal(total);
        return orderRepository.save(order);
    }

    // Kui personId on antud, võtame selle kliendi.
    // Muidu otsime kliendi e-posti järgi ja kui teda pole, loome uue.
    private Person findOrCreatePerson(OrderRequest request) {
        if (request.getPersonId() != null) {
            return personRepository.findById(request.getPersonId()).orElseThrow();
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        Person existing = personRepository.findByEmail(request.getEmail());
        if (existing != null) {
            return existing;
        }
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setPhone(request.getPhone());
        return personRepository.save(person);
    }
}