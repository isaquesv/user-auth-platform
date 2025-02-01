package servlet;

import database.RegisterToken;
import database.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

/** @author isaquesv */
@WebServlet(name = "CheckIfRegisterTokenExistsInDatabaseServlet", urlPatterns = {"/CheckIfRegisterTokenExistsInDatabaseServlet"})
public class CheckIfRegisterTokenExistsInDatabaseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String registerToken = request.getParameter("registerToken");
        JSONObject registerTokenExistenceResponse = new JSONObject();
        
        try {
            if (registerToken == null || registerToken.trim().isEmpty()) {
                registerTokenExistenceResponse.put("isRegisterTokenExistsInDatabase", false);
                registerTokenExistenceResponse.put("message", "Houve um erro inesperado ao enviar o parâmetro (token). Tente novamente.");
            } else {
                RegisterToken.createRegisterTokenTable();
                // Verificando se o token de cadastro de usuário existe no banco de dados e se é válido
                registerTokenExistenceResponse = RegisterToken.getRegisterToken(registerToken);
                
                if (registerTokenExistenceResponse.getBoolean("isRegisterTokenExistsInDatabase")) {
                    User.createUserTable();
                    
                    String name = registerTokenExistenceResponse.getString("name");
                    String email = registerTokenExistenceResponse.getString("email");
                    String password = registerTokenExistenceResponse.getString("password");
                    boolean isUserRegistered = User.addUser(name, email, password);
                    
                    if (isUserRegistered) {
                        // Iniciando uma sessão com os dados do usuário, caso o token de cadastro exista no banco de dados e seja válido
                        HttpSession loginSession = request.getSession();
                        loginSession.setAttribute("isUserLoggedIn", true);
                        loginSession.setAttribute("name", registerTokenExistenceResponse.getString("name"));
                        loginSession.setAttribute("email", registerTokenExistenceResponse.getString("email"));
                    }
                }
            }
        } catch (Exception ex) {
            registerTokenExistenceResponse.put("isRegisterTokenExistsInDatabase", false);
            registerTokenExistenceResponse.put("message", "Erro inesperado: " + ex.getMessage());
        }
        
        response.getWriter().write(registerTokenExistenceResponse.toString());
    }

    @Override
    public String getServletInfo() {
        return "Servlet para verificar se o token de cadastro de usuário existe no banco de dados e se é válido.";
    }
}