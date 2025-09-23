package com.udea.sistemas.innosistemas.controllers;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing Users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /users -> devuelve todos los usuarios
    
   @GetMapping("/getAllUsers")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            // Agregar logging más detallado
            e.printStackTrace();
            System.out.println("Error específico: " + e.getMessage());
            // Si hay una causa raíz, también la imprimimos
            if (e.getCause() != null) {
                System.out.println("Causa raíz: " + e.getCause().getMessage());
            }
            return ResponseEntity.internalServerError().build();
        }
    }
}
