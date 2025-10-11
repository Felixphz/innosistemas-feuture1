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
                if (teamDto.students() == null || teamDto.students().size() <= 1 || teamDto.students().size() > 3) {
                    System.err.println("Error: Un equipo debe tener entre 2 y 3 usuarios");
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

    @Transactional
    public boolean updateTeam(Integer teamId, TeamShowDto teamDto) {
        try {
            // Validaciones básicas
            if (teamDto == null || teamDto.nameTeam() == null || teamDto.nameTeam().trim().isEmpty()) {
                System.err.println("Error: Datos del equipo inválidos");
                return false;
            }

            // Validar número de estudiantes
            if (teamDto.students() == null || teamDto.students().size() < 2 || teamDto.students().size() > 3) {
                System.err.println("Error: Un equipo debe tener entre 2 y 3 miembros");
                return false;
            }

            // Buscar el equipo existente
            Optional<Team> optionalTeam = teamRepository.findById(teamId);
            if (!optionalTeam.isPresent()) {
                System.err.println("Error: Equipo no encontrado con ID: " + teamId);
                return false;
            }

            Team team = optionalTeam.get();
            
            // Actualizar información básica del equipo
            team.setNameTeam(teamDto.nameTeam().trim());
            
            // Actualizar proyecto si es necesario
            if (teamDto.projectId() != null && teamDto.projectName() != null) {
                Project project = new Project();
                project.setId(teamDto.projectId());
                project.setNameProject(teamDto.projectName());
                team.setProject(project);
            }

            // Eliminar todos los miembros actuales
            usersTeamRepository.deleteByTeam_IdTeam(teamId);
            
            // Limpiar la lista de miembros en memoria
            team.getMembers().clear();

            // Agregar los nuevos miembros
            for (UserDto userDto : teamDto.students()) {
                if (userDto.email() == null || userDto.email().trim().isEmpty()) {
                    System.err.println("Error: Email de usuario inválido");
                    return false;
                }

                // Crear nueva relación usuario-equipo
                UsersTeam userTeam = new UsersTeam();
                
                // Crear usuario
                User user = new User();
                user.setEmail(userDto.email().trim());
                user.setNameUser(userDto.nameUser() != null ? userDto.nameUser().trim() : "");

                // Crear ID compuesto
                UsersTeamId id = new UsersTeamId();
                id.setTeamId(teamId);
                id.setEmail(userDto.email().trim());

                // Configurar relación
                userTeam.setId(id);
                userTeam.setTeam(team);
                userTeam.setUser(user);

                // Guardar relación
                userTeam = usersTeamRepository.save(userTeam);
                team.getMembers().add(userTeam);
            }

            // Guardar el equipo actualizado
            teamRepository.save(team);
            
            System.out.println("Equipo actualizado exitosamente: " + team.getNameTeam());
            return true;

        } catch (Exception e) {
            System.err.println("Error al actualizar el equipo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }}
