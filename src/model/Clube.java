package model;

// Classe que representa um clube/time de futebol
public class Clube {
    private String nome;
    private String sigla;

    public Clube() {
        this.nome = "";
        this.sigla = "";
    }

    public Clube(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @Override
    public String toString() {
        return nome + " (" + sigla + ")";
    }
}
