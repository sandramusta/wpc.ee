package ee.sandra.wpc.controller;

import ee.sandra.wpc.dto.PartnerProfileRequest;
import ee.sandra.wpc.entity.Person;
import ee.sandra.wpc.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class PersonController {

    private final PersonRepository personRepository;

    @GetMapping("persons")
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    // B2B: kõik partnerid
    @GetMapping("partners")
    public List<Person> getPartners() {
        return personRepository.findByPartnerLevelIsNotNull();
    }

    // B2B: partner muudab oma ettevõtte andmeid ja logo
    @PutMapping("partners/{id}/profile")
    public Person updateProfile(@PathVariable Long id, @RequestBody PartnerProfileRequest request) {
        Person partner = personRepository.findById(id).orElseThrow();
        if (partner.getPartnerLevel() == null) {
            throw new RuntimeException("Only partners have a profile");
        }
        // Logo peab olema pilt ja mitte liiga suur (umbes kuni 500 KB)
        String logo = request.getLogo();
        if (logo != null && !logo.isEmpty()) {
            if (!logo.startsWith("data:image/")) {
                throw new RuntimeException("Logo must be an image");
            }
            if (logo.length() > 700_000) {
                throw new RuntimeException("Logo is too large");
            }
        }
        partner.setCompany(request.getCompany());
        partner.setCompanyRegCode(request.getCompanyRegCode());
        partner.setVatNumber(request.getVatNumber());
        partner.setCompanyAddress(request.getCompanyAddress());
        partner.setWebsite(request.getWebsite());
        partner.setBankName(request.getBankName());
        partner.setBankAccount(request.getBankAccount());
        partner.setPhone(request.getPhone());
        partner.setEmail(request.getEmail());
        partner.setLogo(logo);
        partner.setInstallationHourlyRate(request.getInstallationHourlyRate());
        partner.setInstallationHoursPerKit(request.getInstallationHoursPerKit());
        return personRepository.save(partner);
    }

    @GetMapping("persons/{id}")
    public Person getPerson(@PathVariable Long id) {
        return personRepository.findById(id).orElseThrow();
    }

    @PostMapping("persons")
    public Person savePerson(@RequestBody Person person) {
        if (person.getId() != null) {
            throw new RuntimeException("Cannot add person with id");
        }
        Person existing = personRepository.findByEmail(person.getEmail());
        if (existing != null) {
            throw new RuntimeException("Email already in use");
        }
        return personRepository.save(person);
    }
}