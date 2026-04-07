package model;

import java.util.ArrayList;
import java.util.List;

// Participante herda de Pessoa e implementa a interface Pontuavel
public class Participante extends Pessoa implements Pontuavel {
    private List<Aposta> apostas;
    private int pontuacaoTotal;

    public Participante() {
        super();
        this.apostas = new ArrayList<>();
        this.pontuacaoTotal = 0;
    }

    public Participante(String nome, String email) {
        super(nome, email);
        this.apostas = new ArrayList<>();
        this.pontuacaoTotal = 0;
    }

    public List<Aposta> getApostas() {
        return apostas;
    }

    public void adicionarAposta(Aposta aposta) {
        this.apostas.add(aposta);
    }

    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    public void setPontuacaoTotal(int pontuacaoTotal) {
        this.pontuacaoTotal = pontuacaoTotal;
    }

    // sobrescrita do metodo abstrato (polimorfismo)
    @Override
    public String getTipo() {
        return "Participante";
    }

    // implementacao da interface Pontuavel
    @Override
    public int calcularPontuacao() {
        int total = 0;
        for (int i = 0; i < apostas.size(); i++) {
            total += apostas.get(i).calcularPontos();
        }
        this.pontuacaoTotal = total;
        return total;
    }

    @Override
    public String getResumo() {
        return getNome() + " - " + calcularPontuacao() + " pts";
    }
}
