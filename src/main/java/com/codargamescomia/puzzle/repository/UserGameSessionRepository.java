package com.codargamescomia.puzzle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codargamescomia.puzzle.entity.User;
import com.codargamescomia.puzzle.entity.UserGameSession;

public interface UserGameSessionRepository extends JpaRepository<UserGameSession, Long> {
	
	List<UserGameSession> findByUserOrderByDateDesc(User user);
}

