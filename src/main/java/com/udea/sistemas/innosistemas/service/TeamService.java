package com.udea.sistemas.innosistemas.service;
import com.udea.sistemas.innosistemas.models.dtos.TeamDto;
import com.udea.sistemas.innosistemas.models.entity.Team;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TeamService{
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public boolean deleteTeam(int idTeam) {
        try {

            Optional<Team> opTeam = teamRepository.findById(idTeam);
            if(opTeam.isPresent()) {
                teamRepository.deleteById(idTeam);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar el equipo: " + e.getMessage());
            return false;
        }
        return false;
    }

}
