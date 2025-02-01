package hashAndToken;

import org.mindrot.jbcrypt.BCrypt;

/**
    * Classe responsável por gerenciar hash.
    * @author isaquesv
    * @since v1.0
*/
public class HashUtil {
    /**
        * Gera um hash para a senha fornecida.
        * 
        * @param password      Senha fornecida pelo usuário.
        * @return              Hash gerado com base na senha informada.
        * @author              isaquesv
    */
    public static String getHash(String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        return hash;
    }
    
    /**
        * Verifica se a senha fornecida pelo usuário corresponde ao hash da senha armazenada no banco de dados.
        * 
        * @param password               Senha fornecida pelo usuário.
        * @param storedHashedPassword   Hash da senha armazenada no banco de dados.
        * @return                       Retorna true se a senha informada corresponder ao hash armazenado, caso contrário retorna false.
        * @author                       isaquesv
    */
   public static boolean checkHash(String password, String storedHashedPassword) {
       return BCrypt.checkpw(password, storedHashedPassword);
   }
}