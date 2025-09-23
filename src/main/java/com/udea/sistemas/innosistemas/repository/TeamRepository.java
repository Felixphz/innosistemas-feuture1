package com.udea.sistemas.innosistemas.repository;

import com.udea.sistemas.innosistemas.models.entity.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends CrudRepository<Team,Integer> {
}
