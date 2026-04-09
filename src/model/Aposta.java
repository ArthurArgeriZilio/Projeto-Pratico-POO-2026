package model;

// Classe que representa a aposta de um participante em uma partida
public class Aposta {
    private Participante participante;
    private Partida partida;
    private int golsCasaAposta;
    private int golsVisitanteAposta;

    public Aposta() {
        this.golsCasaAposta = 0;
        this.golsVisitanteAposta = 0;
    }

    public Aposta(Participante participante, Partida partida, int golsCasaAposta, int golsVisitanteAposta) {
        this.participante = participante;
        this.partida = partida;
        this.golsCasaAposta = golsCasaAposta;
        this.golsVisitanteAposta = golsVisitanteAposta;
    }

    public Participante getParticipante() {
        return participante;
    }

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public int getGolsCasaAposta() {
        return golsCasaAposta;
    }

    public void setGolsCasaAposta(int golsCasaAposta) {
        this.golsCasaAposta = golsCasaAposta;
    }

    public int getGolsVisitanteAposta() {
        return golsVisitanteAposta;
    }

    public void setGolsVisitanteAposta(int golsVisitanteAposta) {
        this.golsVisitanteAposta = golsVisitanteAposta;
    }

    // retorna o resultado que o participante apostou
    public String getResultadoAposta() {
        if (golsCasaAposta > golsVisitanteAposta) {
            return "CASA";
        } else if (golsVisitanteAposta > golsCasaAposta) {
            return "VISITANTE";
        } else {
            return "EMPATE";
        }
    }

    // calcula os pontos da aposta (sem parametros - usa dados da partida)
    public int calcularPontos() {
        if (partida == null || !partida.isResultadoRegistrado()) {
            return 0;
        }

        boolean acertouResultado = getResultadoAposta().equals(partida.getResultado());
        boolean acertouPlacar = (golsCasaAposta == partida.getGolsCasa())
                && (golsVisitanteAposta == partida.getGolsVisitante());

        if (acertouResultado && acertouPlacar) {
            return 10; // acertou resultado + placar
        } else if (acertouResultado) {
            return 5; // acertou so o resultado
        } else {
            return 0;
        }
    }

    // sobrecarga - calcula pontos passando o resultado real como parametro
    public int calcularPontos(int golsCasaReal, int golsVisitanteReal) {
        String resultadoReal;
        if (golsCasaReal > golsVisitanteReal) {
            resultadoReal = "CASA";
        } else if (golsVisitanteReal > golsCasaReal) {
            resultadoReal = "VISITANTE";
        } else {
            resultadoReal = "EMPATE";
        }

        boolean acertouResultado = getResultadoAposta().equals(resultadoReal);
        boolean acertouPlacar = (golsCasaAposta == golsCasaReal)
                && (golsVisitanteAposta == golsVisitanteReal);

        if (acertouResultado && acertouPlacar) {
            return 10;
        } else if (acertouResultado) {
            return 5;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return participante.getNome() + ": " + partida.getClubeCasa().getSigla() + " "
                + golsCasaAposta + " x " + golsVisitanteAposta + " " + partida.getClubeVisitante().getSigla();
    }
}
