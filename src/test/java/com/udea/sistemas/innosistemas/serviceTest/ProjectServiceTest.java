package com.udea.sistemas.innosistemas.serviceTest;

import com.udea.sistemas.innosistemas.models.entity.Project;
import com.udea.sistemas.innosistemas.models.entity.Team;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.service.ProjectService;
import com.udea.sistemas.innosistemas.repository.ProjectRepository;
import com.udea.sistemas.innosistemas.repository.UserRepository;

// Imports añadidos
import java.util.Collections;
import java.util.List;
import org.mockito.ArgumentCaptor;

// Volvemos a usar Mockito puro
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // ¡Sin @SpringBootTest!
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    // --- Tu prueba original (corregida para verificar 2 llamadas) ---
    @Test
    void testGetProject() {
        // (El código de la prueba no cambia)
        Project proyectoFalso = new Project();
        proyectoFalso.setId(100);
        proyectoFalso.setNameProject("Proyecto de Prueba");

        // Simula las dos llamadas a findById que hace el método getProject
        // (una para existProject y otra para el findById principal)
        when(projectRepository.findById(100)).thenReturn(Optional.of(proyectoFalso));

        Project resultado = projectService.getProject(100);

        assertNotNull(resultado);
        assertEquals(100, resultado.getId());

        // Verificamos las 2 llamadas
        verify(projectRepository, times(2)).findById(100);
    }

    // --- NUEVAS PRUEBAS AÑADIDAS ---

    @Test
    void testGetProject_NotFound() {
        int projectId = 99;
        // Simula que el proyecto no se encuentra
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Project resultado = projectService.getProject(projectId);

        assertNull(resultado);
        // Solo debe llamarlo 1 vez (en existProject) y luego detenerse
        verify(projectRepository, times(1)).findById(projectId);
    }

    // --- Pruebas para createProject ---

    @Test
    void testCreateProject_Success() {
        Integer courseId = 1;
        String projectName = "Nuevo Proyecto";
        String description = "Descripción";

        // Simula que no hay proyectos existentes con ese nombre en ese curso
        when(projectRepository.findByCourseId(courseId)).thenReturn(Collections.emptyList());

        Project projectToSave = new Project();
        projectToSave.setId(1); // Simula el ID generado
        when(projectRepository.save(any(Project.class))).thenReturn(projectToSave);

        boolean result = projectService.createProject(courseId, projectName, description);

        assertTrue(result);

        // Verifica que se guardó el proyecto correcto
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        assertEquals(projectName, projectCaptor.getValue().getNameProject());
        assertFalse(projectCaptor.getValue().getIs_deleted()); // Asegura que se crea como NO borrado
    }

    @Test
    void testCreateProject_Fail_AlreadyExists() {
        Integer courseId = 1;
        String projectName = "Proyecto Repetido";
        String description = "Descripción";

        Project existingProject = new Project();
        existingProject.setNameProject(projectName); // Mismo nombre

        // Simula que SÍ existe un proyecto con ese nombre
        when(projectRepository.findByCourseId(courseId)).thenReturn(List.of(existingProject));

        boolean result = projectService.createProject(courseId, projectName, description);

        assertFalse(result);
        verify(projectRepository, never()).save(any(Project.class)); // No debe guardar nada
    }

    @Test
    void testCreateProject_Fail_Exception() {
        Integer courseId = 1;
        String projectName = "Proyecto Error";
        String description = "Descripción";

        when(projectRepository.findByCourseId(courseId)).thenReturn(Collections.emptyList());
        // Simula error de BD al guardar
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Error de BD"));

        boolean result = projectService.createProject(courseId, projectName, description);

        assertFalse(result); // El método debe capturar la excepción y devolver false
    }

    // --- Pruebas para updateProject ---

    @Test
    void testUpdateProject_Success() {
        Integer projectId = 1;
        Integer courseId = 10;
        String newName = "Nombre Actualizado";
        String newDesc = "Desc Actualizada";

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setNameProject("Nombre Viejo");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        boolean result = projectService.updateProject(projectId, courseId, newName, newDesc);

        assertTrue(result);

        // Verifica que el objeto guardado tiene los datos nuevos
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        assertEquals(newName, projectCaptor.getValue().getNameProject());
        assertEquals(newDesc, projectCaptor.getValue().getDescriptions());
        assertEquals(courseId, projectCaptor.getValue().getCourseId());
    }

    @Test
    void testUpdateProject_Fail_NotFound() {
        Integer projectId = 99; // No existe
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        boolean result = projectService.updateProject(projectId, 10, "Test", "Test");

        assertFalse(result); // El método captura la excepción y devuelve false
        verify(projectRepository, never()).save(any(Project.class));
    }

    // --- Pruebas para deleteProject ---

    @Test
    void testDeleteProject_Success() {
        Integer projectId = 1;
        doNothing().when(projectRepository).deleteById(projectId);

        boolean result = projectService.deleteProject(projectId);

        assertTrue(result);
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    void testDeleteProject_Fail_Exception() {
        Integer projectId = 1;
        // Simula error (ej. restricción de llave foránea)
        doThrow(new RuntimeException("Error de BD")).when(projectRepository).deleteById(projectId);

        boolean result = projectService.deleteProject(projectId);

        assertFalse(result);
    }

    // --- Pruebas para invalidateProject ---

    @Test
    void testInvalidateProject_Success() {
        Integer projectId = 1;
        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setIs_deleted(false); // Está activo

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        boolean result = projectService.invalidateProject(projectId);

        assertTrue(result);

        // Verifica que el proyecto se guardó como "borrado"
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        assertTrue(projectCaptor.getValue().getIs_deleted());
    }

    @Test
    void testInvalidateProject_Fail_NotFound() {
        Integer projectId = 99; // No existe
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        boolean result = projectService.invalidateProject(projectId);

        assertFalse(result);
        verify(projectRepository, never()).save(any(Project.class));
    }

    // --- Pruebas para getUsersInOneTeamByProject ---

    @Test
    void testGetUsersInOneTeamByProject() {
        Integer targetProjectId = 1;
        Integer otherProjectId = 2;

        // Proyecto objetivo
        Project targetProject = new Project();
        targetProject.setId(targetProjectId);

        // Otro proyecto
        Project otherProject = new Project();
        otherProject.setId(otherProjectId);

        // --- CORRECCIÓN AQUÍ ---
        // Debemos hacer que team1 y team2 sean distintos
        // dándoles propiedades únicas (como un ID).

        // Equipo 1 (del proyecto objetivo)
        Team team1 = new Team();
        team1.setProject(targetProject);
        team1.setIdTeam(101); // ID único

        // Equipo 2 (del proyecto objetivo)
        Team team2 = new Team();
        team2.setProject(targetProject);
        team2.setIdTeam(102); // ID único

        // Equipo 3 (de OTRO proyecto)
        Team team3 = new Team();
        team3.setProject(otherProject);
        team3.setIdTeam(103);
        // --- FIN DE LA CORRECCIÓN ---

        // --- Configuración de Usuarios ---

        // Usuario 1: En 1 equipo del proyecto objetivo. DEBE APARECER.
        User user1 = new User();
        user1.setEmail("user1@test.com");
        UsersTeam ut1 = new UsersTeam();
        ut1.setTeam(team1);
        user1.setUserTeams(List.of(ut1));

        // Usuario 2: En 2 equipos del proyecto objetivo. NO debe aparecer.
        User user2 = new User();
        user2.setEmail("user2@test.com");
        UsersTeam ut2_1 = new UsersTeam();
        ut2_1.setTeam(team1);
        UsersTeam ut2_2 = new UsersTeam();
        ut2_2.setTeam(team2);
        user2.setUserTeams(List.of(ut2_1, ut2_2));

        // Usuario 3: En 1 equipo de OTRO proyecto. NO debe aparecer.
        User user3 = new User();
        user3.setEmail("user3@test.com");
        UsersTeam ut3 = new UsersTeam();
        ut3.setTeam(team3);
        user3.setUserTeams(List.of(ut3));

        // Usuario 4: Sin equipos. NO debe aparecer.
        User user4 = new User();
        user4.setEmail("user4@test.com");
        user4.setUserTeams(Collections.emptyList());

        // Simula la llamada a la BD
        when(userRepository.findAllBy()).thenReturn(List.of(user1, user2, user3, user4));

        // Act
        List<User> result = projectService.getUsersInOneTeamByProject(targetProjectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Esta línea (292) ahora debe pasar
        assertTrue(result.contains(user1));
        assertFalse(result.contains(user2)); // Verificamos que user2 NO está
        assertFalse(result.contains(user3));
        assertFalse(result.contains(user4));
    }
}