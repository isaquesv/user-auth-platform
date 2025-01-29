package servlet;

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

/**  @author isaquesv */
@WebServlet(name = "VerifyUserLoginServlet", urlPatterns = {"/VerifyUserLoginServlet"})
public class VerifyUserLoginServlet extends HttpServlet {
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
        JSONObject userSelectResultJSON = new JSONObject();
        
        try {
            if ((email == null || email.trim().isEmpty()) || password == null || password.trim().isEmpty()) {
                userSelectResultJSON.put("isLoginVerifiedSuccessfully", false);
                userSelectResultJSON.put("message", "Houve um erro inesperado ao enviar os parâmetros (e-mail, senha). Tente novamente.");
            } else {
                UserDAO.createUserTable();
                // Verificando se o login é válido
                userSelectResultJSON = UserDAO.selectUser(email, password);
                
                if (userSelectResultJSON.getBoolean("isLoginVerifiedSuccessfully")) {
                    // Iniciando uma sessão com os dados do usuário, caso o login seja válido
                    HttpSession loginSession = request.getSession();
                    loginSession.setAttribute("isUserLoggedIn", true);
                    loginSession.setAttribute("name", userSelectResultJSON.getString("name"));
                    loginSession.setAttribute("email", email);
                }
            }
        } catch (Exception ex) {
            userSelectResultJSON.put("isLoginVerifiedSuccessfully", false);
            userSelectResultJSON.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(userSelectResultJSON.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar a validade do login.";
    }
}