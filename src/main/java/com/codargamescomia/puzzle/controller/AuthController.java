package com.codargamescomia.puzzle.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserGameSessionRepository sessionRepository;


    private final String SECRET_KEY = System.getenv("JWT_SECRET");


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body("E-mail já registrado");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setConsentGiven(request.isConsentGiven());
        System.out.println(user.isConsentGiven());
        user.setPremium(false);
        if (user.isConsentGiven()) {
            return ResponseEntity.badRequest().body("Consentimento necessário");
        }
        userRepository.save(user);
        return ResponseEntity.ok("Usuário registrado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(null);
        }
        String token = Jwts.builder()
                .setSubject(user.getEmail()).setIssuer(SECRET_KEY)
                //.signWith(SECRET_KEY)
                .compact();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setToken(token);
        if(user.isPremium()) {
        	userDTO.setPremium(true);
        }else {
        	userDTO.setPremium(false);
        }
        return ResponseEntity.ok(userDTO);
        
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) {
        String email = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token.substring(7))
            .getBody()
            .getSubject();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
        userRepository.delete(user);
        return ResponseEntity.ok("Conta excluída com sucesso");
    }
    
    @PostMapping("/users")
    public List<User> listUsers() {
        List<User> users = userRepository.findAll();
        if (users.size() <= 0) {
            return null;
        }
        return users;
    }
    
    @PostMapping("/email")
    public User getByUserId(String email) {
    	List<User> users = userRepository.findAll();
        if (users.size() <= 0) {
            return null;
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        return user;
    }
    
    @PostMapping("/save-game-stats")
    public ResponseEntity<String> saveGameStats(
            @RequestHeader("Authorization") String token,
            @RequestBody UserGameStatsDTO statsDTO) {

        String email = Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token.substring(7))
            .getBody()
            .getSubject();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        // ✅ Só salva se for premium
        boolean isPremium = userHasPremiumAccess(user);
        if (!isPremium) {
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

    private boolean userHasPremiumAccess(User user) {
    	if(user.getEmail() != null && !user.isPremium()) {
    		return false;
    	}
    	// Aqui você define a lógica de premium.
        // Exemplo: e-mails específicos ou flag no futuro.
        return user.isPremium();
        
        // Ou:
        // return user.isPremium(); // se futuramente criar esse campo
    }


    
}


class RegisterRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private boolean consentGiven;
    


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isConsentGiven() { return consentGiven; }
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