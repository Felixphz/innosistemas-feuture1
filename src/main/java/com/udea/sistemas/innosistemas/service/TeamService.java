package com.udea.sistemas.innosistemas.service;

import com.udea.sistemas.innosistemas.enums.TeamState;
import com.udea.sistemas.innosistemas.models.dto.TeamDto;
import com.udea.sistemas.innosistemas.models.dto.TeamShowDto;
import com.udea.sistemas.innosistemas.models.dto.UserDto;
import com.udea.sistemas.innosistemas.models.entity.*;
import com.udea.sistemas.innosistemas.repository.TeamRepository;
import com.udea.sistemas.innosistemas.repository.UsersTeamRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UsersTeamRepository usersTeamRepository;

    private static final int MIN_MEMBERS = 2;
    private static final int MAX_MEMBERS = 3;

    public TeamService(TeamRepository teamRepository,
                       UsersTeamRepository usersTeamRepository) {
        this.teamRepository = teamRepository;
        this.usersTeamRepository = usersTeamRepository;
    }

    public boolean createTeam(TeamDto teamDto) {
        try {
            if (!teamDto.nameTeam().isEmpty() && teamDto.projectId() != null) {
                if (teamDto.students() == null || teamDto.students().size() < MIN_MEMBERS || teamDto.students().size() > MAX_MEMBERS) {
                    System.err.println("Error: Un equipo debe tener entre " + MIN_MEMBERS + " y " + MAX_MEMBERS + " usuarios");
                    return false;
                }

                Team team = new Team();
                Project project = new Project();
                project.setId(teamDto.projectId());
                project.setNameProject(teamDto.projectName());

                team.setNameTeam(teamDto.nameTeam());
                team.setProject(project);

                // Determinar State basado en la cantidad de miembros
                TeamState State = determineState(teamDto.students().size());
                team.setState(State);

                team = teamRepository.save(team);

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
            if (teamDto == null || teamDto.nameTeam() == null || teamDto.nameTeam().trim().isEmpty()) {
                System.err.println("Error: Datos del equipo inválidos");
                return false;
            }

            if (teamDto.students() == null || teamDto.students().size() < MIN_MEMBERS || teamDto.students().size() > MAX_MEMBERS) {
                System.err.println("Error: Un equipo debe tener entre " + MIN_MEMBERS + " y " + MAX_MEMBERS + " miembros");
                return false;
            }

            Optional<Team> optionalTeam = teamRepository.findById(teamId);
            if (!optionalTeam.isPresent()) {
                System.err.println("Error: Equipo no encontrado con ID: " + teamId);
                return false;
            }

            Team team = optionalTeam.get();

            team.setNameTeam(teamDto.nameTeam().trim());

            // Actualizar State basado en la cantidad de miembros
            TeamState nuevoState = determineState(teamDto.students().size());
            team.setState(nuevoState);

            if (teamDto.projectId() != null && teamDto.projectName() != null) {
                Project project = new Project();
                project.setId(teamDto.projectId());
                project.setNameProject(teamDto.projectName());
                team.setProject(project);
            }

            usersTeamRepository.deleteByTeam_IdTeam(teamId);
            team.getMembers().clear();

            for (UserDto userDto : teamDto.students()) {
                if (userDto.email() == null || userDto.email().trim().isEmpty()) {
                    System.err.println("Error: Email de usuario inválido");
                    return false;
                }

                UsersTeam userTeam = new UsersTeam();

                User user = new User();
                user.setEmail(userDto.email().trim());
                user.setNameUser(userDto.nameUser() != null ? userDto.nameUser().trim() : "");

                UsersTeamId id = new UsersTeamId();
                id.setTeamId(teamId);
                id.setEmail(userDto.email().trim());

                userTeam.setId(id);
                userTeam.setTeam(team);
                userTeam.setUser(user);

                userTeam = usersTeamRepository.save(userTeam);
                team.getMembers().add(userTeam);
            }

            teamRepository.save(team);

            System.out.println("Equipo actualizado exitosamente: " + team.getNameTeam() + " con State: " + nuevoState);
            return true;

        } catch (Exception e) {
            System.err.println("Error al actualizar el equipo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<TeamShowDto> filterTeams(String nameTeam, Integer projectId, Integer courseId, String status) {
        List<Team> teams = teamRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nameTeam != null && !nameTeam.trim().isEmpty()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("nameTeam")),
                                "%" + nameTeam.toLowerCase() + "%"
                        )
                );
            }

            if (projectId != null) {
                predicates.add(
                        cb.equal(root.get("project").get("id"), projectId)
                );
            }

            if (courseId != null) {
                predicates.add(
                        cb.equal(root.get("project").get("course").get("id"), courseId)
                );
            }

            // Filtro por State usando el enum TeamState
            if (status != null && !status.trim().isEmpty()) {
                try {
                    TeamState State = TeamState.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("State"), State));
                } catch (IllegalArgumentException e) {
                    System.err.println("State no válido para filtro: " + status);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });

        return teams.stream().map(team -> {
            return new TeamShowDto(
                    team.getIdTeam(),
                    team.getNameTeam(),
                    team.getProject().getId(),
                    team.getProject().getNameProject(),
                    team.getProject().getCourseId(),
                    team.getState(), // Incluir el State TeamState en el DTO
                    team.getMembers().stream()
                            .map(usersTeam -> new UserDto(
                                    usersTeam.getUser().getEmail(),
                                    usersTeam.getUser().getNameUser()
                            ))
                            .toList()
            );
        }).toList();
    }

    /**
     * Método auxiliar para determinar el State basado en la cantidad de miembros
     */
    private TeamState determineState(int memberCount) {
        return memberCount == MAX_MEMBERS ? TeamState.FORMADO : TeamState.INCOMPLETO;
    }

    /**
     * Método para actualizar el State de un equipo basado en sus miembros actuales
     */
    @Transactional
    public void actualizarStateAutomatico(Integer teamId) {
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (optionalTeam.isPresent()) {
            Team team = optionalTeam.get();
            int memberCount = team.getMembers().size();
            TeamState nuevoState = determineState(memberCount);

            if (team.getState() != nuevoState) {
                team.setState(nuevoState);
                teamRepository.save(team);
                System.out.println("State actualizado automáticamente a: " + nuevoState);
            }
        }
    }
}