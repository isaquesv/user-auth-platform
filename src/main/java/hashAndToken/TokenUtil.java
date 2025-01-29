package hashAndToken;

import java.security.SecureRandom;

/**
    * Classe responsável por gerenciar a criação de token.
    * 
    * @author isaquesv
    * @since v1.0
*/
public class TokenUtil {
    /**
        * Cria um token de validação para confirmar o cadastro de usuário.
        * 
        * @return              O token de validação para confirmar o cadastro de usuário.
        * @author              isaquesv
    */
    public static String generateToken() {
        String availableCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int tokenSize = 40;
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(40);

        for (int i = 0; i < tokenSize; i++) {
            token.append(availableCharacters.charAt(random.nextInt(availableCharacters.length())));
        }
                            
        return token.toString();
    }
}