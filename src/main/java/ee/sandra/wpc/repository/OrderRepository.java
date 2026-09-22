package ee.sandra.wpc.repository;

import ee.sandra.wpc.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPerson_Id(Long personId);
}