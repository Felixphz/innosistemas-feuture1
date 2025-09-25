package com.udea.sistemas.innosistemas.controllers;


import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/usersteam")
public class UsersTeamController {

    private final UsersTeamRepository usersTeamRepository;

    public UsersTeamController(UsersTeamRepository usersTeamRepository) {
        this.usersTeamRepository = usersTeamRepository;
    }

    @PostMapping("/addUserTeam/{id_user}/{id_team}")
    public ResponseEntity<UsersTeam> addUsersTeam(@PathVariable Integer id_user, @PathVariable Integer id_team){


    }

}
