package servlet;

import database.User;
import database.ForgotPasswordToken;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/ChangePasswordServlet"})
public class ChangePasswordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String forgotPasswordToken = request.getParameter("forgotPasswordToken");
        String password = request.getParameter("password");
        JSONObject changePasswordResponse = new JSONObject();
        
        try {
            if ((forgotPasswordToken == null || forgotPasswordToken.trim().isEmpty()) || password == null || password.trim().isEmpty()) {
                changePasswordResponse.put("isPasswordChanged", false);
                changePasswordResponse.put("message", "Houve um erro inesperado ao enviar os parâmetros (token, senha). Tente novamente.");
            } else {
                User.createUserTable(); 
                // Capturando o nome e e-mail do usuário com base em seu token de alteração de senha
                JSONObject user = ForgotPasswordToken.getNameAndEmailByForgotPasswordToken(forgotPasswordToken);
                
                if (user.getBoolean("isNameAndEmailFound")) {
                    String name = user.getString("nameAssociatedWithToken");
                    String email = user.getString("emailAssociatedWithToken");
                    // Alterando a senha do usuário
                    changePasswordResponse = User.setPassword(email, password);
                    
                    HttpSession loginSession = request.getSession();
                    loginSession.setAttribute("isUserLoggedIn", true);
                    loginSession.setAttribute("name", name);
                    loginSession.setAttribute("email", email);
                } else {
                    changePasswordResponse.put("isPasswordChanged", false);
                    changePasswordResponse.put("message", "Houve um erro inesperado ao tentar capturar seu e-mail. Tente novamente.");
                }
                
            }
        } catch (Exception ex) {
            changePasswordResponse.put("isPasswordChanged", false);
            changePasswordResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(changePasswordResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para alterar a senha do usuário.";
    }
}