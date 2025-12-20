package com.codargamescomia.puzzle.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codargamescomia.puzzle.dto.UserGameStatsDTO;
import com.codargamescomia.puzzle.entity.User;
import com.codargamescomia.puzzle.entity.UserGameSession;
import com.codargamescomia.puzzle.repository.UserGameSessionRepository;
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
@RequestMapping("/api/session")
public class UserGameSessionController {

    @Autowired
    private UserGameSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/user")
    public ResponseEntity<List<UserGameStatsDTO>> getUserSessions(@RequestHeader("Authorization") String token) {
        String tokenClean = token.replace("Bearer ", "");
        Claims claims = jwtUtil.validateToken(tokenClean);
        String email = claims.getSubject();
        System.out.println(token);
        System.out.println(email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<UserGameSession> sessions = sessionRepository.findByUserOrderByDateDesc(user);
        List<UserGameStatsDTO> dtos = sessions.stream()
                .map(UserGameStatsDTO::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
