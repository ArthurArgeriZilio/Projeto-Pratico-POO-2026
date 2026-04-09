package model;

import java.util.ArrayList;
import java.util.List;

// Classe que representa um campeonato (maximo 8 clubes)
public class Campeonato {
    private String nome;
    private List<Clube> clubes;
    private List<Partida> partidas;

    public Campeonato() {
        this.nome = "";
        this.clubes = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public Campeonato(String nome) {
        this.nome = nome;
        this.clubes = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Clube> getClubes() {
        return clubes;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public boolean adicionarClube(Clube clube) {
        if (clubes.size() >= 8) {
            return false; // limite de 8 clubes
        }
        clubes.add(clube);
        return true;
    }

    public void adicionarPartida(Partida partida) {
        partidas.add(partida);
    }

    @Override
    public String toString() {
        return nome;
    }
}
