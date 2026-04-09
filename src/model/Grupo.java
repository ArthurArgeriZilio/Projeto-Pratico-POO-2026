package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Classe que representa um grupo de apostas (maximo 5 participantes)
public class Grupo {
    private String nome;
    private List<Participante> participantes;

    public Grupo() {
        this.nome = "";
        this.participantes = new ArrayList<>();
    }

    public Grupo(String nome) {
        this.nome = nome;
        this.participantes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public boolean adicionarParticipante(Participante participante) {
        if (participantes.size() >= 5) {
            return false; // limite de 5 participantes
        }
        participantes.add(participante);
        return true;
    }

    // retorna a classificacao ordenada por pontuacao (maior primeiro)
    public List<Participante> getClassificacao() {
        // primeiro recalcula a pontuacao de todo mundo
        for (int i = 0; i < participantes.size(); i++) {
            participantes.get(i).calcularPontuacao();
        }

        // copia a lista e ordena
        List<Participante> classificacao = new ArrayList<>(participantes);
        Collections.sort(classificacao, new Comparator<Participante>() {
            @Override
            public int compare(Participante p1, Participante p2) {
                return p2.getPontuacaoTotal() - p1.getPontuacaoTotal();
            }
        });
        return classificacao;
    }

    @Override
    public String toString() {
        return nome;
    }
}
