<%-- 
    Document   : index
    Author     : isaquesv

    Categoria: Java with Maven
    JDK: 19.0.2
    source/binary format: 19
    IDE: NetBeans 22
    Server: Glassfish Server 7.0
    Java EE Version: Jakarta EE 10 Web
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome - User Auth Platform</title>
        <%@include file="WEB-INF/JSPF/html-head-libs.jspf"%>
    </head>
    <body>
        <%
            HttpSession loginSession = request.getSession(false);

            if (loginSession != null && Boolean.TRUE.equals(loginSession.getAttribute("isUserLoggedIn"))) {
                response.sendRedirect("home.jsp");
            }
        %>
        
        <main class="p-4 mt-5 mb-5 ms-4 me-4 shadow">
            <p>Ainda não possui uma conta? <a href='register.jsp'>Cadastre-se</a>.</p>
            <hr>
            <p>Já possui uma conta? <a href='login.jsp'>Faça login</a>.</p>
            <hr>
            <p>Esqueceu sua senha? <a href='forgot-password.jsp'>Clique aqui</a>.</p>
        </main>
        
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
    </body>
</html>