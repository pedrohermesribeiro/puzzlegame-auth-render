package com.codargamescomia.puzzle.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int percentualAcertos;
    private long tempoTotalJogado; // em segundos
    private long totalCortes;
    private String image;
    private Date date;
    
    
    
    public GameSession(int percentualAcertos, long tempoTotalJogado, long totalCortes, String image, Date date,
			User user) {
		super();
		this.percentualAcertos = percentualAcertos;
		this.tempoTotalJogado = tempoTotalJogado;
		this.totalCortes = totalCortes;
		this.image = image;
		this.date = date;
		this.user = user;
	}
    
    

	public GameSession() {
		super();
	}



	@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPercentualAcertos() {
		return percentualAcertos;
	}

	public void setPercentualAcertos(int percentualAcertos) {
		this.percentualAcertos = percentualAcertos;
	}

	public long getTempoTotalJogado() {
		return tempoTotalJogado;
	}

	public void setTempoTotalJogado(long tempoTotalJogado) {
		this.tempoTotalJogado = tempoTotalJogado;
	}

	public long getTotalCortes() {
		return totalCortes;
	}

	public void setTotalCortes(long totalCortes) {
		this.totalCortes = totalCortes;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
    
}
