package ee.sandra.wpc.repository;

import ee.sandra.wpc.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}