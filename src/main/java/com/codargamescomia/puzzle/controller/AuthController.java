package com.codargamescomia.puzzle.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codargamescomia.puzzle.dto.UserDTO;
import com.codargamescomia.puzzle.dto.UserGameStatsDTO;
import com.codargamescomia.puzzle.entity.User;
import com.codargamescomia.puzzle.entity.UserGameSession;
import com.codargamescomia.puzzle.repository.UserGameSessionRepository;
import com.codargamescomia.puzzle.repository.UserRepository;
import com.codargamescomia.puzzle.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@CrossOrigin(
	    origins = {
	        "https://puzzlegame.onrender.com",
	        "http://localhost:5500"
	    }
	)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserGameSessionRepository sessionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
    	

        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body("E-mail já registrado");
        }
        if (!request.getConsentGiven()) {
            return ResponseEntity.badRequest().body("Consentimento necessário");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setConsentGiven(request.getConsentGiven());
        user.setPremium(false);
        userRepository.save(user);

        return ResponseEntity.ok("Usuário registrado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(null);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setToken(token);
        userDTO.setPremium(user.isPremium());

        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.validateToken(token);
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("Conta excluída com sucesso");
    }

    @PostMapping("/users")
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/email")
    public User getByUserId(@RequestBody String email) {
        return userRepository.findByEmail(email);
    }

    @PostMapping("/save-game-stats")
    public ResponseEntity<String> saveGameStats(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserGameStatsDTO statsDTO) {

        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.validateToken(token);
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        if (!user.isPremium()) {
            return ResponseEntity.ok("Dados não salvos - usuário sem acesso premium.");
        }

        UserGameSession session = new UserGameSession();
        session.setUser(user);
        session.setPercentualAcertos(statsDTO.getPercentualAcertos());
        session.setTempoTotalJogado(statsDTO.getTempoTotalJogado());
        session.setTotalCortes(statsDTO.getTotalCortes());
        session.setImage(statsDTO.getImage());
        session.setDate(new Date());

        sessionRepository.save(session);

        return ResponseEntity.ok("Sessão salva com sucesso!");
    }
}

// DTOs internos
class RegisterRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotNull
    private boolean consentGiven;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean getConsentGiven() { return consentGiven; }
    public void setConsentGiven(boolean consentGiven) { this.consentGiven = consentGiven; }
}

class LoginRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
