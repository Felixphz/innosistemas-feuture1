package com.udea.sistemas.innosistemas.controllers;
import com.udea.sistemas.innosistemas.models.dto.ApiResponseDto;
import com.udea.sistemas.innosistemas.models.dto.CreateProjectDto;
import com.udea.sistemas.innosistemas.models.dto.modProjectDto;
import com.udea.sistemas.innosistemas.models.entity.Project;
import org.springframework.web.bind.annotation.*;
import com.udea.sistemas.innosistemas.models.dto.ProjectDto;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.repository.ProjectRepository;
import com.udea.sistemas.innosistemas.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "http://localhost:3004")
@RestController
@RequestMapping("/api/project")
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
    @PreAuthorize("hasAuthority('read_project')")
    @Operation(summary =  "Get all Projects", description = "Retrieves a list of all projects in the system")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        try {
            List<ProjectDto> projects = projectRepository.findAll().stream()
                    .map(proj -> new ProjectDto(proj.getNameProject(),proj.getId()))
                    .toList();
            if (projects.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create_project')")
    @Operation(summary = "Create a new project", description = "Creates a new project in the system")
    public ResponseEntity<ApiResponseDto> createProject(@RequestBody CreateProjectDto projectDto){
        try{
            if (proyectService.createProject(projectDto.courseId(), projectDto.nameProject(), projectDto.descriptions())) {
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto("Project was created successfully", true));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto("Invalid Project data", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto("Error creating project: " + e.getMessage(), false));
        }
    }

    @PutMapping("/updateProject")
    @PreAuthorize("hasAuthority('update_project')")
    @Operation(summary = "update a project", description = "Creates the data of project in the system")
    public ResponseEntity<ApiResponseDto> updateProject(@RequestBody modProjectDto projectDto){
        try{
            if (proyectService.updateProject(projectDto.projectId(), projectDto.courseId(), projectDto.nameProject(), projectDto.descriptions())) {
                return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDto("Project was updated successfully", true));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto("Invalid Project data", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto("Error updating project: " + e.getMessage(), false));
        }
    }

    @DeleteMapping("/deleteProject/{projectID}")
    @PreAuthorize("hasAuthority('delete_project')")
    @Operation(summary = "delete a project", description = "delete a project in the system")
    public ResponseEntity<ApiResponseDto> deleteProject(@PathVariable int projectID){
        try{
            if (proyectService.deleteProject(projectID)) {
                return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDto("Project was deleted successfully", true));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto("Invalid Project data", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto("Error deleting project: " + e.getMessage(), false));
        }
    }

    @PatchMapping("/invalidateProject/{projectID}")
    @Operation(summary = "invalidate a project", description = "made a logic delete of the project in the system")
    public ResponseEntity<?> invalidateProject(@PathVariable Integer projectID){
        try{
            if (proyectService.invalidateProject(projectID)) {
                return ResponseEntity.status(HttpStatus.OK).body("Project was invalidated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Project data");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getProject/{id}")
    @Operation(summary =  "Get a Project", description = "Retrieves a project in the system")
    public ResponseEntity<Project> getProject(@PathVariable Integer id) {
        try {
            Project project = proyectService.getProject(id);
            if (project == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
