package com.codargamescomia.puzzle.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password; // Criptografada com BCrypt
    private boolean consentGiven; // Consentimento para coleta de dados
    private boolean premium;
    private Date dataPremium;
    
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isConsentGiven() {
		return consentGiven;
	}
	public void setConsentGiven(boolean consentGiven) {
		this.consentGiven = consentGiven;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

	public Date getDataPremium() {
		return dataPremium;
	}

	public void setDataPremium(Date dataPremium) {
		this.dataPremium = dataPremium;
	}
	
	

}


