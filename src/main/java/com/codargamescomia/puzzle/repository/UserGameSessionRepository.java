package com.codargamescomia.puzzle.repository;

import com.codargamescomia.puzzle.entity.UserGameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGameSessionRepository extends JpaRepository<UserGameSession, Long> {
}

