package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.json.JSONObject;

/**
    * Classe responsável por gerenciar a tabela de tokens de confirmação de cadastro
    * de usuários do banco de dados.
    *
    * @author isaquesv
    * @since v1.0
*/
public class RegisterToken {
    /**
        * Cria a tabela de tokens de confirmação de cadastro de usuários do banco de dados.
        * 
        * @author isaquesv
    */
    public static void createRegisterTokenTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS token_confirmation_register ("
                   + "code INTEGER PRIMARY KEY, "
                   + "token VARCHAR(40) NOT NULL, "
                   + "name VARCHAR(30) NOT NULL, "
                   + "email VARCHAR(255) NOT NULL, "
                   + "password VARCHAR(255) NOT NULL, "
                   + "is_used TINYINT(1) NOT NULL, "
                   + "expires_at TIMESTAMP NOT NULL "
                   + ")";
        
        Connection databaseConnection = DatabaseConnection.getConnection();
        Statement stmt = databaseConnection.createStatement();
        stmt.execute(sql);
    }
    
    /**
        * Verifica se o token fornecido existe no banco de dados.
        * 
        * @param registerToken      Token de confirmação de cadastro.
        * @return                   JSONObject com o resultado da consulta.
        * @author                   isaquesv
    */
    public static JSONObject getRegisterToken(String registerToken) throws Exception {
        JSONObject registerTokenExistenceJsonResponse = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT * FROM token_confirmation_register "
                       + "WHERE token = ? AND is_used = ?";
            
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, registerToken);
            pStmt.setInt(2, 0);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                String name = result.getString("name");
                String email = result.getString("email");
                String password = result.getString("password");

                LocalDateTime expiresAt = result.getTimestamp("expires_at").toLocalDateTime();
                String tokenDatabase = result.getString("token");

                // Token válido e dentro do tempo limite
                if (expiresAt.isAfter(LocalDateTime.now()) && tokenDatabase.equals(registerToken)) {
                    setRegisterTokenStatus(registerToken);
                    
                    registerTokenExistenceJsonResponse.put("isRegisterTokenInDatabase", true);
                    registerTokenExistenceJsonResponse.put("name", name);
                    registerTokenExistenceJsonResponse.put("email", email);
                    registerTokenExistenceJsonResponse.put("password", password);
                    registerTokenExistenceJsonResponse.put("message", "Token validado com sucesso!");
                } else {
                    registerTokenExistenceJsonResponse.put("isRegisterTokenInDatabase", false);
                    registerTokenExistenceJsonResponse.put("message", "Sentimos muito! Este link de confirmação de cadastro está expirado. Por favor, solicite o envio de outro e-mail para concluir seu cadastro. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
                }
            } else {
                registerTokenExistenceJsonResponse.put("isRegisterTokenInDatabase", false);
                registerTokenExistenceJsonResponse.put("message", "Sentimos muito! Este link de confirmação de cadastro está incorreto ou expirado. Por favor, solicite o envio de outro e-mail para concluir seu cadastro. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return registerTokenExistenceJsonResponse;
    }
    
    /**
        * Registra um token de confirmação de cadastro.
        * 
        * @param registerToken          Token gerado para a confirmação de cadastro do usuário.
        * @param name                   Nome do usuário.
        * @param email                  E-mail do usuário.
        * @param password               Senha, hash, do usuário.
        * @return                       Boolean com o resultado da execução da inserção.
        * @author              isaquesv
    */
    public static boolean addRegisterToken(String registerToken, String name, String email, String password) throws SQLException {
        boolean isTokenRegistered = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "INSERT INTO token_confirmation_register (token, name, email, password, is_used, expires_at) "
                       + "VALUES(?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, registerToken);
            pStmt.setString(2, name);
            pStmt.setString(3, email);
            pStmt.setString(4, password);
            pStmt.setInt(5, 0);
            // Define o timestamp de expiração (15 minutos a partir de agora)
            pStmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now().plus(15, ChronoUnit.MINUTES)));

            int result = pStmt.executeUpdate(); 
            if (result == 1) {
                isTokenRegistered = true;
            }
            
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isTokenRegistered;
    }
    
    /**
        * Atualiza o status de uso de um determinado token de cadastro de usuário.
        * 
        * @param registerToken          Token gerado para a confirmação de cadastro do usuário.
        * @return                   Boolean com o resultado da execução da atualização do status do token, caso dê certo retorna true, caso contrário retorna false.
        * @author                   isaquesv
    */
    public static boolean setRegisterTokenStatus(String registerToken) {
        boolean isTokenUpdated = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
            
            // Prepara o comando SQL de seleção
            PreparedStatement pStmt = databaseConnection.prepareStatement(
                "UPDATE token_confirmation_register " +
                "SET is_used = ? " +
                "WHERE token = ?"
            );
            pStmt.setInt(1, 1);
            pStmt.setString(2, registerToken);
            
            int result = pStmt.executeUpdate(); 
            if (result == 1) {
                isTokenUpdated = true;
            }
            
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isTokenUpdated;
    }
}