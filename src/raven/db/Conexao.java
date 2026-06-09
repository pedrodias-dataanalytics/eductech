package raven.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = envOrDefault(
            "DB_URL",
            "jdbc:mysql://localhost:3306/eductech_manager?useSSL=false&serverTimezone=America/Sao_Paulo");
    private static final String USUARIO = envOrDefault("DB_USER", "root");
    private static final String SENHA = envOrDefault("DB_PASSWORD", "");

    public static Connection getConnexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco: " + e.getMessage());
            return null;
        }
    }

    private static String envOrDefault(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
