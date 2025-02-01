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
                String registerToken = request.getParameter("RT");
                String errorMessage = "";

                if (registerToken == null || registerToken.isEmpty()) {
                    response.sendRedirect("index.jsp");
                } else if (registerToken.length() != 40 || !registerToken.matches("^[a-zA-Z0-9]+$")) {
                    errorMessage = "O token fornecido é inválido. Certifique-se de que o link está correto e não contém caracteres ausentes ou inválidos.";
                }
                
                %>
                <main class="p-4 mt-5 mb-5 ms-4 me-4 shadow">
                    <div id="invalidTokenContainer">
                        <p id="invalidTokenMessage"><%= errorMessage %></p>
                    </div>
                </main>
                <%
            }
        %>
            
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
        <script src="js/verify-register-token.js"></script>
    </body>
</html>
