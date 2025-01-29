package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** @author isaquesv */
@WebServlet(name = "TerminateUserSessionServlet", urlPatterns = {"/TerminateUserSessionServlet"})
public class TerminateUserSessionServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession loginSession = request.getSession(false);
        if (loginSession != null) {
            // Encerrando a sessão dde login do usuário, caso exista
            loginSession.invalidate();
        }
        
        response.sendRedirect("index.jsp");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Servlet para finalizar a sessão do usuário.";
    }
}