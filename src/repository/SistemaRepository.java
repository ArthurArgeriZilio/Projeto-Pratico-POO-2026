package repository;

public interface SistemaRepository {
    EstadoSistema carregar() throws Exception;
    void salvar(EstadoSistema estado) throws Exception;
    boolean testarConexao();
}
