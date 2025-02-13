package servlet;

import database.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

/**  @author isaquesv */
@WebServlet(name = "VerifyLoginServlet", urlPatterns = {"/VerifyLoginServlet"})
public class VerifyLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        JSONObject loginVerificationJsonResponse = new JSONObject();
        
        try {
            if ((email == null || email.trim().isEmpty()) || password == null || password.trim().isEmpty()) {
                loginVerificationJsonResponse.put("isLoginValid", false);
                loginVerificationJsonResponse.put("message", "Houve um erro inesperado ao enviar os parâmetros (e-mail, senha). Tente novamente.");
            } else {
                User.createUserTable();
                // Verificando se o login é válido
                loginVerificationJsonResponse = User.getUser(email, password);
                
                if (loginVerificationJsonResponse.getBoolean("isLoginValid")) {
                    String name = loginVerificationJsonResponse.getString("name");
                    
                    // Iniciando uma sessão com os dados do usuário, caso o login seja válido
                    HttpSession loginSession = request.getSession();
                    loginSession.setAttribute("isUserLoggedIn", true);
                    loginSession.setAttribute("name", name);
                    loginSession.setAttribute("email", email);
                }
            }
        } catch (Exception ex) {
            loginVerificationJsonResponse.put("isLoginValid", false);
            loginVerificationJsonResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(loginVerificationJsonResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar se o login é válido.";
    }
}