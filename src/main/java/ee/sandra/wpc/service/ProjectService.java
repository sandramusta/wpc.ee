package ee.sandra.wpc.service;

import ee.sandra.wpc.dto.OrderItemRequest;
import ee.sandra.wpc.dto.OrderRequest;
import ee.sandra.wpc.dto.ProjectRequest;
import ee.sandra.wpc.entity.*;
import ee.sandra.wpc.repository.PersonRepository;
import ee.sandra.wpc.repository.ProductVariantRepository;
import ee.sandra.wpc.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PersonRepository personRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderService orderService;

    public List<Project> getProjects(Long partnerId) {
        return projectRepository.findByPartner_IdOrderByIdDesc(partnerId);
    }

    public Project createProject(Long partnerId, ProjectRequest request) {
        Person partner = personRepository.findById(partnerId).orElseThrow();
        if (partner.getPartnerLevel() == null) {
            throw new RuntimeException("Only partners can create projects");
        }
        Project project = new Project();
        project.setPartner(partner);
        project.setStatus(ProjectStatus.OFFER);
        project.setCreatedAt(LocalDateTime.now());
        project.setItems(new ArrayList<>());
        fillProject(project, request);
        return projectRepository.save(project);
    }

    // Projekti muutmine. Tellitud või arvega projekti enam muuta ei saa, sest need andmed on juba edasi läinud.
    @Transactional
    public Project updateProject(Long projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        if (project.getStatus() == ProjectStatus.ORDERED || project.getInvoiceId() != null) {
            throw new RuntimeException("Ordered or invoiced project cannot be changed");
        }
        fillProject(project, request);
        return projectRepository.save(project);
    }

    // Ühine osa loomisel ja muutmisel: kliendi andmed, materjalid ja paigalduse hind
    private void fillProject(Project project, ProjectRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Project needs at least one item");
        }
        project.setClientName(request.getClientName());
        project.setClientPhone(request.getClientPhone());
        project.setClientEmail(request.getClientEmail());
        project.setAddress(request.getAddress());
        project.setInstallDate(request.getInstallDate());
        project.setNotes(request.getNotes());
        project.setDiscountPercent(request.getDiscountPercent());

        // Vanad read eemaldatakse ja asemele pannakse uued (orphanRemoval kustutab vanad andmebaasist)
        project.getItems().clear();
        int kits = 0;
        for (OrderItemRequest itemRequest : request.getItems()) {
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new RuntimeException("Quantity must be at least 1");
            }
            ProjectItem item = new ProjectItem();
            item.setVariant(productVariantRepository.findById(itemRequest.getVariantId()).orElseThrow());
            item.setQuantity(itemRequest.getQuantity());
            item.setProject(project);
            project.getItems().add(item);
            kits += itemRequest.getQuantity();
        }

        // Paigalduse hind: partner kirjutab ise või arvutatakse tema tunnihinnast
        project.setInstallationManual(request.isInstallationManual());
        if (request.isInstallationManual()) {
            project.setInstallationPrice(request.getInstallationPrice());
        } else {
            project.setInstallationPrice(installationPrice(project.getPartner(), kits));
        }
    }

    // Komplektide arv × tunde komplekti kohta × tunnihind. Kui tunde pole määratud, arvestame 1 tund komplekti kohta.
    private double installationPrice(Person partner, int kits) {
        double rate = partner.getInstallationHourlyRate() == null ? 0 : partner.getInstallationHourlyRate();
        double hoursPerKit = partner.getInstallationHoursPerKit() == null ? 1 : partner.getInstallationHoursPerKit();
        return Math.round(kits * hoursPerKit * rate * 100) / 100.0;
    }

    // Klient kinnitas hinnapakkumise
    public Project confirm(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        if (project.getStatus() != ProjectStatus.OFFER) {
            throw new RuntimeException("Only an offer can be confirmed");
        }
        project.setStatus(ProjectStatus.CONFIRMED);
        return projectRepository.save(project);
    }

    // Üks nupuvajutus: projekti materjalidest tehakse meile tellimus partneri hindadega.
    // Laoseis väheneb ja meie saadame partnerile arve.
    @Transactional
    public Project order(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        if (project.getStatus() != ProjectStatus.CONFIRMED) {
            throw new RuntimeException("Only a confirmed project can be ordered");
        }

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setPersonId(project.getPartner().getId());
        orderRequest.setOrderType(OrderType.PURCHASE);
        orderRequest.setSource("partner-projekt");
        orderRequest.setMessage("Projekt nr " + project.getId() + ": " + project.getClientName() + ", "
                + project.getAddress() + ". Paigaldus: " + project.getInstallDate());

        List<OrderItemRequest> orderItems = new ArrayList<>();
        for (ProjectItem projectItem : project.getItems()) {
            OrderItemRequest orderItem = new OrderItemRequest();
            orderItem.setVariantId(projectItem.getVariant().getId());
            orderItem.setQuantity(projectItem.getQuantity());
            orderItems.add(orderItem);
        }
        orderRequest.setItems(orderItems);

        Order order = orderService.saveOrder(orderRequest);
        project.setOrderId(order.getId());
        project.setStatus(ProjectStatus.ORDERED);
        return projectRepository.save(project);
    }

    public void delete(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        if (project.getStatus() == ProjectStatus.ORDERED || project.getInvoiceId() != null) {
            throw new RuntimeException("Ordered or invoiced project cannot be deleted");
        }
        projectRepository.delete(project);
    }
}
