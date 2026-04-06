package model;

// Classe abstrata que representa uma pessoa no sistema
// Serve como base para Participante e Administrador (heranca)
public abstract class Pessoa {
    private String nome;
    private String email;

    // construtor padrao
    public Pessoa() {
        this.nome = "";
        this.email = "";
    }

    // construtor sobrecarregado
    public Pessoa(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // metodo abstrato que cada subclasse vai ter que implementar
    public abstract String getTipo();

    @Override
    public String toString() {
        return nome;
    }
}
