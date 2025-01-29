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
    * Classe responsável por gerenciar a tabela de tokens de confirmação de cadastro de usuários do banco de dados
    *
    * @author isaquesv
    * @since v1.0
*/
public class UserRegistrationTokenDAO {
    /**
        * Cria a tabela de tokens de confirmação de cadastro de usuários do banco de dados.
        * 
        * @author isaquesv
    */
    public static void createRegistrationTokenTable() throws SQLException {
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
        * Busca registros que tenham o mesmo valor que o token fornecido.
        * 
        * @param token         Token fornecido pela URL do usuário.
        * @return              JSONObject com o resultado da consulta e, caso dê tudo certo, as informações do usuário.
        * @author              isaquesv
    */
    public static JSONObject selectRegistrationToken(String token) throws Exception {
        JSONObject tokenConfirmationResponseJSON = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT * FROM token_confirmation_register "
                       + "WHERE token = ? AND is_used = ?";
            
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, token);
            stmt.setInt(2, 0);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");

                LocalDateTime expiresAt = rs.getTimestamp("expires_at").toLocalDateTime();
                String tokenDatabase = rs.getString("token");

                // Token válido e dentro do tempo limite
                if (expiresAt.isAfter(LocalDateTime.now()) && tokenDatabase.equals(token)) {
                    updateRegistrationTokenStatus(token);
                    
                    tokenConfirmationResponseJSON.put("isTokenValid", true);
                    tokenConfirmationResponseJSON.put("name", name);
                    tokenConfirmationResponseJSON.put("email", email);
                    tokenConfirmationResponseJSON.put("password", password);
                    tokenConfirmationResponseJSON.put("message", "Token validado com sucesso!");
                } else {
                    tokenConfirmationResponseJSON.put("isTokenValid", false);
                    tokenConfirmationResponseJSON.put("message", "Sentimos muito! Este link de confirmação de cadastro está expirado. Por favor, solicite o envio de outro e-mail para concluir seu cadastro. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
                }
            } else {
                tokenConfirmationResponseJSON.put("isTokenValid", false);
                tokenConfirmationResponseJSON.put("message", "Sentimos muito! Este link de confirmação de cadastro está incorreto ou expirado. Por favor, solicite o envio de outro e-mail para concluir seu cadastro. Se precisar de ajuda, entre em contato com o suporte: <a href='https://github.com/isaquesv/user-auth-platform/issues'>clique aqui</a>.");
            }

            rs.close();
            stmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return tokenConfirmationResponseJSON;
    }
    
    /**
        * Cadastra um token de confirmação de cadastro de usuário.
        * 
        * @param token         Token gerado para a confirmação de cadastro do usuário.
        * @param name          Nome fornecido pelo usuário.
        * @param email         E-mail fornecido pelo usuário.
        * @param password         Senha, hash, fornecida pelo usuário.
        * @return              Boolean com o resultado da execução da inserção, caso dê certo retorna true, caso contrário retorna false.
        * @author              isaquesv
    */
    public static boolean insertRegistrationToken(String token, String name, String email, String password) throws SQLException {
        boolean isTokenRegistered = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "INSERT INTO token_confirmation_register (token, name, email, password, is_used, expires_at) "
                       + "VALUES(?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, token);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setInt(5, 0);
            // Define o timestamp de expiração (15 minutos a partir de agora)
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now().plus(15, ChronoUnit.MINUTES)));

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
        * Atualiza o status de um determinado token de confirmação de cadastro de usuário
        * 
        * @param token         Token de confirmação de cadastro do usuário fornecido pela URL do usuário.
        * @return              Boolean com o resultado da execução da atualização do status do token, caso dê certo retorna true, caso contrário retorna false.
        * @author              isaquesv
    */
    public static boolean updateRegistrationTokenStatus(String token) {
        boolean isTokenUpdated = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
            
            // Prepara o comando SQL de seleção
            PreparedStatement pstmt = databaseConnection.prepareStatement(
                "UPDATE token_confirmation_register " +
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