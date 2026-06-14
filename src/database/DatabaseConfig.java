package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// Configuracoes usadas para conectar no banco.
public class DatabaseConfig {
    private static final String ARQUIVO_CONFIG = "database.properties";
    private final Properties properties;

    public DatabaseConfig() {
        properties = new Properties();
        carregarPadroes();
        carregarArquivo();
    }

    private void carregarPadroes() {
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/sistema_apostas?useSSL=false&serverTimezone=UTC");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
    }

    private void carregarArquivo() {
        try (FileInputStream input = new FileInputStream(ARQUIVO_CONFIG)) {
            properties.load(input);
        } catch (IOException e) {
            // Se nao achar o arquivo, usa os valores padrao.
        }
    }

    public String getDriver() {
        return properties.getProperty("db.driver");
    }

    public String getUrl() {
        return properties.getProperty("db.url");
    }

    public String getUser() {
        return properties.getProperty("db.user");
    }

    public String getPassword() {
        return properties.getProperty("db.password");
    }
}
