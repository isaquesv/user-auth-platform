package servlet;

import database.UserDAO;
import database.UserForgotPasswordTokenDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "ChangeUserPassword", urlPatterns = {"/ChangeUserPassword"})
public class ChangeUserPassword extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        JSONObject passwordChangeResultJSON = new JSONObject();
        
        try {
            if ((token == null || token.trim().isEmpty()) || password == null || password.trim().isEmpty()) {
                passwordChangeResultJSON.put("isPasswordChanged", false);
                passwordChangeResultJSON.put("message", "Houve um erro inesperado ao enviar os parâmetros (token, senha). Tente novamente.");
            } else {
                UserDAO.createUserTable(); 
                // Capturando o nome e e-mail do usuário com base em seu token de alteração de senha
                JSONObject user = UserForgotPasswordTokenDAO.selectNameAndEmailForgotPasswordToken(token);
                
                if (user.getBoolean("isNameAndEmailFound")) {
                    String name = user.getString("nameAssociatedWithToken");
                    String email = user.getString("emailAssociatedWithToken");
                    // Alterando a senha do usuário
                    passwordChangeResultJSON = UserDAO.updatePassword(email, password);
                    
                    HttpSession loginSession = request.getSession();
                    loginSession.setAttribute("isUserLoggedIn", true);
                    loginSession.setAttribute("name", name);
                    loginSession.setAttribute("email", email);
                } else {
                    passwordChangeResultJSON.put("isPasswordChanged", false);
                    passwordChangeResultJSON.put("message", "Houve um erro inesperado ao tentar capturar seu e-mail. Tente novamente.");
                }
                
            }
        } catch (Exception ex) {
            passwordChangeResultJSON.put("isPasswordChanged", false);
            passwordChangeResultJSON.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(passwordChangeResultJSON.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para alterar a senha do usuário.";
    }
}