package com.udea.sistemas.innosistemas.repository;

import com.udea.sistemas.innosistemas.models.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findByTokenAndIsExpiredFalse(String token);

    List<RefreshToken> findByUserEmailAndIsExpiredFalse(String userEmail);

    List<RefreshToken> findByExpiresAtBefore(LocalDateTime now);

}
