package com.udea.sistemas.innosistemas.controllers;

import com.udea.sistemas.innosistemas.models.dto.CourseDto;
import com.udea.sistemas.innosistemas.models.entity.Course;
import com.udea.sistemas.innosistemas.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "API para la gestión de cursos")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Obtener todos los cursos")
    @PreAuthorize("hasAuthority('read_courses')")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @Operation(summary = "Obtener curso por ID")
    @PreAuthorize("hasAuthority('read_courses')")
    @ApiResponse(responseCode = "200", description = "Curso encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable int id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(summary = "Crear un nuevo curso")
    @PreAuthorize("hasAuthority('create_courses')")
    @ApiResponse(responseCode = "201", description = "Curso creado exitosamente")
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@RequestBody String name) {
        Course created = courseService.createCourse(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CourseDto(created.getId(), created.getNameCourse()));
    }

    @Operation(summary = "Actualizar un curso existente")
    @PreAuthorize("hasAuthority('update_courses')")
    @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente")
    @PutMapping("/update")
    public ResponseEntity<CourseDto> updateCourse( @RequestBody CourseDto dto) {
        Course updated = courseService.updateCourse(dto);
        return ResponseEntity.ok(new CourseDto(updated.getId(), updated.getNameCourse()));
    }

    @Operation(summary = "Eliminar un curso (borrado lógico)")
    @PreAuthorize("hasAuthority('delete_courses')")
    @ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        courseService.deleteCourseById(id);
        return ResponseEntity.noContent().build();
    }
}
