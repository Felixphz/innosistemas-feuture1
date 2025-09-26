package com.udea.sistemas.innosistemas.service;
import com.udea.sistemas.innosistemas.models.dto.TeamDto;
import com.udea.sistemas.innosistemas.models.dto.TeamShowDto;
import com.udea.sistemas.innosistemas.models.dto.UserDto;
import com.udea.sistemas.innosistemas.models.entity.Project;
import com.udea.sistemas.innosistemas.models.entity.Team;
import com.udea.sistemas.innosistemas.models.entity.User;
import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.models.entity.UsersTeamId;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TeamService{
    private final TeamRepository teamRepository;
    private final UsersTeamRepository usersTeamRepository;
    public TeamService(TeamRepository teamRepository, 
    UsersTeamRepository usersTeamRepository) {
        this.teamRepository = teamRepository;
        this.usersTeamRepository = usersTeamRepository;
    }

    public boolean createTeam(TeamDto teamDto) {
        try {
            if (!teamDto.nameTeam().isEmpty() && teamDto.projectId() != null) {
                if (teamDto.students() == null || teamDto.students().size() <= 1) {
                    System.err.println("Error: Un equipo debe tener más de un usuario");
                    return false;
                }
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
                    return true;
                }
        } catch (Exception e) {
            System.err.println("Error al crear el equipo: " + e.getMessage());
            return false;
        }
        return false;
    }

    @Transactional
    public boolean deleteTeam(int idTeam) {
        try {

            Optional<Team> opTeam = teamRepository.findById(idTeam);
            if(opTeam.isPresent()) {
                usersTeamRepository.deleteByTeam_IdTeam(idTeam);
                teamRepository.deleteById(idTeam);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar el equipo: " + e.getMessage());
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
