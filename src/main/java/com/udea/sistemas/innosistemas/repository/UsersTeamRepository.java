package com.udea.sistemas.innosistemas.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import com.udea.sistemas.innosistemas.models.entity.UsersTeam;
import com.udea.sistemas.innosistemas.models.entity.UsersTeamId;
import jakarta.transaction.Transactional;

@Repository
public interface UsersTeamRepository extends JpaRepository<UsersTeam, UsersTeamId> {
    
    @Modifying
    @Transactional
    void deleteByTeam_IdTeam(Integer idTeam);
}
