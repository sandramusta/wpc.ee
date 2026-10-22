package ee.sandra.wpc.repository;

import ee.sandra.wpc.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Partneri projektid, uuemad eespool
    List<Project> findByPartner_IdOrderByIdDesc(Long partnerId);
}
