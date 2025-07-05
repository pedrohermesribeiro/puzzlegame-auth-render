package com.codargamescomia.puzzle.dto;

public class UserDTO {

	
    private String email;
    private String token;
    private boolean premium;

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public boolean isPremium() {
		return premium;
	}
	public void setPremium(boolean premium) {
		this.premium = premium;
	}
    
	
    
}
