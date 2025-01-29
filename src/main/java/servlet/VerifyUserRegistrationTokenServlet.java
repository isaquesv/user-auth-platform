package servlet;

import database.UserRegistrationTokenDAO;
import database.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "VerifyUserRegistrationTokenServlet", urlPatterns = {"/VerifyUserRegistrationTokenServlet"})
public class VerifyUserRegistrationTokenServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String token = request.getParameter("token");
        JSONObject tokenConfirmationResponseJSON = new JSONObject();
        
        try {
            if (token == null || token.trim().isEmpty()) {
                tokenConfirmationResponseJSON.put("isTokenValid", false);
                tokenConfirmationResponseJSON.put("message", "Houve um erro inesperado ao enviar o parâmetro (token). Tente novamente.");
            } else {
                UserRegistrationTokenDAO.createRegistrationTokenTable();
                // Verificando se o token é válido
                tokenConfirmationResponseJSON = UserRegistrationTokenDAO.selectRegistrationToken(token);
                
                if (tokenConfirmationResponseJSON.getBoolean("isTokenValid")) {
                    UserDAO.createUserTable();
                    boolean isUserRegistered = UserDAO.insertUser(tokenConfirmationResponseJSON.getString("name"), tokenConfirmationResponseJSON.getString("email"), tokenConfirmationResponseJSON.getString("password"));
                    
                    if (isUserRegistered) {
                        // Iniciando uma sessão com os dados do usuário, caso o token de cadastro seja válido
                        HttpSession loginSession = request.getSession();
                        loginSession.setAttribute("isUserLoggedIn", true);
                        loginSession.setAttribute("name", tokenConfirmationResponseJSON.getString("name"));
                        loginSession.setAttribute("email", tokenConfirmationResponseJSON.getString("email"));
                    }
                }
            }
        } catch (Exception ex) {
            tokenConfirmationResponseJSON.put("isTokenValid", false);
            tokenConfirmationResponseJSON.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(tokenConfirmationResponseJSON.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar a validade do token.";
    }
}