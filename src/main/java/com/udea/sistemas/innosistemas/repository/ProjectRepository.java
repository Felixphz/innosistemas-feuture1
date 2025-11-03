package com.udea.sistemas.innosistemas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.udea.sistemas.innosistemas.models.entity.Project;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByCourseId(Integer courseId);

}
