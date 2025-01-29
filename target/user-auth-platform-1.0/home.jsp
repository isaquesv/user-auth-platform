<%-- 
    Document   : cadastro
    Author     : isaquesv
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Início - User Auth Platform</title>
        <%@include file="WEB-INF/JSPF/html-head-libs.jspf"%>
    </head>
    <body>
        <%
            HttpSession loginSession = request.getSession(false);

            if (loginSession != null && Boolean.TRUE.equals(loginSession.getAttribute("isUserLoggedIn"))) {
                String name = (String) loginSession.getAttribute("name");
                String email = (String) loginSession.getAttribute("email");
                
                %>
                <main class="p-4 mt-5 mb-5 ms-4 me-4 shadow">
                    <p><b>Nome de usuário:</b> <%= name %></p>
                    <p><b>E-mail:</b> <%= email %></p>
                    <p><b><a href="TerminateUserSessionServlet">Sair</a></b></p>
                </main>
                <%
            } else {
                response.sendRedirect("index.jsp");
            }
        %>
        
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
    </body>
</html>
