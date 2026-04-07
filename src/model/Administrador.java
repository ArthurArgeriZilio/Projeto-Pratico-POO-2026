package model;

// Administrador tambem herda de Pessoa
public class Administrador extends Pessoa {
    private String senha;

    public Administrador() {
        super();
        this.senha = "";
    }

    public Administrador(String nome, String email, String senha) {
        super(nome, email);
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // sobrescrita - polimorfismo
    @Override
    public String getTipo() {
        return "Administrador";
    }
}
