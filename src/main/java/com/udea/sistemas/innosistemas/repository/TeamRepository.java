package com.udea.sistemas.innosistemas.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.udea.sistemas.innosistemas.models.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

}
