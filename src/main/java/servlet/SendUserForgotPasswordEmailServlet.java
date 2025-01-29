package servlet;

import api.brevo.UserForgotPasswordEmailManager;
import database.UserDAO;
import hashAndToken.*;
import database.UserForgotPasswordTokenDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "SendUserForgotPasswordEmailServlet", urlPatterns = {"/SendUserForgotPasswordEmailServlet"})
public class SendUserForgotPasswordEmailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        JSONObject emailSendResultJSON = new JSONObject();
        
        try {
            if (email == null || email.trim().isEmpty()) {
                emailSendResultJSON.put("isEmailSentSuccessfully", false);
                emailSendResultJSON.put("message", "Houve um erro inesperado ao enviar o parâmetro (e-mail). Tente novamente.");
            } else {
                UserForgotPasswordTokenDAO.createForgotPasswordTokenTable();
                // Capturando o nome do usuário, com base no seu e-mail, e gerando um token
                String name = UserDAO.selectNameUser(email);
                String token = TokenUtil.generateToken();

                boolean isTokenRegistered = UserForgotPasswordTokenDAO.insertForgotPasswordToken(name, email, token);
                if (isTokenRegistered == true) {
                    // Envia o e-mail de esqueci minha senha para o e-mail do usuário
                    emailSendResultJSON = UserForgotPasswordEmailManager.sendForgotPasswordEmail(name, email, token);
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
        return "Servlet para enviar o e-mail de alteração de senha.";
    }
}