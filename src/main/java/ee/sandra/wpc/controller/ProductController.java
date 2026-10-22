package ee.sandra.wpc.controller;

import ee.sandra.wpc.entity.Product;
import ee.sandra.wpc.entity.ProductVariant;
import ee.sandra.wpc.repository.ProductRepository;
import ee.sandra.wpc.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    // Tootenimekiri tuleb Redise vahemälust. Andmebaasi poole minnakse ainult siis, kui vahemälus seda pole
    // (esimene kord, pärast 10 minutit või pärast seda, kui laoseis või toode muutus).
    @Cacheable("products")
    @GetMapping("products")
    public List<Product> getProducts() {
        log.info("Tooted võeti andmebaasist, mitte vahemälust");
        List<Product> products = productRepository.findAll();
        // Laeme variandid ja värvid kohe ära, et need jõuaksid koos tootega vahemällu
        products.forEach(product -> {
            product.getVariants().size();
            product.getColors().size();
        });
        return products;
    }

    @GetMapping("products/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    @CacheEvict(value = "products", allEntries = true) // toode muutus, vana nimekiri vahemälust välja
    @PostMapping("products")
    public Product saveProduct(@RequestBody Product product) {
        if (product.getId() != null) {
            throw new RuntimeException("Cannot add product with id");
        }
        // Seome variandid tootega, muidu jääb product_id tühjaks
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true) // toode muutus, vana nimekiri vahemälust välja
    @PutMapping("products")
    public Product editProduct(@RequestBody Product product) {
        if (product.getId() == null) {
            throw new RuntimeException("Cannot edit product without id");
        }
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true) // toode muutus, vana nimekiri vahemälust välja
    @DeleteMapping("products/{id}")
    public List<Product> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return productRepository.findAll();
    }

    // Eeldatav tarneaeg ühele variandile, nt PUT /variants/4/expected-date?date=2027-03-15
    // Ilma kuupäevata kustutab tarneaja.
    @CacheEvict(value = "products", allEntries = true) // toode muutus, vana nimekiri vahemälust välja
    @PutMapping("variants/{id}/expected-date")
    public ProductVariant setExpectedDate(@PathVariable Long id, @RequestParam(required = false) LocalDate date) {
        ProductVariant variant = productVariantRepository.findById(id).orElseThrow();
        variant.setExpectedDate(date);
        return productVariantRepository.save(variant);
    }
}
