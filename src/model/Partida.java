package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Classe que representa uma partida entre dois clubes
public class Partida {
    private Clube clubeCasa;
    private Clube clubeVisitante;
    private LocalDateTime dataHora;
    private int golsCasa;
    private int golsVisitante;
    private boolean resultadoRegistrado;

    public Partida() {
        this.golsCasa = 0;
        this.golsVisitante = 0;
        this.resultadoRegistrado = false;
    }

    public Partida(Clube clubeCasa, Clube clubeVisitante, LocalDateTime dataHora) {
        this.clubeCasa = clubeCasa;
        this.clubeVisitante = clubeVisitante;
        this.dataHora = dataHora;
        this.golsCasa = 0;
        this.golsVisitante = 0;
        this.resultadoRegistrado = false;
    }

    public Clube getClubeCasa() {
        return clubeCasa;
    }

    public void setClubeCasa(Clube clubeCasa) {
        this.clubeCasa = clubeCasa;
    }

    public Clube getClubeVisitante() {
        return clubeVisitante;
    }

    public void setClubeVisitante(Clube clubeVisitante) {
        this.clubeVisitante = clubeVisitante;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public int getGolsCasa() {
        return golsCasa;
    }

    public void setGolsCasa(int golsCasa) {
        this.golsCasa = golsCasa;
    }

    public int getGolsVisitante() {
        return golsVisitante;
    }

    public void setGolsVisitante(int golsVisitante) {
        this.golsVisitante = golsVisitante;
    }

    public boolean isResultadoRegistrado() {
        return resultadoRegistrado;
    }

    public void setResultadoRegistrado(boolean resultadoRegistrado) {
        this.resultadoRegistrado = resultadoRegistrado;
    }

    // registra o resultado real da partida
    public void registrarResultado(int golsCasa, int golsVisitante) {
        this.golsCasa = golsCasa;
        this.golsVisitante = golsVisitante;
        this.resultadoRegistrado = true;
    }

    // retorna quem ganhou: CASA, VISITANTE ou EMPATE
    public String getResultado() {
        if (!resultadoRegistrado) {
            return null;
        }
        if (golsCasa > golsVisitante) {
            return "CASA";
        } else if (golsVisitante > golsCasa) {
            return "VISITANTE";
        } else {
            return "EMPATE";
        }
    }

    // verifica se ainda da pra apostar (ate 20 min antes)
    public boolean podeApostar() {
        if (dataHora == null) {
            return false;
        }
        LocalDateTime limite = dataHora.minusMinutes(20);
        return LocalDateTime.now().isBefore(limite);
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String placar = "";
        if (resultadoRegistrado) {
            placar = " [" + golsCasa + " x " + golsVisitante + "]";
        }
        String data = "Sem data";
        if (dataHora != null) {
            data = dataHora.format(fmt);
        }
        return clubeCasa.getNome() + " vs " + clubeVisitante.getNome() + " - " + data + placar;
    }
}
