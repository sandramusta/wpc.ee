package ee.sandra.wpc.repository;

import ee.sandra.wpc.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}