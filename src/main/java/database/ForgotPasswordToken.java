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
    * Classe responsável por gerenciar a tabela de tokens de alteração de senhas do banco de dados.
    *
    * @author isaquesv
    * @since v1.0
*/
public class ForgotPasswordToken {
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
        * Verifica se o token fornecido existe no banco de dados.
        * 
        * @param forgotPasswordToken    Token de confirmação de troca de senha.
        * @return                       JSONObject com o resultado da consulta.
        * @author                       isaquesv
    */
    public static JSONObject getForgotPasswordToken(String forgotPasswordToken) throws Exception {
        JSONObject forgotPasswordTokenExistenceResponse = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT * FROM token_forgot_password "
                       + "WHERE token = ? AND is_used = ?";
            
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, forgotPasswordToken);
            pStmt.setInt(2, 0);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                LocalDateTime expiresAt = result.getTimestamp("expires_at").toLocalDateTime();
                String tokenDatabase = result.getString("token");
                
                // Token válido e dentro do tempo limite
                if (expiresAt.isAfter(LocalDateTime.now()) && tokenDatabase.equals(forgotPasswordToken)) {
                    setForgotPasswordTokenStatus(forgotPasswordToken);
                    
                    forgotPasswordTokenExistenceResponse.put("isForgotPasswordTokenExistsInDatabase", true);
                    forgotPasswordTokenExistenceResponse.put("message", "Token validado com sucesso!");
                } else {
                    forgotPasswordTokenExistenceResponse.put("isForgotPasswordTokenExistsInDatabase", false);
                    forgotPasswordTokenExistenceResponse.put("message", "Sentimos muito! Este link de recuperação de senha está expirado. Por favor, solicite o envio de outro e-mail para alterar sua senha. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
                }
            } else {
                forgotPasswordTokenExistenceResponse.put("isForgotPasswordTokenExistsInDatabase", false);
                forgotPasswordTokenExistenceResponse.put("message", "Sentimos muito! Este link de recuperação de senha está incorreto ou expirado. Por favor, solicite o envio de outro e-mail para alterar sua senha. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return forgotPasswordTokenExistenceResponse;
    }
    
    /**
        * Verifica se o token fornecido existe no banco de dados. Captura o nome e e-mail atrelado ao token.
        * 
        * @param forgotPasswordToken    Token de confirmação de troca de senha.
        * @return                       JSONObject com o resultado da consulta.
        * @author                       isaquesv
    */
    public static JSONObject getNameAndEmailByForgotPasswordToken(String forgotPasswordToken) throws Exception {
        JSONObject emailSearchResponse = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT name, email FROM token_forgot_password "
                       + "WHERE token = ? AND is_used = ?";
            
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, forgotPasswordToken);
            pStmt.setInt(2, 1);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                String nameAssociatedWithToken = result.getString("name");
                String emailAssociatedWithToken = result.getString("email");
                
                emailSearchResponse.put("isNameAndEmailFound", true);
                emailSearchResponse.put("nameAssociatedWithToken", nameAssociatedWithToken);
                emailSearchResponse.put("emailAssociatedWithToken", emailAssociatedWithToken);
            } else {
                emailSearchResponse.put("isNameAndEmailFound", false);
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return emailSearchResponse;
    }
    
    /**
        * Registra um token de alteração de senha.
        * 
        * @param name                   Nome do usuário.
        * @param email                  E-mail do usuário.
        * @param forgotPasswordToken    Token gerado para a alteração da senha do usuário.
        * @return                       Boolean com o resultado da inserção.
        * @author                       isaquesv
    */
    public static boolean addForgotPasswordToken(String name, String email, String forgotPasswordToken) throws SQLException {
        boolean isForgotPasswordTokenRegistered = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "INSERT INTO token_forgot_password (token, name, email, is_used, expires_at) "
                       + "VALUES(?, ?, ?, ?, ?)";
            
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, forgotPasswordToken);
            pStmt.setString(2, name);
            pStmt.setString(3, email);
            pStmt.setInt(4, 0);
            // Define o timestamp de expiração (15 minutos a partir de agora)
            pStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now().plus(15, ChronoUnit.MINUTES)));

            int result = pStmt.executeUpdate(); 
            if (result == 1) {
                isForgotPasswordTokenRegistered = true;
            }
            
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isForgotPasswordTokenRegistered;
    }
    
    /**
        * Atualiza o status de uso de um determinado token de alteração de senha.
        * 
        * @param forgotPasswordToken    Token gerado para a alteração da senha do usuário.
        * @return                       Boolean com o resultado da atualização do status de uso do token.
        * @author                       isaquesv
    */
    public static boolean setForgotPasswordTokenStatus(String forgotPasswordToken) {
        boolean isForgotPasswordTokenUpdated = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
            
            // Prepara o comando SQL de seleção
            PreparedStatement pStmt = databaseConnection.prepareStatement(
                "UPDATE token_forgot_password " +
                "SET is_used = ? " +
                "WHERE token = ?"
            );
            pStmt.setInt(1, 1);
            pStmt.setString(2, forgotPasswordToken);
            
            int result = pStmt.executeUpdate(); 
            if (result == 1) {
                isForgotPasswordTokenUpdated = true;
            }
            
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isForgotPasswordTokenUpdated;
    }
}