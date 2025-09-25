package com.udea.sistemas.innosistemas.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.repository.UserRepository;


@Service
public class ProjectService {
    private final UserRepository userRepository;

    public ProjectService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}

