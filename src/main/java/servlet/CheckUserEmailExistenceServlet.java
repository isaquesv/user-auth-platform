package servlet;

import database.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "CheckUserEmailExistenceServlet", urlPatterns = {"/CheckUserEmailExistenceServlet"})
public class CheckUserEmailExistenceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        JSONObject emailSelectResultJSON = new JSONObject();
        
        try {
            if (email == null || email.trim().isEmpty()) {
                emailSelectResultJSON.put("isEmailAvailable", false);
                emailSelectResultJSON.put("message", "Houve um erro inesperado ao enviar o parâmetro (e-mail). Tente novamente.");
            } else {
                UserDAO.createUserTable();
                // Verificando se o e-mail esta disponível
                emailSelectResultJSON = UserDAO.selectEmailUser(email);
            }
        } catch (Exception ex) {
            emailSelectResultJSON.put("isEmailAvailable", false);
            emailSelectResultJSON.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(emailSelectResultJSON.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar a disponibilidade do e-mail.";
    }
}