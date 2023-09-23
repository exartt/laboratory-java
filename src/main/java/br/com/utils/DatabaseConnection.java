package br.com.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "config.yml";

    public static Connection getConnection() throws SQLException {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Arquivo de configuração " + CONFIG_FILE + " não encontrado.");
            }

            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(input);
            Map<String, String> databaseConfig = (Map<String, String>) config.get("database");

            String url = databaseConfig.get("url");
            String user = databaseConfig.get("user");
            String password = databaseConfig.get("password");

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new SQLException("Erro ao ler configurações do banco de dados.", e);
        }
    }
}
