package com.udea.sistemas.innosistemas.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import com.udea.sistemas.innosistemas.models.dto.TeamDto;
import com.udea.sistemas.innosistemas.models.dto.UserDto;
import com.udea.sistemas.innosistemas.models.entity.Project;
import com.udea.sistemas.innosistemas.models.entity.Team;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.models.entity.UsersTeamId;
import com.udea.sistemas.innosistemas.repository.TeamRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/api/team")
@Tag(name = "Teams", description = "Endpoints for managing Teams")
public class TeamController {
    private final TeamRepository teamRepository;
    private final UsersTeamRepository usersTeamRepository;

    public TeamController(TeamRepository teamRepository, UsersTeamRepository usersTeamRepository) {
        this.teamRepository = teamRepository;
        this.usersTeamRepository = usersTeamRepository;
    }

    @PostMapping("/createTeam")
    @Operation(summary = "Create a new team", description = "Creates a new team in the system")
    public ResponseEntity<?> createTeam(@RequestBody TeamDto teamDto){
        try{
            Team team= new Team();
            Project proyect=new Project();
            proyect.setId(teamDto.projectId());
            proyect.setNameProject(teamDto.projectName());
            team.setNameTeam(teamDto.nameTeam());
            team.setProject(proyect);

            team=teamRepository.save(team);
            for (UserDto user : teamDto.students()) {
                UsersTeam member = new UsersTeam();
                User membUser = new User();
                membUser.setEmail(user.email());
                membUser.setNameUser(user.nameUser());

                UsersTeamId id = new UsersTeamId();
                id.setTeamId(team.getIdTeam());
                id.setEmail(user.email());

                member.setTeam(team);
                member.setUser(membUser);
                member.setId(id);

                member = usersTeamRepository.save(member);
                team.getMembers().add(member);
            }
            
            teamRepository.save(team);
            return ResponseEntity.status(HttpStatus.OK).body("Team was create successfully");
        } catch (Exception e){

         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
