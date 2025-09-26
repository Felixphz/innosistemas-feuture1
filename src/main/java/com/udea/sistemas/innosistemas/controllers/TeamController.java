package com.udea.sistemas.innosistemas.controllers;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import com.udea.sistemas.innosistemas.service.TeamService;
import com.udea.sistemas.innosistemas.models.dto.TeamDto;
import com.udea.sistemas.innosistemas.models.dto.TeamShowDto;
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
    private final TeamService teamService;

    public TeamController(TeamRepository teamRepository, UsersTeamRepository usersTeamRepository, TeamService teamService) {
        this.teamRepository = teamRepository;
        this.usersTeamRepository = usersTeamRepository;
        this.teamService = teamService;
    }

    @GetMapping("/getAllTeam")
    @Operation(summary = "Get all teams", description = "Retrieves a list of all teams in the system")
    public ResponseEntity<List<TeamShowDto>> getAllTeams() {
        try {
            List<TeamShowDto> teamDtos = teamRepository.findAll().stream().map(team -> new TeamShowDto(
                    team.getIdTeam(),
                    team.getNameTeam(),
                    team.getProject().getId(),
                    team.getProject().getNameProject(),
                    team.getProject().getCourseId(),
                    team.getMembers().stream().map(member -> new UserDto(
                            member.getUser().getEmail(),
                            member.getUser().getNameUser()
                    )).toList()
            )).toList();
            return ResponseEntity.ok(teamDtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
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

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable int id) {
            if(teamService.deleteTeam(id)){
                return ResponseEntity.ok().build();
            }

        return ResponseEntity.notFound().build();

    }

    @PutMapping("update")
    public ResponseEntity<?> updateTeam(@RequestBody TeamShowDto teamDto) {
        if(teamService.updateTeam(teamDto.idTeam(), teamDto)){
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }
    
    }


