package com.udea.sistemas.innosistemas.service;

import com.udea.sistemas.innosistemas.exceptions.CourseAlreadyExistsException;
import com.udea.sistemas.innosistemas.exceptions.CourseNotFoundException;
import com.udea.sistemas.innosistemas.exceptions.InvalidCourseDataException;
import com.udea.sistemas.innosistemas.models.dto.CourseDto;
import com.udea.sistemas.innosistemas.models.entity.Course;
import com.udea.sistemas.innosistemas.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDto> getAllCourses() {
        Iterable<Course> courses = courseRepository.findAll();

        return StreamSupport.stream(courses.spliterator(), false)
                .filter(course -> !course.getIsDeleted())
                .map(course -> new CourseDto(course.getId(), course.getNameCourse()))
                .toList();
    }

    public Course createCourse(String name) {
        String cleanName = name == null ? "" : name.trim();

        if (cleanName.isEmpty()) {
            throw new InvalidCourseDataException("El nombre del curso no puede estar vacío");
        }

        if (courseRepository.existsByNameCourseIgnoreCase(cleanName)) {
            throw new CourseAlreadyExistsException(cleanName);
        }

        Course course = Course.builder()
                .nameCourse(cleanName)
                .isDeleted(false)
                .build();

        return courseRepository.save(course);
    }

    public Course updateCourse(CourseDto courseDto) {
        if (courseDto == null) {
            throw new InvalidCourseDataException("Los datos del curso son inválidos");
        }

        Course course = courseRepository.findById(courseDto.idCourse())
                .orElseThrow(() -> new CourseNotFoundException(courseDto.idCourse()));

        String newName = courseDto.nameCourse().trim();

        if (newName.isEmpty()) {
            throw new InvalidCourseDataException("El nombre del curso no puede estar vacío");
        }

        if (!course.getNameCourse().equalsIgnoreCase(newName)
                && courseRepository.existsByNameCourseIgnoreCase(newName)) {
            throw new CourseAlreadyExistsException(newName);
        }

        course.setNameCourse(newName);
        return courseRepository.save(course);
    }

    public void deleteCourseById(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        if (course.getIsDeleted()) {
            throw new InvalidCourseDataException("El curso con ID " + id + " ya está eliminado");
        }

        course.setIsDeleted(true);
        courseRepository.save(course);
    }

    public CourseDto getCourseById(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        if (course.getIsDeleted()) {
            throw new InvalidCourseDataException("El curso con ID " + id + " está eliminado");
        }

        return new CourseDto(course.getId(), course.getNameCourse());
    }
}
