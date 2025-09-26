package com.udea.sistemas.innosistemas.service;
import com.udea.sistemas.innosistemas.models.dto.TeamDto;
import com.udea.sistemas.innosistemas.models.dto.TeamShowDto;
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

    public boolean updateTeam(Integer teamId, TeamShowDto teamDto) {
        try{
            if(teamDto != null && teamDto.nameTeam() != null){
                System.out.println("entramos al if del try catch");
                Optional<Team> opTeam = teamRepository.findById(teamId);
                if(opTeam.isPresent()) {

                    Team team = opTeam.get();
                    team.setNameTeam(teamDto.nameTeam());
                    // Para el proyecto, necesitas buscar la entidad Project
                    // team.setProject(proyectRepository.findById(teamDto.projectId()).orElse(null));
                    
                    teamRepository.save(team);
                    System.out.println("el team se ha actualizado");
                    return true;
                }
            }

        }catch (Exception e){
            System.out.println("Error al actualizar el equipo: " + e.getMessage());
            return false;
        }

        return false;
    }}
