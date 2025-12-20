package com.codargamescomia.puzzle.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codargamescomia.puzzle.dto.GameSessionDTO;
import com.codargamescomia.puzzle.dto.UserGameStatsDTO;
import com.codargamescomia.puzzle.entity.GameSession;
import com.codargamescomia.puzzle.entity.User;
import com.codargamescomia.puzzle.repository.GameSessionRepository;
import com.codargamescomia.puzzle.repository.UserRepository;
import com.codargamescomia.puzzle.util.JwtUtil;

import io.jsonwebtoken.Claims;


@CrossOrigin(
	    origins = {
	        "https://puzzlegame.onrender.com",
	        "http://localhost:5500"
	    }
	)
@RestController
@RequestMapping("/api/ranking")
public class GameSessionController {

    @Autowired
    private GameSessionRepository gameSessionRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/save-session")
    public ResponseEntity<String> saveSessions(@RequestHeader("Authorization") String token,@RequestBody UserGameStatsDTO statsDTO) {
        String tokenClean = token.replace("Bearer ", "");
        Claims claims = jwtUtil.validateToken(tokenClean);
        String email = claims.getSubject();
        System.out.println(token);
        System.out.println(email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
        	return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
         
        GameSession session = new GameSession();
        session.setUser(user);
        session.setPercentualAcertos(statsDTO.getPercentualAcertos());
        session.setTempoTotalJogado(statsDTO.getTempoTotalJogado());
        session.setTotalCortes(statsDTO.getTotalCortes());
        session.setImage(statsDTO.getImage());
        session.setDate(new Date());

        gameSessionRepository.save(session);
        
        return ResponseEntity.ok("Sessão salva com sucesso!");
    }
    
    
    @GetMapping("/sessions")
    public ResponseEntity<List<GameSessionDTO>> getSessions(@RequestHeader("Authorization") String token) {
        String tokenClean = token.replace("Bearer ", "");
        Claims claims = jwtUtil.validateToken(tokenClean);
        String email = claims.getSubject();
        System.out.println(token);
        System.out.println(email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<GameSession> sessions = gameSessionRepository.findAllByOrderByTotalCortesDescTempoTotalJogadoAscPercentualAcertosDesc();
        List<GameSessionDTO> dtos = sessions.stream()
                .map(GameSessionDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}

