package com.udea.sistemas.innosistemas.controllers;
import com.udea.sistemas.innosistemas.models.dto.UserDto;
import com.udea.sistemas.innosistemas.models.dto.CreateUserDto;
import com.udea.sistemas.innosistemas.repository.UserRepository;
import com.udea.sistemas.innosistemas.service.UserService;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing Users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }
    
   @GetMapping("/getAllUsers")
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> userDtos = userRepository.findAll().stream()
                    .map(user -> new UserDto(user.getEmail(), user.getNameUser()))
                    .toList();
            if (userDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/getStudents")
    @Operation(summary = "Get all students", description = "Retrieves a list of all users with student role")
    public ResponseEntity<List<UserDto>> getAllStudents() {
        try {
            List<UserDto> userDtos = userRepository.findByRole_NameRol("Estudiante").stream()
                    .map(user -> new UserDto(user.getEmail(), user.getNameUser()))
                    .toList();
            if (userDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/createUser")
    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    public ResponseEntity<String> createUser(@RequestBody CreateUserDto createUserDto) {
        try {
            if(userService.createUser(createUserDto)) {
                return ResponseEntity.status(HttpStatus.OK).body("User created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}

