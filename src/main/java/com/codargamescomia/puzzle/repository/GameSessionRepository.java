package com.codargamescomia.puzzle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codargamescomia.puzzle.entity.GameSession;


public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
	
	//List<GameSession> findAllGameOrderByDateDesc();
		List<GameSession> findAllByOrderByTotalCortesDescTempoTotalJogadoAscPercentualAcertosDesc();
	
}