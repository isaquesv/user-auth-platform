<%-- 
    Document   : verify-registration-token
    Author     : isaquesv
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Verificar Token de Confirmação de Cadastro - User Auth Platform</title>
        <%@include file="WEB-INF/JSPF/html-head-libs.jspf"%>
    </head>
    <body>
        <%
            HttpSession loginSession = request.getSession(false);

            if (loginSession != null && Boolean.TRUE.equals(loginSession.getAttribute("isUserLoggedIn"))) {
                response.sendRedirect("home.jsp");
            } else {
                String token = request.getParameter("token");
                String errorMessage = "";

                if (token == null || token.isEmpty()) {
                    response.sendRedirect("index.jsp");
                } else if (token.length() != 40 || !token.matches("^[a-zA-Z0-9]+$")) {
                    errorMessage = "O token fornecido é inválido. Certifique-se de que o link está correto e não contém caracteres ausentes ou inválidos.";
                }
                
                %>
                <main class="p-4 mt-5 mb-5 ms-4 me-4 shadow">
                    <p id="errorMessage"><%= errorMessage %></p>
                    <p><b><a href="index.jsp">Voltar</a></b></p>
                </main>
                <%
            }
        %>
            
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
        <script src="js/verify-user-registration-token.js"></script>
    </body>
</html>
