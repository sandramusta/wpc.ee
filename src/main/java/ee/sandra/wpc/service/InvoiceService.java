package ee.sandra.wpc.service;

import ee.sandra.wpc.dto.InvoiceRequest;
import ee.sandra.wpc.entity.*;
import ee.sandra.wpc.repository.InvoiceRepository;
import ee.sandra.wpc.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final double VAT = 0.24;

    private final InvoiceRepository invoiceRepository;
    private final ProjectRepository projectRepository;

    public List<Invoice> getInvoices(Long partnerId) {
        return invoiceRepository.findByPartner_IdOrderByIdDesc(partnerId);
    }

    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id).orElseThrow();
    }

    // Kinnitatud projektist tehakse kliendile arve. Read, allahindluse ja maksetähtaja saadab frontend
    // (partner võis neid enne muuta). Ühele projektile ainult üks arve.
    @Transactional
    public Invoice createInvoice(Long projectId, InvoiceRequest request) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        if (project.getStatus() == ProjectStatus.OFFER) {
            throw new RuntimeException("Client has not confirmed the offer yet");
        }
        if (project.getInvoiceId() != null) {
            throw new RuntimeException("Project already has an invoice");
        }
        if (request.getLines() == null || request.getLines().isEmpty()) {
            throw new RuntimeException("Invoice needs at least one line");
        }
        if (request.getDueDate() == null || request.getDueDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Due date cannot be in the past");
        }
        Person partner = project.getPartner();

        Invoice invoice = new Invoice();
        invoice.setPartner(partner);
        invoice.setProject(project);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(request.getDueDate());
        invoice.setPaid(false);
        // Arve number: aasta ja partneri arvete järjekorranumber, nt 2026-001
        long count = invoiceRepository.countByPartner_Id(partner.getId());
        invoice.setNumber(LocalDate.now().getYear() + "-" + String.format("%03d", count + 1));

        // Read ja summad
        List<InvoiceLine> lines = new ArrayList<>();
        double total = 0;
        for (InvoiceLine line : request.getLines()) {
            if (line.getQuantity() <= 0 || line.getUnitPrice() < 0) {
                throw new RuntimeException("Quantity must be at least 1 and price cannot be negative");
            }
            line.setId(null);
            line.setInvoice(invoice);
            lines.add(line);
            total += round2(line.getUnitPrice() * line.getQuantity()); // sama ümardus nagu arve real
        }
        invoice.setLines(lines);
        invoice.setDiscountPercent(request.getDiscountPercent());
        invoice.setTotal(round2(total));
        invoice.setDiscount(round2(invoice.getTotal() * request.getDiscountPercent() / 100));
        invoice.setTotalWithoutVat(round2(invoice.getTotal() - invoice.getDiscount()));
        invoice.setVat(round2(invoice.getTotalWithoutVat() * VAT));
        invoice.setGrandTotal(round2(invoice.getTotalWithoutVat() + invoice.getVat()));

        Invoice saved = invoiceRepository.save(invoice);
        project.setInvoiceId(saved.getId());
        projectRepository.save(project);
        return saved;
    }

    // Partner märgib, et klient on arve tasunud
    public Invoice markPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow();
        invoice.setPaid(true);
        return invoiceRepository.save(invoice);
    }

    private double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
