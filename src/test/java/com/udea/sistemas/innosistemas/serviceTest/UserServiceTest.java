package com.udea.sistemas.innosistemas.serviceTest;

import com.udea.sistemas.innosistemas.models.dto.CreateUserDto;
import com.udea.sistemas.innosistemas.models.entity.Role;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.repository.UserRepository;
import com.udea.sistemas.innosistemas.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // --- Pruebas para existsByEmail ---

    @Test
    void testExistsByEmail_UserExists() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(new User()); // Devuelve un usuario (no nulo)

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testExistsByEmail_UserDoesNotExist() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null); // Devuelve nulo

        // Act
        boolean result = userService.existsByEmail(email);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    // --- Pruebas para createUser ---

    @Test
    void testCreateUser_Success() {
        // Arrange
        CreateUserDto userDto = new CreateUserDto("new@example.com", "Nuevo Usuario", "password123");
        String encodedPassword = "encodedPassword123";

        // Simula que el usuario NO existe
        when(userRepository.findByEmail(userDto.email())).thenReturn(null);
        // Simula la codificación de la contraseña
        when(passwordEncoder.encode(userDto.password())).thenReturn(encodedPassword);

        // Act
        boolean result = userService.createUser(userDto);

        // Assert
        assertTrue(result);

        // Verifica que se llamó a save() en el repositorio
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        // Verifica que el usuario guardado tiene los datos correctos
        User savedUser = userCaptor.getValue();
        assertEquals(userDto.email(), savedUser.getEmail());
        assertEquals(userDto.nameUser(), savedUser.getNameUser());
        assertEquals(encodedPassword, savedUser.getPassword()); // Verifica contraseña codificada
        assertEquals(2, savedUser.getRole().getId()); // Verifica rol por defecto
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        // Arrange
        CreateUserDto userDto = new CreateUserDto("existe@example.com", "Usuario Existente", "password123");

        // Simula que el usuario YA existe
        when(userRepository.findByEmail(userDto.email())).thenReturn(new User());

        // Act
        boolean result = userService.createUser(userDto);

        // Assert
        assertFalse(result);
        // Verifica que NUNCA se llamó a save()
        verify(userRepository, never()).save(any(User.class));
        // Verifica que NUNCA se llamó al passwordEncoder
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testCreateUser_EmptyEmail() {
        // Arrange
        CreateUserDto userDto = new CreateUserDto("", "Usuario Sin Email", "password123");

        // --- CORRECCIÓN AQUÍ ---
        // Tu código original SÍ llama a findByEmail(""), así que debemos simular esa llamada.
        // Asumimos que findByEmail("") devuelve null (no encuentra nada).
        when(userRepository.findByEmail("")).thenReturn(null);
        // --- FIN DE LA CORRECCIÓN ---

        // Act
        boolean result = userService.createUser(userDto);

        // Assert
        assertFalse(result); // El resultado debe ser 'false' porque el email está vacío

        // Verificamos que SÍ se llamó a findByEmail(con string vacío) 1 vez
        verify(userRepository, times(1)).findByEmail("");

        // Verificamos que NUNCA se llamó a 'save'
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_GeneralException() {
        // Arrange
        CreateUserDto userDto = new CreateUserDto("error@example.com", "Error User", "password123");

        when(userRepository.findByEmail(userDto.email())).thenReturn(null);
        when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPass");

        // Simula una excepción (ej. error de BD) al guardar
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Error de base de datos"));

        // Act
        boolean result = userService.createUser(userDto);

        // Assert
        assertFalse(result); // El método debe capturar la excepción y devolver false
        verify(userRepository, times(1)).save(any(User.class));
    }


    // --- Pruebas para deleteUser ---

    @Test
    void testDeleteUser_Success() {
        // Arrange
        String email = "delete@example.com";
        User userToDelete = new User();
        userToDelete.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(userToDelete);
        // Hacemos que doNothing() sea explícito, ya que deleteByEmail es void
        doNothing().when(userRepository).deleteByEmail(email);

        // Act
        boolean result = userService.deleteUser(email);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).deleteByEmail(email);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        String email = "notfound@example.com";

        // Simula que el usuario NO se encuentra
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act
        boolean result = userService.deleteUser(email);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(email);
        // Verifica que NUNCA se llamó a deleteByEmail
        verify(userRepository, never()).deleteByEmail(anyString());
    }

    @Test
    void testDeleteUser_GeneralException() {
        // Arrange
        String email = "error@example.com";
        User userToDelete = new User();
        userToDelete.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(userToDelete);

        // Simula una excepción (ej. error de BD) al eliminar
        doThrow(new RuntimeException("Error de base de datos")).when(userRepository).deleteByEmail(email);

        // Act
        boolean result = userService.deleteUser(email);

        // Assert
        assertFalse(result); // El método debe capturar la excepción y devolver false
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).deleteByEmail(email);
    }
}