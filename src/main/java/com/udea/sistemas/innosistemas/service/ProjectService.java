package com.udea.sistemas.innosistemas.service;
import java.util.List;
import java.util.Optional;

import com.udea.sistemas.innosistemas.models.entity.Project;
import com.udea.sistemas.innosistemas.repository.ProjectRepository;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import org.springframework.stereotype.Service;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.repository.UserRepository;


@Service
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ProjectService(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public List<User> getUsersInOneTeamByProject(Integer projectId) {
        List<User> users = userRepository.findAllBy();

        return users.stream()
                .filter(user -> {
                    long teamsInProject = user.getUserTeams().stream()
                            .filter(ut -> ut.getTeam().getProject().getId().equals(projectId))
                            .map(UsersTeam::getTeam)
                            .distinct()
                            .count();
                    return teamsInProject ==1;
                })
                .toList();
    }

    public boolean createProject(Integer courseId, String nameProject, String descriptions) {
        Project project = new Project();

        try {
            List<Project> projectsInCourse = projectRepository.findByCourseId(courseId);

            for (Project projectInCourse : projectsInCourse) {
                if (projectInCourse.getNameProject().equals(nameProject)) {
                    System.err.println("Ya existe un proyecto con ese nombre en el curso " + courseId);
                    return false;
                }
            }

            project.setCourseId(courseId);
            project.setNameProject(nameProject);
            project.setDescriptions(descriptions);
            project.setIs_deleted(false);

            project = projectRepository.save(project);

            return true;
        } catch (Exception e) {

            System.err.println("Error al crear el equipo: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProject(Integer projectId, Integer courseId, String nameProject, String descriptions){
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el proyecto con id " + projectId));

            project.setCourseId(courseId);
            project.setNameProject(nameProject);
            project.setDescriptions(descriptions);

            project = projectRepository.save(project);

            return true;
        } catch (Exception e) {

            System.err.println("Error al modificar el proyecto: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProject(Integer projectId){
        try {
            projectRepository.deleteById(projectId);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar el proyecto: " + e.getMessage());
            return false;
        }
    }

    public boolean invalidateProject(Integer projectId){
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el proyecto con id " + projectId));

            project.setIs_deleted(true);

            project = projectRepository.save(project);
            return true;
        } catch (Exception e) {

            System.err.println("Error al invalidar el proyecto: " + e.getMessage());
            return false;
        }
    }

    public boolean existProject(Integer projectId){
        try {
            Optional<Project> project = projectRepository.findById(projectId);
            return project.isPresent();
        }catch (Exception e) {

            System.err.println("Error al buscar el proyecto el proyecto: " + e.getMessage());
            return false;
        }
    }

    public Project getProject(Integer projectID){
        try {
            if(existProject(projectID)) {
                Project project = projectRepository.findById(projectID)
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró el proyecto con id " + projectID));

                return project;
            }
            return null;
        }catch (Exception e) {

            System.err.println("Error al buscar el proyecto el proyecto: " + e.getMessage());
            return null;
        }


    }


}

