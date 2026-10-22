package ee.sandra.wpc.controller;

import ee.sandra.wpc.dto.ProjectRequest;
import ee.sandra.wpc.entity.Project;
import ee.sandra.wpc.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("partners/{partnerId}/projects")
    public List<Project> getProjects(@PathVariable Long partnerId) {
        return projectService.getProjects(partnerId);
    }

    @PostMapping("partners/{partnerId}/projects")
    public Project createProject(@PathVariable Long partnerId, @RequestBody ProjectRequest request) {
        return projectService.createProject(partnerId, request);
    }

    @PutMapping("projects/{id}")
    public Project updateProject(@PathVariable Long id, @RequestBody ProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @PutMapping("projects/{id}/confirm")
    public Project confirm(@PathVariable Long id) {
        return projectService.confirm(id);
    }

    @PostMapping("projects/{id}/order")
    public Project order(@PathVariable Long id) {
        return projectService.order(id);
    }

    @DeleteMapping("projects/{id}")
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}
