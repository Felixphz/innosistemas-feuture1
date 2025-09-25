package com.udea.sistemas.innosistemas.repository;

import com.udea.sistemas.innosistemas.models.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
}
