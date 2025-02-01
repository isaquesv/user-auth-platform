package database;

import hashAndToken.HashUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;
import hashAndToken.HashUtil;

/**
    * Classe responsável por gerenciar a tabela de usuários do banco de dados.
    * 
    * @author isaquesv
    * @since v1.0
*/
public class User {
    /**
        * Cria a tabela usuários do banco de dados.
        * 
        * @author              isaquesv
    */
    public static void createUserTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS user ("
                    + "code INTEGER PRIMARY KEY, "
                    + "name VARCHAR(30) NOT NULL, "
                    + "email VARCHAR(255) NOT NULL, "
                    + "password VARCHAR(255) NOT NULL "
                    + ")";
        
        Connection databaseConnection = DatabaseConnection.getConnection();
        Statement stmt = databaseConnection.createStatement();
        stmt.execute(sql);
    }
    
    /**
        * Verifica se o e-mail e senha fornecidos existem no banco de dados.
        * 
        * @param email         E-mail do usuário.
        * @param password      Senha do usuário.
        * @return              JSONObject com o resultado da consulta.
        * @author              isaquesv
    */
    public static JSONObject getUser(String email, String password) throws Exception {
        JSONObject loginVerificationResponse = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();

            String sql = "SELECT name, password FROM user WHERE email = ?";
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, email);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                String name = result.getString("name");
                String storedHash = result.getString("password");
                
                boolean arePasswordsEqual = HashUtil.checkHash(password, storedHash);
                
                if (arePasswordsEqual == true) {
                    loginVerificationResponse.put("isLoginCorrect", true);
                    loginVerificationResponse.put("message", "O login foi realizado com sucesso.");
                    loginVerificationResponse.put("name", name);
                } else {
                    loginVerificationResponse.put("isLoginCorrect", false);
                    loginVerificationResponse.put("message", "O e-mail e a senha declarados não conferem.");
                }
            } else {
                loginVerificationResponse.put("isLoginCorrect", false);
                loginVerificationResponse.put("message", "O e-mail e a senha declarados não conferem.");
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            // Erro de conexão ou execução
            System.err.println(e.getMessage());
        }
        
        return loginVerificationResponse;
    }
    
    /**
        * Verifica se o e-mail fornecido existe no banco de dados.
        * 
        * @param email         E-mail do usuário.
        * @return              JSONObject com o resultado da consulta.
        * @author              isaquesv
    */
    public static JSONObject getEmailByUser(String email) throws SQLException {
        JSONObject emailExistenceResponse = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT email FROM user WHERE email = ?";
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, email);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                emailExistenceResponse.put("isEmailExistsInDatabase", false);
                emailExistenceResponse.put("message", "Este e-mail já esta em uso. Tente outro.");
            } else {
                emailExistenceResponse.put("isEmailExistsInDatabase", true);
                emailExistenceResponse.put("message", "Este e-mail não foi cadastrado.");
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return emailExistenceResponse;
    }
    
    /**
        * Verifica se o e-mail fornecido existe no banco de dados. Captura o nome do usuário.
        * 
        * @param email         E-mail fornecido pelo usuário.
        * @return              String com o nome do usuário.
        * @author              isaquesv
    */
    public static String getNameByUser(String email) throws SQLException {
        String nameAssociatedWithEmail = "usuário";
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT name FROM user WHERE email = ?";
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, email);

            ResultSet result = pStmt.executeQuery();
            if (result.next()) {
                nameAssociatedWithEmail = result.getString("name");
            }

            result.close();
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return nameAssociatedWithEmail;
    }
    
    /**
        * Cadastra um usuário.
        * 
        * @param name          Nome do usuário.
        * @param email         E-mail do usuário.
        * @param password      Senha, hash, do usuário.
        * @return              Boolean com o resultado da execução da inserção.
        * @author              isaquesv
    */
    public static boolean addUser(String name, String email, String password) throws SQLException {
        boolean isUserRegistered = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "INSERT INTO user (name, email, password) VALUES(?, ?, ?)";
            PreparedStatement pStmt = databaseConnection.prepareStatement(sql);
            pStmt.setString(1, name);
            pStmt.setString(2, email);
            pStmt.setString(3, password);
            
            int result = pStmt.executeUpdate();
            if (result == 1) {
                isUserRegistered = true;
            }
            
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isUserRegistered;
    }
    
    /**
        * Atualiza a senha do usuário, com base em seu e-mail.
        * 
        * @param email         E-mail do usuário.
        * @param password      Nova senha do usuárioo.
        * @return              Boolean com o resultado da execução da atualização de senha.
        * @author              isaquesv
    */
    public static JSONObject setPassword(String email, String password) {
        JSONObject changePasswordResponse = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
            String hash = HashUtil.getHash(password);
                    
            // Prepara o comando SQL de seleção
            PreparedStatement pStmt = databaseConnection.prepareStatement(
                "UPDATE user " +
                "SET password = ? " +
                "WHERE email = ?"
            );
            pStmt.setString(1, hash);
            pStmt.setString(2, email);
            
            int result = pStmt.executeUpdate(); 
            if (result == 1) {
                changePasswordResponse.put("isPasswordChanged", true);
                changePasswordResponse.put("message", "Senha alterada com sucesso");
            } else {
                changePasswordResponse.put("isPasswordChanged", false);
                changePasswordResponse.put("message", "Falha ao tentar alterar a senha");
            }
            
            pStmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return changePasswordResponse;
    }
}