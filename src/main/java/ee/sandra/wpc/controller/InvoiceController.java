package ee.sandra.wpc.controller;

import ee.sandra.wpc.dto.InvoiceRequest;
import ee.sandra.wpc.entity.Invoice;
import ee.sandra.wpc.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("partners/{partnerId}/invoices")
    public List<Invoice> getInvoices(@PathVariable Long partnerId) {
        return invoiceService.getInvoices(partnerId);
    }

    @GetMapping("invoices/{id}")
    public Invoice getInvoice(@PathVariable Long id) {
        return invoiceService.getInvoice(id);
    }

    @PostMapping("projects/{id}/invoice")
    public Invoice createInvoice(@PathVariable Long id, @RequestBody InvoiceRequest request) {
        return invoiceService.createInvoice(id, request);
    }

    @PutMapping("invoices/{id}/paid")
    public Invoice markPaid(@PathVariable Long id) {
        return invoiceService.markPaid(id);
    }
}
