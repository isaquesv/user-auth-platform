package servlet;

import api.brevo.UserRegistrationEmailManager;
import hashAndToken.*;
import database.UserRegistrationTokenDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "SendUserRegistrationEmailServlet", urlPatterns = {"/SendUserRegistrationEmailServlet"})
public class SendUserRegistrationEmailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        JSONObject emailSendResultJSON = new JSONObject();
        
        try {
            if ((name == null || name.trim().isEmpty()) || (email == null || email.trim().isEmpty()) || (password == null || password.trim().isEmpty())) {
                emailSendResultJSON.put("isEmailSentSuccessfully", false);
                emailSendResultJSON.put("message", "Houve um erro inesperado ao enviar os parâmetros (nome, e-mail, senha). Tente novamente.");
            } else {
                UserRegistrationTokenDAO.createRegistrationTokenTable();
                // Gerando um token e um hash, com base na senha do usuário
                String token = TokenUtil.generateToken();
                String hash = HashUtil.getHash(password);

                boolean isTokenRegistered = UserRegistrationTokenDAO.insertRegistrationToken(token, name, email, hash);
                if (isTokenRegistered == true) {
                    // Envia o e-mail de confirmação de cadastro para o e-mail do usuário
                    emailSendResultJSON = UserRegistrationEmailManager.sendRegistrationConfirmationEmail(name, email, token);
                } else {
                    emailSendResultJSON.put("isEmailSentSuccessfully", false);
                    emailSendResultJSON.put("message", "Houve um erro inesperado ao registrar o token. Tente novamente.");                    
                }
            }
        } catch (Exception ex) {
            emailSendResultJSON.put("isEmailSentSuccessfully", false);
            emailSendResultJSON.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(emailSendResultJSON.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para enviar o e-mail de confirmação de cadastro.";
    }
}