package com.udea.sistemas.innosistemas.service;

import com.udea.sistemas.innosistemas.models.entity.*;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import org.springframework.stereotype.Service;
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UsersTeamRepository usersTeamRepository;

    public TeamService(TeamRepository teamRepository, UsersTeamRepository usersTeamRepository) {
        this.teamRepository = teamRepository;
        this.usersTeamRepository = usersTeamRepository;
    }

    public Team editTeamName(Integer teamId, String newName){
        Team teamToUpdate = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("grupo no encontrado con id " + teamId));

        teamToUpdate.setNameTeam(newName);

        teamToUpdate = teamRepository.save(teamToUpdate);

        return teamToUpdate;
    }

    public UsersTeam addStudent(Integer teamId, Integer studentID, String studentEmail){
        UsersTeamId usersTeamId = new UsersTeamId();

        usersTeamId.setTeamId(teamId);
        usersTeamId.setUserId(studentID);

        UsersTeam newUsersTeam = new UsersTeam();

        newUsersTeam.setId(usersTeamId);

        newUsersTeam = usersTeamRepository.save(newUsersTeam);

        return newUsersTeam;
    }

    public void removeStudent(Integer teamId, Integer studentID, String studentEmail){
        UsersTeamId usersTeamId = new UsersTeamId();

        usersTeamId.setTeamId(teamId);
        usersTeamId.setUserId(studentID);

        UsersTeam newUsersTeam = new UsersTeam();

        newUsersTeam.setId(usersTeamId);

        usersTeamRepository.delete(newUsersTeam);
    }
}
