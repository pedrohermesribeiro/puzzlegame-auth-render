package com.codargamescomia.puzzle.dto;

import java.util.Date;

import com.codargamescomia.puzzle.entity.GameSession;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GameSessionDTO {


    private int percentualAcertos;
    private long tempoTotalJogado;
    private long totalCortes;
    private String image;
    private Date date;
    private String user;

    // ✅ Construtor que aceita UserGameSession
    public GameSessionDTO(GameSession session) {
        this.percentualAcertos = session.getPercentualAcertos();
        this.tempoTotalJogado = session.getTempoTotalJogado();
        this.totalCortes = session.getTotalCortes();
        this.image = session.getImage();
        this.date = session.getDate();
        this.user = session.getUser().getEmail();
    }

    // Construtor padrão (necessário para Jackson ou outras libs)
    public GameSessionDTO() {
    }

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
    
    
}
