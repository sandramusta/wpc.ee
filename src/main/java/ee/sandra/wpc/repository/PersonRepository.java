package ee.sandra.wpc.repository;

import ee.sandra.wpc.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByEmail(String email);

    // Kõik partnerid: need, kellel on partneri tase
    List<Person> findByPartnerLevelIsNotNull();
}