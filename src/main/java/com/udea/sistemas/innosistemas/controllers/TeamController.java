package com.udea.sistemas.innosistemas.controllers;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.udea.sistemas.innosistemas.service.TeamService;
import com.udea.sistemas.innosistemas.models.dto.TeamDto;
import com.udea.sistemas.innosistemas.models.dto.TeamShowDto;
import com.udea.sistemas.innosistemas.models.dto.UserDto;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "http://localhost:3004")
@RestController
@RequestMapping("/api/team")
@Tag(name = "Teams", description = "Endpoints for managing Teams")
public class TeamController {
    private final TeamRepository teamRepository;
    private final TeamService teamService;

    public TeamController(TeamRepository teamRepository, TeamService teamService) {
        this.teamRepository = teamRepository;
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
            if (teamService.createTeam(teamDto)) {
                return ResponseEntity.status(HttpStatus.OK).body("Team was create successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid team data");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("deleteTeam/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable int id) {
            if(teamService.deleteTeam(id)){
                return ResponseEntity.ok().build();
            }

        return ResponseEntity.notFound().build();

    }

    @PutMapping("updateTeam")
    public ResponseEntity<?> updateTeam(@RequestBody TeamShowDto teamDto) {
        if(teamService.updateTeam(teamDto.idTeam(), teamDto)){
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }
    
    }


