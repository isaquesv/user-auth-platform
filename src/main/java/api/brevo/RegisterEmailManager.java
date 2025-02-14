package api.brevo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
    * Classe responsável por gerenciar o uso da API Brevo para integrar envios de
    * e-mails de solicitação de confirmação de cadastro de usuário.
    * 
    * @author isaquesv
    * @since v1.0
*/
public class RegisterEmailManager {
    /**
        * @param API_URL       URL da API Brevo, necessária para realizar o envio dos e-mails.
        * @param API_KEY       Chave da API Brevo, também necessária para realizar o envio dos e-mails.
        * @param API_EMAIL     E-mail do remetente, deve ser o mesmo que o registrado na conta Brevo que gerou a API KEY.
        * @param API_NAME      Nome do remetente, não precisa ser o mesmo que o nome do e-mail.
        * @param HTTP_CLIENT   HttpClient configurado para desabilitar a validação de certificados SSL/TLS.
    */
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String API_KEY = "<<Insira-Sua-API-KEY-Aqui>>";
    private static final String API_EMAIL = "<<Insira-Seu-Email-Aqui>>";
    private static final String API_NAME = "UAP Team";
    private static final HttpClient HTTP_CLIENT = SelfCertificatedServer.createHttpClient();


    /**
        * Cria a estrutura HTML do e-mail de confirmação de cadastro de usuários.
        * 
        * @param name               Nome do usuário.
        * @param RegisterToken      Token de confirmação de cadastro do usuário.
        * @return                   String com a estrutura HTML do e-mail de cadastro do usuário.
        * @author                   isaquesv
    */
    public static String createRegisterEmailHTMLBody(String name, String registerToken) {
        String emailHTMLBody =
                "<div style='max-width: 600px; margin: 20px auto; background: #FDFDFD; border: 1px solid #E0E0E0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); font-family: Arial, sans-serif;'>" +
                    "<div style='background-color: #0056B3; color: white; text-align: center; padding: 20px; font-size: 24px; font-weight: bold;'>" +
                        "Confirmação Necessária" +
                    "</div>" +
                    "<div style='padding: 25px; color: #333333;'>" +
                        "<p style='margin: 0 0 15px; line-height: 1.8;'>Prezado(a) " + name + ",</p>" +
                        "<p style='margin: 0 0 15px; line-height: 1.8;'>Recebemos uma tentativa de registro em nossa plataforma <b>UAP (User Auth Platform)</b> vinculada a este endereço de e-mail. Para finalizar seu cadastro e aproveitar todos os recursos da nossa plataforma, basta confirmar clicando no link abaixo:</p>" +
                        "<p style='margin: 0 0 15px; line-height: 1.8;'><a href='localhost:8080/user-auth-platform/verify-register-token.jsp?RT=" + registerToken + "'>Confirmar Cadastro</a></p>" +
                        "<p style='margin: 0 0 15px; line-height: 1.8;'>Se você não reconhece esta tentativa de registro, simplesmente ignore esta mensagem.</p>" +
                    "</div>" +
                    "<div style='background-color: #F4F4F4; padding: 15px; text-align: center; font-size: 14px; color: #777777; border-top: 1px solid #DDDDDD;'>" +
                        "Atenciosamente,<br>" +
                        "<b>UAP Team</b>" +
                    "</div>" +
                "</div>";
        
        return emailHTMLBody;
    }
    
    /**
        * Envia o e-mail de confirmação de cadastro.
        * 
        * @param name               Nome do usuário.
        * @param email              E-mail do usuário.
        * @param registerToken      Token gerado para a confirmação de cadastro do usuário.
        * @return                   JSONObject com o resultado do envio do e-mail de cadastro do usuário.
        * @author                   isaquesv
    */
    public static JSONObject sendRegisterEmail(String name, String email, String registerToken) {
        JSONObject registerEmailJsonResponse = new JSONObject();
        
        String emailHTMLBody = createRegisterEmailHTMLBody(name, registerToken);
        String completeMessage  = String.format("""
            {
                "sender": {
                    "email": "%s",
                    "name": "%s"
                },
                "to": [
                    {
                        "email": "%s",
                        "name": "Prezado(a) %s"
                    }
                ],
                "subject": "UAP - Confirmação de Cadastro",
                "htmlContent": "%s"
            }
        """, API_EMAIL, API_NAME, email, name, emailHTMLBody);
        
        try {
            // Criar requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("api-key", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(completeMessage))
                .build();

            // Enviar requisição e processar resposta
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // Sucesso no envio do email
            if (response.statusCode() == 201) {
                registerEmailJsonResponse.put("isRegisterEmailSent", true);
                registerEmailJsonResponse.put("message", "Enviamos um e-mail de confirmação para: <b>" + email + "</b>. Para concluir o seu cadastro, acesse o e-mail e clique no link de confirmação.");
            } else {
                registerEmailJsonResponse.put("isRegisterEmailSent", false);
                registerEmailJsonResponse.put("message", "Erro ao enviar e-mail. Código de erro: " + response.statusCode() + ". Detalhes: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            registerEmailJsonResponse.put("isRegisterEmailSent", false);
            registerEmailJsonResponse.put("message", "Erro ao enviar e-mail: " + e.getMessage());
        }
        
        return registerEmailJsonResponse;
    }
}