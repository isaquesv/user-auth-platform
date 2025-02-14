package servlet;

import database.ForgotPasswordToken;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(name = "CheckIfForgotPasswordTokenExistsInDatabaseServlet", urlPatterns = {"/CheckIfForgotPasswordTokenExistsInDatabaseServlet"})
public class CheckIfForgotPasswordTokenExistsInDatabaseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String forgotPasswordToken = request.getParameter("forgotPasswordToken");
        JSONObject forgotPasswordTokenExistenceJsonResponse = new JSONObject();
        
        try {
            if (forgotPasswordToken == null || forgotPasswordToken.trim().isEmpty()) {
                forgotPasswordTokenExistenceJsonResponse.put("isForgotPasswordTokenExistsInDatabase", false);
                forgotPasswordTokenExistenceJsonResponse.put("message", "Houve um erro inesperado ao enviar o parâmetro (token). Tente novamente.");
            } else {
                ForgotPasswordToken.createForgotPasswordTokenTable();
                // Verificando se o token de solicitação de alteração de senha existe no banco de dados e se é válido
                forgotPasswordTokenExistenceJsonResponse = ForgotPasswordToken.getForgotPasswordToken(forgotPasswordToken);
            }
        } catch (Exception ex) {
            forgotPasswordTokenExistenceJsonResponse.put("isForgotPasswordTokenExistsInDatabase", false);
            forgotPasswordTokenExistenceJsonResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(forgotPasswordTokenExistenceJsonResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar se o token de alteração de senha existe no banco de dados e se é válido.";
    }
}