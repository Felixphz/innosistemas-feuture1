package com.udea.sistemas.innosistemas.controllers;


import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.service.TeamService;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/usersteam")
public class UsersTeamController {

    private final UsersTeamRepository usersTeamRepository;
    private final TeamService teamService;

    public UsersTeamController(UsersTeamRepository usersTeamRepository, TeamService teamService) {
        this.usersTeamRepository = usersTeamRepository;
        this.teamService = teamService;
    }

    @PostMapping("/addUserTeam/{id_user}/{id_team}/{emailUser}")
    public ResponseEntity<UsersTeam> addUsersTeam(@PathVariable Integer id_user, @PathVariable Integer id_team, @PathVariable String emailUser){
        UsersTeam usersTeam = teamService.addStudent(id_team, id_user, emailUser);

        return ResponseEntity.ok(usersTeam);
    }

    @DeleteMapping("/removeUSer/{id_user}/{id_team}/{emailUser}")
    public ResponseEntity<String> removeUser(@PathVariable Integer id_user, @PathVariable Integer id_team, @PathVariable String emailUser){
        teamService.removeStudent(id_team, id_user, emailUser);

        return ResponseEntity.ok("Usuario "+id_user+" eliminado con exito del grupo "+id_team);
    }

}
