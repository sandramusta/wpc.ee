package ee.sandra.wpc.service;

import ee.sandra.wpc.dto.PaymentBody;
import ee.sandra.wpc.dto.PaymentLink;
import ee.sandra.wpc.dto.PaymentResponse;
import ee.sandra.wpc.entity.Order;
import ee.sandra.wpc.entity.OrderItem;
import ee.sandra.wpc.entity.OrderType;
import ee.sandra.wpc.entity.PaymentState;
import ee.sandra.wpc.entity.ProductVariant;
import ee.sandra.wpc.repository.OrderRepository;
import ee.sandra.wpc.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // EveryPay andmed tulevad application-dev.properties failist, mitte koodist.
    // Nii ei satu parool GitHubi ja päris võtmed saab hiljem vahetada ilma koodi muutmata.
    @Value("${everypay.url}")
    private String everypayUrl;
    @Value("${everypay.api-username}")
    private String apiUsername;
    @Value("${everypay.api-secret}")
    private String apiSecret;
    @Value("${everypay.account-name}")
    private String accountName;
    @Value("${everypay.customer-url}")
    private String customerUrl;

    // Tellimuse viide EveryPays on kujul "wpc-15-1729420000000": tellimuse id ja aeg.
    // Aeg on lõpus, et sama tellimust saaks uuesti maksta, kui esimene katse katkes.
    private static final String REFERENCE_PREFIX = "wpc-";

    public PaymentLink makePayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getOrderType() != OrderType.PURCHASE) {
            throw new RuntimeException("Only purchases can be paid");
        }
        // Maksta saab ainult tellimust, mille makse on veel pooleli.
        // Ebaõnnestunud tellimuse kaup on juba lattu tagasi pandud, seega seda uuesti maksta ei saa.
        if (order.getPaymentState() != PaymentState.INITIAL) {
            throw new RuntimeException("Order cannot be paid, payment state is " + order.getPaymentState());
        }

        PaymentBody body = new PaymentBody();
        body.setAccount_name(accountName);
        body.setNonce(UUID.randomUUID().toString());
        body.setTimestamp(OffsetDateTime.now().toString());
        body.setAmount(order.getTotal()); // summa võetakse andmebaasist, mitte frontendist
        body.setOrder_reference(REFERENCE_PREFIX + order.getId() + "-" + System.currentTimeMillis());
        body.setCustomer_url(customerUrl);
        body.setApi_username(apiUsername);

        HttpEntity<PaymentBody> entity = new HttpEntity<>(body, headers());
        try {
            PaymentResponse response = restTemplate.exchange(
                    everypayUrl + "/payments/oneoff", HttpMethod.POST, entity, PaymentResponse.class).getBody();
            return new PaymentLink(response.getPayment_link());
        } catch (HttpClientErrorException e) {
            // EveryPay ütleb vastuses täpselt, mis valesti on. Kirjutame selle konsooli eraldi reale.
            System.out.println("EVERYPAY VIGA: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    // Kui klient tuleb EveryPayst tagasi, küsime EveryPaylt, kas makse õnnestus
    @CacheEvict(value = "products", allEntries = true) // laoseis võis muutuda
    @Transactional
    public Order checkPayment(String paymentReference) {
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        PaymentResponse response = restTemplate.exchange(
                everypayUrl + "/payments/" + paymentReference + "?api_username=" + apiUsername,
                HttpMethod.GET, entity, PaymentResponse.class).getBody();

        // "wpc-15-1729420000000" -> 15
        Long orderId = Long.valueOf(response.getOrder_reference().split("-")[1]);
        Order order = orderRepository.findById(orderId).orElseThrow();
        PaymentState oldState = order.getPaymentState();
        PaymentState newState = toPaymentState(response.getPayment_state());

        // Makse ebaõnnestus: paneme kauba lattu tagasi.
        // Ainult üks kord: kui klient lehte värskendab, on olek juba FAILED ja teist korda ei lisata.
        if (isFailed(newState) && !isFailed(oldState)) {
            returnToStock(order);
        }

        order.setPaymentState(newState);
        return orderRepository.save(order);
    }

    private boolean isFailed(PaymentState state) {
        return state == PaymentState.FAILED || state == PaymentState.ABANDONED || state == PaymentState.VOIDED;
    }

    private void returnToStock(Order order) {
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            variant.setStock(variant.getStock() + item.getQuantity());
            productVariantRepository.save(variant);
        }
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiUsername, apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // EveryPayl on rohkem olekuid kui meil (nt "waiting_for_3ds_response").
    // Need, mida me ei tunne, jäävad INITIAL-iks ehk "makse pooleli".
    private PaymentState toPaymentState(String everypayState) {
        return switch (everypayState) {
            case "settled" -> PaymentState.SETTLED;
            case "failed" -> PaymentState.FAILED;
            case "abandoned" -> PaymentState.ABANDONED;
            case "voided" -> PaymentState.VOIDED;
            default -> PaymentState.INITIAL;
        };
    }
}
