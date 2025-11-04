package com.udea.sistemas.innosistemas.repository;

import com.udea.sistemas.innosistemas.models.entity.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Integer> {
    Optional<Course> findByNameCourseIgnoreCase(String nameCourse);
    boolean existsByNameCourseIgnoreCase(String nameCourse);
}
