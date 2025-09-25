package com.udea.sistemas.innosistemas.controllers;

import com.udea.sistemas.innosistemas.models.entity.Team;
import com.udea.sistemas.innosistemas.repository.UserRepository;
import com.udea.sistemas.innosistemas.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.udea.sistemas.innosistemas.repository.TeamRepository;

@RestController
@RequestMapping(value = "/teams")
public class TeamController {

    private final TeamRepository teamRepository;
    private final TeamService teamService;

    public TeamController(TeamRepository teamRepository, TeamService teamService) {
        this.teamRepository = teamRepository;
        this.teamService = teamService;
    }

    @PatchMapping("/modify/{id}/{newName}")
    public ResponseEntity<Team> updateTeamName(@PathVariable Integer id, @PathVariable String newName) {
        Team teamToUpdate = teamService.editTeamName(id, newName);

        return ResponseEntity.ok(teamToUpdate);
    }

}
