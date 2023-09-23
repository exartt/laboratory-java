package br.com.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String configFilePath = "src/main/resources/config.yml";

        try (FileInputStream input = new FileInputStream(configFilePath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(input);
            Map<String, String> databaseConfig = (Map<String, String>) config.get("database");

            String url = databaseConfig.get("url");
            String user = databaseConfig.get("user");
            String password = databaseConfig.get("password");

            return DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            throw new SQLException("Erro ao ler configurações do banco de dados.", e);
        }
    }
}
