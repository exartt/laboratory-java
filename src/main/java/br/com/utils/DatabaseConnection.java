package jar .utils;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        Dotenv dotenv = load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SQLException("Erro ao conectar ao banco de dados.", e);
        }
    }

    private static Dotenv load() {
//        String envFilePath = "/home/opc/laboratory-java/src/main/resources/.env";
        String envFilePath = "src/main/resources/.env";

        return new DotenvBuilder()
                .filename(envFilePath)
                .load();
    }
}
