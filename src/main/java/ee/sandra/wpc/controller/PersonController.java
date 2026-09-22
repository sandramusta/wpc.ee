package ee.sandra.wpc.controller;

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