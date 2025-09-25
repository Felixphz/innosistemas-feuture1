package com.udea.sistemas.innosistemas.controllers;

import com.udea.sistemas.innosistemas.models.entity.Team;
import com.udea.sistemas.innosistemas.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.udea.sistemas.innosistemas.repository.TeamRepository;

@RestController
@RequestMapping(value = "/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @PatchMapping("/modify/{id}/{newName}")
    public ResponseEntity<Team> updateTeamName(@PathVariable  Integer id, @PathVariable String newName) {
        Team teamToUpdate = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + id));

        teamToUpdate.setNameTeam(newName);

        teamToUpdate = teamRepository.save(teamToUpdate);

        return ResponseEntity.ok(teamToUpdate);
    }




}
