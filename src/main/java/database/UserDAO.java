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
    * @author isaquesv
    * @since v1.0
*/
public class UserDAO {
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
        * Busca registros que tenham o mesmo valor que o e-mail e senha fornecidos.
        * 
        * @param email         E-mail fornecido pelo usuário.
        * @param password      Senha fornecida pelo usuário.
        * @return              JSONObject com o resultado da consulta e, caso dê tudo certo, o nome do usuário.
        * @author              isaquesv
    */
    public static JSONObject selectUser(String email, String password) throws Exception {
        JSONObject userSelectResultJSON = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();

            String sql = "SELECT name, password FROM user WHERE email = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String storedHash = rs.getString("password");
                boolean arePasswordsEqual = HashUtil.checkHash(password, storedHash);
                
                if (arePasswordsEqual) {
                    userSelectResultJSON.put("isLoginVerifiedSuccessfully", true);
                    userSelectResultJSON.put("message", "O login foi realizado com sucesso.");
                    userSelectResultJSON.put("name", name);
                } else {
                    userSelectResultJSON.put("isLoginVerifiedSuccessfully", false);
                    userSelectResultJSON.put("message", "O e-mail e a senha declarados não conferem.");
                }
            } else {
                userSelectResultJSON.put("isLoginVerifiedSuccessfully", false);
                userSelectResultJSON.put("message", "O e-mail e a senha declarados não conferem.");
            }

            rs.close();
            stmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            // Erro de conexão ou execução
            System.err.println(e.getMessage());
        }
        
        return userSelectResultJSON;
    }
    
    /**
        * Busca registros que tenham o mesmo valor que o e-mail fornecido, para verificar se o e-mail esta ou não em uso.
        * 
        * @param email         E-mail fornecido pelo usuário.
        * @return              JSONObject com o resultado da consulta.
        * @author              isaquesv
    */
    public static JSONObject selectEmailUser(String email) throws SQLException {
        JSONObject emailSelectResultJSON = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT email FROM user WHERE email = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                emailSelectResultJSON.put("isEmailAvailable", false);
                emailSelectResultJSON.put("message", "Este e-mail já esta em uso. Tente outro.");
            } else {
                emailSelectResultJSON.put("isEmailAvailable", true);
                emailSelectResultJSON.put("message", "Este e-mail não foi cadastrado.");
            }

            rs.close();
            stmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return emailSelectResultJSON;
    }
    
    /**
        * Busca registros que tenham o mesmo valor que o e-mail fornecido, para capturar seu respectivo nome de usuário.
        * 
        * @param email         E-mail fornecido pelo usuário.
        * @return              JSONObject com o resultado da consulta.
        * @author              isaquesv
    */
    public static String selectNameUser(String email) throws SQLException {
        String nameAssociatedWithEmail = "usuário";
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "SELECT name FROM user WHERE email = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameAssociatedWithEmail = rs.getString("name");
            }

            rs.close();
            stmt.close();
            databaseConnection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return nameAssociatedWithEmail;
    }
    
    /**
        * Cadastra um usuário.
        * 
        * @param name          Nome fornecido pelo usuário.
        * @param email         E-mail fornecido pelo usuário.
        * @param password      Senha, hash, fornecida pelo usuário.
        * @return              Boolean com o resultado da execução da inserção, caso dê certo retorna true, caso contrário retorna false.
        * @author              isaquesv
    */
    public static boolean insertUser(String name, String email, String password) throws SQLException {
        boolean isUserRegistered = false;
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
        
            String sql = "INSERT INTO user (name, email, password) VALUES(?, ?, ?)";
            PreparedStatement stmt = databaseConnection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            
            int isInsertionSuccessful = stmt.executeUpdate();
            if (isInsertionSuccessful == 1) {
                isUserRegistered = true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return isUserRegistered;
    }
    
    /**
        * Atualiza a senha de um determinado usuário, com base em seu e-mail.
        * 
        * @param email         E-mail fornecido pelo usuário.
        * @param password      Nova senha fornecida pelo usuárioo.
        * @return              Boolean com o resultado da execução da atualização de senha, caso dê certo retorna true, caso contrário retorna false.
        * @author              isaquesv
    */
    public static JSONObject updatePassword(String email, String password) {
        JSONObject passwordChangeResultJSON = new JSONObject();
        
        try {
            Connection databaseConnection = DatabaseConnection.getConnection();
            String hash = HashUtil.getHash(password);
                    
            // Prepara o comando SQL de seleção
            PreparedStatement pstmt = databaseConnection.prepareStatement(
                "UPDATE user " +
                "SET password = ? " +
                "WHERE email = ?"
            );
            pstmt.setString(1, hash);
            pstmt.setString(2, email);
            
            int isUpdatedSuccessfully = pstmt.executeUpdate(); 
            if (isUpdatedSuccessfully == 1) {
                passwordChangeResultJSON.put("isPasswordChanged", true);
                passwordChangeResultJSON.put("message", "Senha alterada com sucesso");
            } else {
                passwordChangeResultJSON.put("isPasswordChanged", false);
                passwordChangeResultJSON.put("message", "Falha ao tentar alterar a senha");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return passwordChangeResultJSON;
    }
}