<%-- 
    Document   : recover-password
    Author     : isaquesv
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Recuperar senha - User Auth Platform</title>
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
            <div id="divForgotPasswordForm">
                <h1 class="text-center fs-2">Esqueceu sua senha?</h1>
                <hr class="mt-4">
                
                <p class="warning ps-4 pe-4 pt-3 pb-3" id="warningConfirmationRequired">Digite o seu e-mail no campo abaixo e lhe enviaremos uma mensagem contendo o passo-a-passo de como prosseguir.</p>
                
                <section id="sectionInputEmail" class="mt-4">
                    <label for="inputEmail">E-mail</label>
                    <input type="email" max id="inputEmail" class="pt-2 pb-2 ps-3 pe-3 mt-2 mb-2" maxlength="255" placeholder="usuario@dominio.com">
                    <p id="warningEmail" class="form-text text-danger"></p>
                </section>
                
                <input type="submit" class="btn btn-success pt-2 pb-2 mt-3 mb-4" id="buttonSendForgotPasswordEmail" value="Enviar e-mail">
                <input type="submit" class="d-none btn btn-success pt-2 pb-2 mt-3 mb-4" id="buttonResendForgotPasswordEmail" value="Reenviar e-mail">
                <hr>
                <p class="text-center">Ainda não possui uma conta? <a href='register.jsp'>Cadastre-se</a>.</p>
                <p class="text-center">Já possui uma conta? <a href='login.jsp'>Faça login</a>.</p>
            </div>
        </main>
        
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
        <script src="js/forgot-password.js"></script>
    </body>
</html>
