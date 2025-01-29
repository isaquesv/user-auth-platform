package api.brevo;

import java.net.http.HttpClient;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;

/**
    * Classe responsável por criar um HttpClient configurado para ignorar a validação de certificados SSL/TLS.
    * Essa configuração é necessária para realizar requisições na API da Brevo.
    *
    * @author isaquesv
    * @since v1.0
*/
public class SelfCertificatedServer {
    /**
        * Cria um HttpClient configurado para desabilitar a validação de certificados SSL/TLS.
        * Essa abordagem é necessária para evitar problemas ao conectar-se com a API da Brevo
        *
        * @return HttpClient configurado para ignorar a validação de certificados SSL/TLS.
        * @throws RuntimeException em caso de falha na configuração do SSLContext.
        * @author isaquesv
    */
    public static HttpClient createHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar HttpClient: " + e.getMessage());
        }
    }
}