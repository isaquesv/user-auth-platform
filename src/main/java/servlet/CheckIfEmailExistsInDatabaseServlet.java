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
        JSONObject emailExistenceResponse = new JSONObject();
        
        try {
            if (email == null || email.trim().isEmpty()) {
                emailExistenceResponse.put("isEmailExistsInDatabase", false);
                emailExistenceResponse.put("message", "Houve um erro inesperado ao enviar o parâmetro (e-mail). Tente novamente.");
            } else {
                User.createUserTable();
                // Verificando se o e-mail esta cadastrado no banco de dados
                emailExistenceResponse = User.getEmailByUser(email);
            }
        } catch (Exception ex) {
            emailExistenceResponse.put("isEmailExistsInDatabase", false);
            emailExistenceResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(emailExistenceResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar se o e-mail esta cadastrado no banco de dados.";
    }
}