package com.udea.sistemas.innosistemas.serviceTest;

import com.udea.sistemas.innosistemas.models.entity.Course;
import com.udea.sistemas.innosistemas.repository.CourseRepository;
import com.udea.sistemas.innosistemas.service.CourseService;
import com.udea.sistemas.innosistemas.models.dto.CourseDto;

// --- Imports añadidos ---
import com.udea.sistemas.innosistemas.exceptions.CourseAlreadyExistsException;
import com.udea.sistemas.innosistemas.exceptions.CourseNotFoundException;
import com.udea.sistemas.innosistemas.exceptions.InvalidCourseDataException;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
// --- Fin Imports añadidos ---

// Volvemos a usar Mockito puro (como tu amigo)
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // ¡Sin @SpringBootTest!
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void testGetAllCourses() {
        // (El código de la prueba no cambia)
        Course cursoFalso = new Course();
        cursoFalso.setId(1);
        cursoFalso.setNameCourse("Curso de Prueba");
        cursoFalso.setIsDeleted(false);
        when(courseRepository.findAll()).thenReturn(Collections.singletonList(cursoFalso));
        List<CourseDto> resultados = courseService.getAllCourses();
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testCreateCourse() {
        // (El código de la prueba no cambia)
        String nombreCurso = "Nuevo Curso";
        Course cursoGuardado = new Course();
        cursoGuardado.setId(1);
        cursoGuardado.setNameCourse(nombreCurso);
        cursoGuardado.setIsDeleted(false);
        when(courseRepository.save(any(Course.class))).thenReturn(cursoGuardado);
        when(courseRepository.existsByNameCourseIgnoreCase(nombreCurso)).thenReturn(false);
        Course resultado = courseService.createCourse(nombreCurso);
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
    }

    // --- NUEVAS PRUEBAS AÑADIDAS ---

    // --- Pruebas para getCourseById ---

    @Test
    void testGetCourseById_Success() {
        int courseId = 1;
        Course cursoExistente = new Course(courseId, "Curso Encontrado", false);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(cursoExistente));

        CourseDto result = courseService.getCourseById(courseId);

        assertNotNull(result);
        assertEquals(courseId, result.idCourse());
        assertEquals("Curso Encontrado", result.nameCourse());
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void testGetCourseById_NotFound() {
        int courseId = 99;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> {
            courseService.getCourseById(courseId);
        });
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void testGetCourseById_IsDeleted() {
        int courseId = 2;
        Course cursoBorrado = new Course(courseId, "Curso Borrado", true);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(cursoBorrado));

        // El servicio debe lanzar una excepción si el curso está borrado lógicamente
        assertThrows(InvalidCourseDataException.class, () -> {
            courseService.getCourseById(courseId);
        });
        verify(courseRepository, times(1)).findById(courseId);
    }

    // --- Pruebas para updateCourse ---

    @Test
    void testUpdateCourse_Success() {
        CourseDto dto = new CourseDto(1, "Curso Actualizado");
        Course cursoExistente = new Course(1, "Curso Viejo", false);
        Course cursoGuardado = new Course(1, "Curso Actualizado", false);

        when(courseRepository.findById(1)).thenReturn(Optional.of(cursoExistente));
        when(courseRepository.existsByNameCourseIgnoreCase("Curso Actualizado")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(cursoGuardado);

        Course result = courseService.updateCourse(dto);

        assertNotNull(result);
        assertEquals("Curso Actualizado", result.getNameCourse());

        // Verificamos que el nombre en el objeto guardado sea el correcto
        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(courseCaptor.capture());
        assertEquals("Curso Actualizado", courseCaptor.getValue().getNameCourse());
    }

    @Test
    void testUpdateCourse_NameAlreadyExists() {
        CourseDto dto = new CourseDto(1, "Nombre Repetido");
        Course cursoExistente = new Course(1, "Curso Viejo", false);

        when(courseRepository.findById(1)).thenReturn(Optional.of(cursoExistente));
        // Simula que el nombre ya existe en la BD
        when(courseRepository.existsByNameCourseIgnoreCase("Nombre Repetido")).thenReturn(true);

        assertThrows(CourseAlreadyExistsException.class, () -> {
            courseService.updateCourse(dto);
        });

        verify(courseRepository, never()).save(any(Course.class)); // No debe guardar nada
    }

    @Test
    void testUpdateCourse_NotFound() {
        CourseDto dto = new CourseDto(99, "Curso No Encontrado");
        when(courseRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> {
            courseService.updateCourse(dto);
        });
        verify(courseRepository, never()).save(any(Course.class));
    }


    // --- Pruebas para deleteCourseById ---

    @Test
    void testDeleteCourseById_Success() {
        // 1. Preparación (Arrange)
        int courseId = 1;
        Course cursoExistente = new Course();
        cursoExistente.setId(courseId);
        cursoExistente.setNameCourse("Curso a Borrar");
        cursoExistente.setIsDeleted(false);

        // Simula que el repositorio SÍ encuentra el curso
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(cursoExistente));

        // 2. Actuación (Act)
        // Ejecuta el método y verifica que NO lanza una excepción
        assertDoesNotThrow(() -> {
            courseService.deleteCourseById(courseId);
        });

        // 3. Verificación (Assert)
        // Verifica que el método findById fue llamado 1 vez
        verify(courseRepository, times(1)).findById(courseId);

        // Captura el objeto 'Course' que se pasó al método save()
        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository, times(1)).save(courseCaptor.capture());

        // Verifica que el curso guardado ahora tiene isDeleted = true
        Course cursoGuardado = courseCaptor.getValue();
        assertNotNull(cursoGuardado);
        assertTrue(cursoGuardado.getIsDeleted());
    }

    @Test
    void testDeleteCourseById_NotFound() {
        // 1. Preparación (Arrange)
        int courseId = 99; // ID que no existe

        // Simula que el repositorio NO encuentra el curso
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // 2. Actuación y 3. Verificación (Act & Assert)
        // Verifica que la ejecución del método lanza la excepción esperada
        assertThrows(CourseNotFoundException.class, () -> {
            courseService.deleteCourseById(courseId);
        });

        // Verifica que findById fue llamado, pero 'save' NUNCA fue llamado
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testDeleteCourseById_AlreadyDeleted() {
        // 1. Preparación (Arrange)
        int courseId = 2;
        Course cursoYaBorrado = new Course();
        cursoYaBorrado.setId(courseId);
        cursoYaBorrado.setNameCourse("Curso Ya Borrado");
        cursoYaBorrado.setIsDeleted(true); // El curso ya está borrado

        // Simula que el repositorio SÍ encuentra el curso
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(cursoYaBorrado));

        // 2. Actuación y 3. Verificación (Act & Assert)
        // Verifica que la ejecución del método lanza la excepción esperada
        assertThrows(InvalidCourseDataException.class, () -> {
            courseService.deleteCourseById(courseId);
        });

        // Verifica que findById fue llamado, pero 'save' NUNCA fue llamado
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }
}