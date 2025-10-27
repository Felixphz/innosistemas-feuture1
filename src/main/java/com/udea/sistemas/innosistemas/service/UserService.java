package com.udea.sistemas.innosistemas.service;
import org.springframework.stereotype.Service;
import com.udea.sistemas.innosistemas.repository.UserRepository;
import com.udea.sistemas.innosistemas.models.dto.CreateUserDto;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.models.entity.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

  public final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder; 
  }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

  
  public boolean createUser(CreateUserDto user) {
    try {
      if (existsByEmail(user.email()) || user.email().isEmpty()) {
        System.err.println("User with email " + user.email() + " already exists.");
        return false;
      }
      User newUser = new User();
      Role role = new Role();
      role.setId(2); // Default role ID
      newUser.setEmail(user.email());
      newUser.setNameUser(user.nameUser());
      newUser.setPassword(passwordEncoder.encode(user.password()));
      newUser.setRole(role);
      userRepository.save(newUser);
      return true;

    } catch (Exception e) {
      System.err.println("Error creating user: " + e.getMessage());
      return false;
    }
  }
}
