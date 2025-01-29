package servlet;

import database.UserForgotPasswordTokenDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(name = "VerifyUserForgotPasswordTokenServlet", urlPatterns = {"/VerifyUserForgotPasswordTokenServlet"})
public class VerifyUserForgotPasswordTokenServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String token = request.getParameter("token");
        JSONObject tokenForgotPasswordResponseJSON = new JSONObject();
        
        try {
            if (token == null || token.trim().isEmpty()) {
                tokenForgotPasswordResponseJSON.put("isTokenValid", false);
                tokenForgotPasswordResponseJSON.put("message", "Houve um erro inesperado ao enviar o parâmetro (token). Tente novamente.");
            } else {
                UserForgotPasswordTokenDAO.createForgotPasswordTokenTable();
                // Verificando se o token de confirmação de cadastro é válido
                tokenForgotPasswordResponseJSON = UserForgotPasswordTokenDAO.selectForgotPasswordToken(token);
            }
        } catch (Exception ex) {
            tokenForgotPasswordResponseJSON.put("isTokenValid", false);
            tokenForgotPasswordResponseJSON.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(tokenForgotPasswordResponseJSON.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar a validade do token.";
    }
}