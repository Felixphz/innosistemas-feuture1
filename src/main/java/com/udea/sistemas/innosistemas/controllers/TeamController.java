package com.udea.sistemas.innosistemas.controllers;

import com.udea.sistemas.innosistemas.models.dtos.TeamDto;
import com.udea.sistemas.innosistemas.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable int id) {
            if(teamService.deleteTeam(id)){
                return ResponseEntity.ok().build();
            }

        return ResponseEntity.notFound().build();


    }

}
