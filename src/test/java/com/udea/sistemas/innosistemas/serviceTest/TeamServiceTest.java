package com.udea.sistemas.innosistemas.serviceTest;

import com.udea.sistemas.innosistemas.models.dto.TeamDto;
import com.udea.sistemas.innosistemas.models.dto.TeamShowDto;
import com.udea.sistemas.innosistemas.models.dto.UserDto;
import com.udea.sistemas.innosistemas.models.entity.*;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import com.udea.sistemas.innosistemas.service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// --- Imports añadidos para la corrección ---
import java.util.HashSet;
// --- Fin Imports ---

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UsersTeamRepository usersTeamRepository;

    @InjectMocks
    private TeamService teamService;

    // --- Pruebas para createTeam ---

    @Test
    void testCreateTeam_Success() {
        // Arrange
        UserDto student1 = new UserDto("student1@example.com", "Student One");
        UserDto student2 = new UserDto("student2@example.com", "Student Two");
        TeamDto teamDto = new TeamDto("Equipo Alfa", 1, "Proyecto 1", 101, List.of(student1, student2));

        Team teamToSave = new Team();
        teamToSave.setNameTeam(teamDto.nameTeam());

        Team savedTeam = new Team();
        savedTeam.setIdTeam(1); // Simula el ID generado por la BD
        savedTeam.setNameTeam(teamDto.nameTeam());

        // Simula la primera llamada a 'save' (para crear el equipo y obtener ID)
        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);
        // Simula la llamada a 'save' para cada miembro
        when(usersTeamRepository.save(any(UsersTeam.class))).thenReturn(new UsersTeam());

        // Act
        boolean result = teamService.createTeam(teamDto);

        // Assert
        assertTrue(result);

        // Verifica que teamRepository.save() se llamó 2 veces (1ro para crear, 2do al final)
        verify(teamRepository, times(2)).save(any(Team.class));
        // Verifica que usersTeamRepository.save() se llamó 2 veces (1 por cada estudiante)
        verify(usersTeamRepository, times(2)).save(any(UsersTeam.class));
    }

    @Test
    void testCreateTeam_Fail_EmptyName() {
        // Arrange
        UserDto student1 = new UserDto("student1@example.com", "Student One");
        UserDto student2 = new UserDto("student2@example.com", "Student Two");
        // Nombre vacío
        TeamDto teamDto = new TeamDto("", 1, "Proyecto 1", 101, List.of(student1, student2));

        // Act
        boolean result = teamService.createTeam(teamDto);

        // Assert
        assertFalse(result);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testCreateTeam_Fail_NullProjectId() {
        // Arrange
        UserDto student1 = new UserDto("student1@example.com", "Student One");
        UserDto student2 = new UserDto("student2@example.com", "Student Two");
        // Project ID nulo
        TeamDto teamDto = new TeamDto("Equipo Beta", null, "Proyecto 1", 101, List.of(student1, student2));

        // Act
        boolean result = teamService.createTeam(teamDto);

        // Assert
        assertFalse(result);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testCreateTeam_Fail_TooFewStudents() {
        // Arrange
        UserDto student1 = new UserDto("student1@example.com", "Student One");
        // Solo 1 estudiante
        TeamDto teamDto = new TeamDto("Equipo Gamma", 1, "Proyecto 1", 101, List.of(student1));

        // Act
        boolean result = teamService.createTeam(teamDto);

        // Assert
        assertFalse(result);
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testCreateTeam_Fail_TooManyStudents() {
        // Arrange
        UserDto student1 = new UserDto("student1@example.com", "Student One");
        UserDto student2 = new UserDto("student2@example.com", "Student Two");
        UserDto student3 = new UserDto("student3@example.com", "Student Three");
        UserDto student4 = new UserDto("student4@example.com", "Student Four");
        // 4 estudiantes
        TeamDto teamDto = new TeamDto("Equipo Delta", 1, "Proyecto 1", 101, List.of(student1, student2, student3, student4));

        // Act
        boolean result = teamService.createTeam(teamDto);

        // Assert
        assertFalse(result);
        verify(teamRepository, never()).save(any(Team.class));
    }

    // --- Pruebas para deleteTeam ---

    @Test
    void testDeleteTeam_Success() {
        // Arrange
        int teamId = 1;
        Team existingTeam = new Team();
        existingTeam.setIdTeam(teamId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        doNothing().when(usersTeamRepository).deleteByTeam_IdTeam(teamId);
        doNothing().when(teamRepository).deleteById(teamId);

        // Act
        boolean result = teamService.deleteTeam(teamId);

        // Assert
        assertTrue(result);
        verify(teamRepository, times(1)).findById(teamId);
        verify(usersTeamRepository, times(1)).deleteByTeam_IdTeam(teamId);
        verify(teamRepository, times(1)).deleteById(teamId);
    }

    @Test
    void testDeleteTeam_Fail_NotFound() {
        // Arrange
        int teamId = 99;

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act
        boolean result = teamService.deleteTeam(teamId);

        // Assert
        assertFalse(result);
        verify(teamRepository, times(1)).findById(teamId);
        verify(usersTeamRepository, never()).deleteByTeam_IdTeam(anyInt());
        verify(teamRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteTeam_Fail_ExceptionOnDelete() {
        // Arrange
        int teamId = 1;
        Team existingTeam = new Team();
        existingTeam.setIdTeam(teamId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        // Simula un error de BD al borrar
        doThrow(new RuntimeException("Error de BD")).when(usersTeamRepository).deleteByTeam_IdTeam(teamId);

        // Act
        boolean result = teamService.deleteTeam(teamId);

        // Assert
        assertFalse(result); // El método captura la excepción y retorna false
        verify(teamRepository, times(1)).findById(teamId);
        verify(usersTeamRepository, times(1)).deleteByTeam_IdTeam(teamId);
        verify(teamRepository, never()).deleteById(anyInt()); // No debe llegar a borrar el equipo
    }


    // --- Pruebas para updateTeam ---

    @Test
    void testUpdateTeam_Success() {
        // Arrange
        int teamId = 1;
        UserDto student1 = new UserDto("new1@example.com", "New One");
        UserDto student2 = new UserDto("new2@example.com", "New Two");
        TeamShowDto teamDto = new TeamShowDto(teamId, "Equipo Actualizado", 1, "Proyecto 1", 101, List.of(student1, student2));

        Team existingTeam = new Team();
        existingTeam.setIdTeam(teamId);
        existingTeam.setNameTeam("Equipo Viejo");

        // --- CORRECCIÓN AQUÍ ---
        // Usamos un HashSet (mutable) en lugar de Set.of() (inmutable)
        // Esto evita la UnsupportedOperationException cuando el servicio llama a .clear()
        existingTeam.setMembers(new HashSet<>());
        // --- FIN DE LA CORRECCIÓN ---

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        doNothing().when(usersTeamRepository).deleteByTeam_IdTeam(teamId);
        when(usersTeamRepository.save(any(UsersTeam.class))).thenReturn(new UsersTeam());
        when(teamRepository.save(any(Team.class))).thenReturn(existingTeam);

        // Act
        boolean result = teamService.updateTeam(teamId, teamDto);

        // Assert
        assertTrue(result); // Esta línea [222] ahora debería pasar
        verify(teamRepository, times(1)).findById(teamId);
        verify(usersTeamRepository, times(1)).deleteByTeam_IdTeam(teamId);
        verify(usersTeamRepository, times(2)).save(any(UsersTeam.class)); // 2 nuevos estudiantes
        verify(teamRepository, times(1)).save(any(Team.class));

        // Verifica que el nombre del equipo se actualizó
        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(teamCaptor.capture());
        assertEquals("Equipo Actualizado", teamCaptor.getValue().getNameTeam());
    }

    @Test
    void testUpdateTeam_Fail_NotFound() {
        // Arrange
        int teamId = 99;
        UserDto student1 = new UserDto("new1@example.com", "New One");
        UserDto student2 = new UserDto("new2@example.com", "New Two");
        TeamShowDto teamDto = new TeamShowDto(teamId, "Equipo No Encontrado", 1, "Proyecto 1", 101, List.of(student1, student2));

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Act
        boolean result = teamService.updateTeam(teamId, teamDto);

        // Assert
        assertFalse(result);
        verify(teamRepository, times(1)).findById(teamId);
        verify(usersTeamRepository, never()).deleteByTeam_IdTeam(anyInt());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void testUpdateTeam_Fail_InvalidStudentCount() {
        // Arrange
        int teamId = 1;
        UserDto student1 = new UserDto("new1@example.com", "New One");
        // Solo 1 estudiante
        TeamShowDto teamDto = new TeamShowDto(teamId, "Equipo Inválido", 1, "Proyecto 1", 101, List.of(student1));

        // Act
        boolean result = teamService.updateTeam(teamId, teamDto);

        // Assert
        assertFalse(result);
        verify(teamRepository, never()).findById(anyInt()); // Ni siquiera debe buscar
    }

    @Test
    void testUpdateTeam_Fail_InvalidDto() {
        // Arrange
        int teamId = 1;
        // DTO nulo
        boolean resultNull = teamService.updateTeam(teamId, null);

        // DTO con nombre nulo
        UserDto student1 = new UserDto("new1@example.com", "New One");
        UserDto student2 = new UserDto("new2@example.com", "New Two");
        TeamShowDto teamDtoNameNull = new TeamShowDto(teamId, null, 1, "Proyecto 1", 101, List.of(student1, student2));
        boolean resultNameNull = teamService.updateTeam(teamId, teamDtoNameNull);

        // Assert
        assertFalse(resultNull);
        assertFalse(resultNameNull);
        verify(teamRepository, never()).findById(anyInt());
    }
}