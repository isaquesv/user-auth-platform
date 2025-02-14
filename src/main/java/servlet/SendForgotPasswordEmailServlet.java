package servlet;

import api.brevo.ForgotPasswordEmailManager;
import database.User;
import hashAndToken.*;
import database.ForgotPasswordToken;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "SendForgotPasswordEmailServlet", urlPatterns = {"/SendForgotPasswordEmailServlet"})
public class SendForgotPasswordEmailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        JSONObject forgotPasswordEmailJsonResponse = new JSONObject();
        
        try {
            if (email == null || email.trim().isEmpty()) {
                forgotPasswordEmailJsonResponse.put("isForgotPasswordEmailSent", false);
                forgotPasswordEmailJsonResponse.put("message", "Houve um erro inesperado ao enviar o parâmetro (e-mail). Tente novamente.");
            } else {
                ForgotPasswordToken.createForgotPasswordTokenTable();
                // Capturando o nome do usuário, com base no seu e-mail, e gerando um token de esqueci minha senha
                String name = User.getNameByUser(email);
                String forgotPasswordToken = TokenUtil.generateToken();
                boolean isTokenRegistered = ForgotPasswordToken.addForgotPasswordToken(name, email, forgotPasswordToken);

                if (isTokenRegistered == true) {
                    // Envia o e-mail de solicitação de alteração de senha para o e-mail do usuário
                    forgotPasswordEmailJsonResponse = ForgotPasswordEmailManager.sendForgotPasswordEmail(name, email, forgotPasswordToken);
                } else {
                    forgotPasswordEmailJsonResponse.put("isForgotPasswordEmailSent", false);
                    forgotPasswordEmailJsonResponse.put("message", "Houve um erro inesperado ao registrar o token. Tente novamente.");                    
                }
            }
        } catch (Exception ex) {
            forgotPasswordEmailJsonResponse.put("isForgotPasswordEmailSent", false);
            forgotPasswordEmailJsonResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(forgotPasswordEmailJsonResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para enviar o e-mail de alteração de senha.";
    }
}