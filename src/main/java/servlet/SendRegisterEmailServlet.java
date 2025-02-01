package servlet;

import api.brevo.RegisterEmailManager;
import hashAndToken.*;
import database.RegisterToken;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "SendRegisterEmailServlet", urlPatterns = {"/SendRegisterEmailServlet"})
public class SendRegisterEmailServlet extends HttpServlet {
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
        JSONObject registerEmailResponse = new JSONObject();
        
        try {
            if ((name == null || name.trim().isEmpty()) || (email == null || email.trim().isEmpty()) || (password == null || password.trim().isEmpty())) {
                registerEmailResponse.put("isRegisterEmailSent", false);
                registerEmailResponse.put("message", "Houve um erro inesperado ao enviar os parâmetros (nome, e-mail, senha). Tente novamente.");
            } else {
                RegisterToken.createRegisterTokenTable();
                // Gerando um token de cadastro de usuário e um hash, com base na senha do usuário
                String registerToken = TokenUtil.generateToken();
                String hash = HashUtil.getHash(password);
                boolean isTokenRegistered = RegisterToken.addRegisterToken(registerToken, name, email, hash);

                if (isTokenRegistered == true) {
                    // Envia o e-mail de confirmação de cadastro para o e-mail do usuário
                    registerEmailResponse = RegisterEmailManager.sendRegisterEmail(name, email, registerToken);
                } else {
                    registerEmailResponse.put("isRegisterEmailSent", false);
                    registerEmailResponse.put("message", "Houve um erro inesperado ao registrar o token. Tente novamente.");                    
                }
            }
        } catch (Exception ex) {
            registerEmailResponse.put("isRegisterEmailSent", false);
            registerEmailResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(registerEmailResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para enviar o e-mail de confirmação de cadastro.";
    }
}