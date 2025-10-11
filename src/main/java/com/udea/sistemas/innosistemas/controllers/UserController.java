package com.udea.sistemas.innosistemas.controllers;
import com.udea.sistemas.innosistemas.models.dto.UserDto;
import com.udea.sistemas.innosistemas.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3004")
@RestController
@RequestMapping("/api/user")
@Tag(name = "Users", description = "Endpoints for managing Users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @GetMapping("/getAllUsers")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userRepository.findAll().stream()
                .map(user -> new UserDto(user.getEmail(), user.getNameUser()))
                .collect(Collectors.toList());
            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/getStudents")
    @Operation(summary = "Get all students", description = "Retrieves a list of all users with student role")
    public ResponseEntity<List<UserDto>> getAllStudents() {
        try {
            List<UserDto> students = userRepository.findByRole_NameRol("Estudiante").stream()
                .map(user -> new UserDto(user.getEmail(), user.getNameUser()))
                .collect(Collectors.toList());
            if (students.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
