<%-- 
    Document   : login
    Author     : isaquesv
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login - User Auth Platform</title>
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
            <div id="divLoginForm">
                <h1 class="text-center fs-2">Login</h1>
                <hr class="mt-4">
                
                <section class="mt-3">
                    <label for="inputEmail">E-mail</label>
                    <input type="email" max id="inputEmail" class="pt-2 pb-2 ps-3 pe-3 mt-2 mb-2" maxlength="255" placeholder="usuario@dominio.com">
                    <p id="warningEmail" class="form-text text-danger"></p>
                </section>
                
                <section>
                    <label for="inputPassword">Senha</label>
                    <section class="mt-2 mb-2">
                        <input type="password" id="inputPassword" class="pt-2 pb-2 ps-3 pe-3" maxlength="30" placeholder="********">
                        <i class="bi bi-eye fs-4 ms-1 p-1 ps-2 pe-2" id="viewOrHidePassword"></i>
                    </section>
                    <p id="warningPassword" class="form-text text-danger"></p>
                </section>
                
                <input type="submit" class="btn btn-success pt-2 pb-2 mt-3 mb-4" id="buttonLoginUser" value="Confirmar">
                <p class="text-center">Esqueceu sua senha? <a href='forgot-password.jsp'>Clique aqui</a>.</p>
                <p class="text-center">Ainda não possui uma conta? <a href='register.jsp'>Cadastre-se</a>.</p>
            </div>
            
            <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
            <script src="js/login.js"></script>
        </main>
    </body>
</html>