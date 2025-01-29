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
    * Classe responsável por gerenciar a tabela de tokens de alterações de senhas do banco de dados.
    *
    * @author isaquesv
    * @since v1.0
*/
public class UserForgotPasswordTokenDAO {
    /**
        * Cria a tabela de tokens de alteração de senhas do banco de dados.
        * 
        * @author isaquesv
    */
    public static void createForgotPasswordTokenTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS token_forgot_password ("
                   + "code INTEGER PRIMARY KEY, "
                   + "token VARCHAR(40) NOT NULL, "
                   + "name VARCHAR(30) NOT NULL, "
                   + "email VARCHAR(255) NOT NULL, "
                   + "is_used TINYINT(1) NOT NULL, "
                   + "expires_at TIMESTAMP NOT NULL "
                   + ")";
        
        Connection databaseConnection = DatabaseConnection.getConnection();
        Statement stmt = databaseConnection.createStatement();
        stmt.execute(sql);
    }
    
    /**
        * Busca registros que tenham o mesmo valor que o token fornecido.
        * 
        * @param token         Token fornecido pela URL do usuário.
        * @return              JSONObject com o resultado da consulta.
        * @author              isaquesv
    */
    public static JSONObject selectForgotPasswordToken(String token) throws Exception {
        JSONObject tokenForgotPasswordResponseJSON = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT * FROM token_forgot_password "
                       + "WHERE token = ? AND is_used = ?";
            
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, token);
            stmt.setInt(2, 0);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                LocalDateTime expiresAt = rs.getTimestamp("expires_at").toLocalDateTime();
                String tokenDatabase = rs.getString("token");
                
                // Token válido e dentro do tempo limite
                if (expiresAt.isAfter(LocalDateTime.now()) && tokenDatabase.equals(token)) {
                    updateForgotPasswordTokenStatus(token);
                    
                    tokenForgotPasswordResponseJSON.put("isTokenValid", true);
                    tokenForgotPasswordResponseJSON.put("message", "Token validado com sucesso!");
                } else {
                    tokenForgotPasswordResponseJSON.put("isTokenValid", false);
                    tokenForgotPasswordResponseJSON.put("message", "Sentimos muito! Este link de recuperação de senha está expirado. Por favor, solicite o envio de outro e-mail para alterar sua senha. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
                }
            } else {
                tokenForgotPasswordResponseJSON.put("isTokenValid", false);
                tokenForgotPasswordResponseJSON.put("message", "Sentimos muito! Este link de recuperação de senha está incorreto ou expirado. Por favor, solicite o envio de outro e-mail para alterar sua senha. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
            }

            rs.close();
            stmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return tokenForgotPasswordResponseJSON;
    }
    
    /**
        * Busca registros que tenham o mesmo valor que o token fornecido, para capturar seu respectivo nome de usuário e e-mail.
        * 
        * @param token         Token fornecido pela URL do usuário.
        * @return              JSONObject com o resultado da consulta.
        * @author              isaquesv
    */
    public static JSONObject selectNameAndEmailForgotPasswordToken(String token) throws Exception {
        JSONObject emailSearchResultJSON = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT name, email FROM token_forgot_password "
                       + "WHERE token = ? AND is_used = ?";
            
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, token);
            stmt.setInt(2, 1);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nameAssociatedWithToken = rs.getString("name");
                String emailAssociatedWithToken = rs.getString("email");
                
                emailSearchResultJSON.put("isNameAndEmailFound", true);
                emailSearchResultJSON.put("nameAssociatedWithToken", nameAssociatedWithToken);
                emailSearchResultJSON.put("emailAssociatedWithToken", emailAssociatedWithToken);
            } else {
                emailSearchResultJSON.put("isNameAndEmailFound", false);
            }

            rs.close();
            stmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return emailSearchResultJSON;
    }
    
    /**
        * Cadastra um token de alteração de senha do usuário.
        * 
        * @param token         Token gerado para a alteração da senha do usuário.
        * @return              Boolean com o resultado da execução da inserção, caso dê certo retorna true, caso contrário retorna false.
        * @author              isaquesv
    */
    public static boolean insertForgotPasswordToken(String name, String email, String token) throws SQLException {
        boolean isTokenRegistered = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "INSERT INTO token_forgot_password (token, name, email, is_used, expires_at) "
                       + "VALUES(?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, token);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setInt(4, 0);
            // Define o timestamp de expiração (15 minutos a partir de agora)
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now().plus(15, ChronoUnit.MINUTES)));

            int isInsertionSuccessful = stmt.executeUpdate(); 
            if (isInsertionSuccessful == 1) {
                isTokenRegistered = true;
            }
            
            stmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isTokenRegistered;
    }
    
    /**
        * Atualiza o status de um determinado token de alteração de senha de usuário.
        * 
        * @param token         Token de alteração de senha do usuário fornecido pela URL do usuário.
        * @return              Boolean com o resultado da execução da atualização do status do token, caso dê certo retorna true, caso contrário retorna false.
        * @author              isaquesv
    */
    public static boolean updateForgotPasswordTokenStatus(String token) {
        boolean isTokenUpdated = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
            
            // Prepara o comando SQL de seleção
            PreparedStatement pstmt = databaseConnection.prepareStatement(
                "UPDATE token_forgot_password " +
                "SET is_used = ? " +
                "WHERE token = ?"
            );
            pstmt.setInt(1, 1);
            pstmt.setString(2, token);
            
            int isUpdatedSuccessfully = pstmt.executeUpdate(); 
            if (isUpdatedSuccessfully == 1) {
                isTokenUpdated = true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isTokenUpdated;
    }
}