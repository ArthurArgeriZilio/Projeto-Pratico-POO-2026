package service;

import model.*;
import java.util.ArrayList;
import java.util.List;

// Classe que controla toda a logica do sistema
public class SistemaService {
    private List<Clube> clubes;
    private List<Campeonato> campeonatos;
    private List<Grupo> grupos;
    private List<Participante> participantes;

    public SistemaService() {
        this.clubes = new ArrayList<>();
        this.campeonatos = new ArrayList<>();
        this.grupos = new ArrayList<>();
        this.participantes = new ArrayList<>();
    }

    // cadastra um novo clube
    public void cadastrarClube(String nome, String sigla) {
        Clube c = new Clube(nome, sigla);
        clubes.add(c);
    }

    public List<Clube> getClubes() {
        return clubes;
    }

    // cria um campeonato
    public Campeonato cadastrarCampeonato(String nome) {
        Campeonato c = new Campeonato(nome);
        campeonatos.add(c);
        return c;
    }

    public List<Campeonato> getCampeonatos() {
        return campeonatos;
    }

    // cria um grupo (maximo 5)
    public boolean cadastrarGrupo(String nome) {
        if (grupos.size() >= 5) {
            return false;
        }
        Grupo g = new Grupo(nome);
        grupos.add(g);
        return true;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    // cadastra participante
    public Participante cadastrarParticipante(String nome, String email) {
        Participante p = new Participante(nome, email);
        participantes.add(p);
        return p;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    // registra uma aposta, retorna mensagem de erro ou null se deu certo
    public String registrarAposta(Participante participante, Partida partida, int golsCasa, int golsVisitante) {
        if (!partida.podeApostar()) {
            return "Nao e possivel apostar. Prazo encerrou (20 min antes da partida).";
        }
        if (partida.isResultadoRegistrado()) {
            return "Nao e possivel apostar em partida ja encerrada.";
        }

        // verifica se ja apostou nessa partida
        for (int i = 0; i < participante.getApostas().size(); i++) {
            if (participante.getApostas().get(i).getPartida() == partida) {
                return "Voce ja apostou nessa partida.";
            }
        }

        Aposta aposta = new Aposta(participante, partida, golsCasa, golsVisitante);
        participante.adicionarAposta(aposta);
        return null;
    }

    // registra o resultado real de uma partida
    public void registrarResultado(Partida partida, int golsCasa, int golsVisitante) {
        partida.registrarResultado(golsCasa, golsVisitante);
    }
}
