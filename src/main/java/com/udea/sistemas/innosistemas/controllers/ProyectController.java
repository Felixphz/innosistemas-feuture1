package com.udea.sistemas.innosistemas.controllers;
import org.springframework.web.bind.annotation.RestController;
import com.udea.sistemas.innosistemas.models.dto.ProjectDto;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.repository.ProjectRepository;
import com.udea.sistemas.innosistemas.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/proyects")
@Tag(name = "Projects", description = "Endpoints for managing Projects")
public class ProyectController {
    private final ProjectService proyectService;
    private final ProjectRepository projectRepository;

    public ProyectController(ProjectService proyectService, ProjectRepository projectRepository) {
        this.proyectService = proyectService;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/{projectId}/users/single-team")
    @Operation(summary =  "Get all Users in a Single Team", description = "Retrieves a list of all users in a single team for a specific project")
    public ResponseEntity<List<User>> getUsersInOneTeam(@PathVariable Integer projectId) {
        return ResponseEntity.ok(proyectService.getUsersInOneTeamByProject(projectId));
    }

    @GetMapping("/getAllProjects")
    @Operation(summary =  "Get all Projects", description = "Retrieves a list of all projects in the system")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        try {
            List<ProjectDto> projects = projectRepository.findAll().stream()
                    .map(proj -> new ProjectDto(proj.getNameProject()))
                    .toList();
            if (projects.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
