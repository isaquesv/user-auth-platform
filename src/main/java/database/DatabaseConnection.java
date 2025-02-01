package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
    * Classe responsável por gerenciar a conexão com o banco de dados.
    * 
    * @author isaquesv
    * @since v1.0
*/
public class DatabaseConnection {
    /**
        * @param CLASS_NAME    Nome da classe do SQLite.
        * @param URL           URL do banco de dados.
    */
    public static final String CLASS_NAME = "org.sqlite.JDBC";
    public static final String URL = "jdbc:sqlite:user_auth_plataform.db?journal_mode=WAL";

    
    /**
        * Cria a conexão com o banco de dados.
        * 
        * @return              A conexão com o banco de dados.
        * @author              isaquesv
    */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(CLASS_NAME);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado.");
            e.printStackTrace();
        }
        
        Connection databaseConnection = DriverManager.getConnection(URL);
        return databaseConnection;  
    }
}