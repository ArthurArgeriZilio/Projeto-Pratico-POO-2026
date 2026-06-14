package repository;

import model.Campeonato;
import model.Clube;
import model.Grupo;
import model.Participante;

import java.util.ArrayList;
import java.util.List;

public class EstadoSistema {
    private final List<Clube> clubes;
    private final List<Campeonato> campeonatos;
    private final List<Grupo> grupos;
    private final List<Participante> participantes;

    public EstadoSistema() {
        this.clubes = new ArrayList<>();
        this.campeonatos = new ArrayList<>();
        this.grupos = new ArrayList<>();
        this.participantes = new ArrayList<>();
    }

    public List<Clube> getClubes() {
        return clubes;
    }

    public List<Campeonato> getCampeonatos() {
        return campeonatos;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }
}
