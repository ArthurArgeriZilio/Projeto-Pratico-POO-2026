package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Classe de conexao com o banco.
public class DatabaseConnection {
    private static DatabaseConnection instancia;
    private final DatabaseConfig config;

    private DatabaseConnection() {
        this.config = new DatabaseConfig();
        carregarDriver();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    private void carregarDriver() {
        try {
            Class.forName(config.getDriver());
        } catch (ClassNotFoundException e) {
            // Alguns drivers carregam sozinhos.
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }
}
