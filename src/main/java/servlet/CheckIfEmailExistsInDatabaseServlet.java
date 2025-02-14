package servlet;

import database.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "CheckIfEmailExistsInDatabaseServlet", urlPatterns = {"/CheckIfEmailExistsInDatabaseServlet"})
public class CheckIfEmailExistsInDatabaseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        JSONObject emailExistenceJsonResponse = new JSONObject();
        
        try {
            if (email == null || email.trim().isEmpty()) {
                emailExistenceJsonResponse.put("isEmailInDatabase", false);
                emailExistenceJsonResponse.put("message", "Houve um erro inesperado ao enviar o parâmetro (e-mail). Tente novamente.");
            } else {
                User.createUserTable();
                // Verificando se o e-mail esta cadastrado no banco de dados
                emailExistenceJsonResponse = User.getEmailByUser(email);
            }
        } catch (Exception ex) {
            emailExistenceJsonResponse.put("isEmailInDatabase", false);
            emailExistenceJsonResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(emailExistenceJsonResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar se o e-mail esta cadastrado no banco de dados.";
    }
}