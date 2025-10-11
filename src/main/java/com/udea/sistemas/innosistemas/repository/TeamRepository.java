package com.udea.sistemas.innosistemas.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import com.udea.sistemas.innosistemas.models.entity.Team;

import jakarta.transaction.Transactional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    
    @Modifying
    @Transactional
    void deleteByIdTeam(Integer idTeam);
}
