package ee.sandra.wpc.repository;

import ee.sandra.wpc.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Partneri arved, uuemad eespool
    List<Invoice> findByPartner_IdOrderByIdDesc(Long partnerId);

    long countByPartner_Id(Long partnerId);
}
