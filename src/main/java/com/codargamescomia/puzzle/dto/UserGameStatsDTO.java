package com.codargamescomia.puzzle.dto;

public class UserGameStatsDTO {
    private int percentualAcertos;
    private long tempoTotalJogado;
    private long totalCortes;
    private String image;
    private String data;

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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
    
    
}
