package com.CineTrust.repository;

import com.CineTrust.entity.UserLogins;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginsRepository extends JpaRepository<UserLogins, Long> {
    Optional<UserLogins> findByToken(String jwtToken);
}
