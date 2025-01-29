<%-- 
    Document   : cadastro
    Author     : isaquesv
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Cadastro - User Auth Platform</title>
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
            <div id="divRegistrationForm">
                <h1 class="text-center fs-2">Cadastrar-se</h1>
                <hr class="mt-4">
                
                <section class="mt-3">
                    <label for="inputName">Nome de usuário</label>
                    <input type="text" id="inputName" class="pt-2 pb-2 ps-3 pe-3 mt-2 mb-2" maxlength="30" placeholder="usuario_123">
                    <p id="warningName" class="form-text text-danger"></p>
                </section>
                
                <section>
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
                
                <section>
                    <label for="inputConfirmPassword">Confirmar senha</label>
                    <section class="mt-2 mb-2">
                        <input type="password" class="pt-2 pb-2 ps-3 pe-3" id="inputConfirmPassword" naxlength="30" placeholder="********">
                        <i class="bi bi-eye fs-4 ms-1 p-1 ps-2 pe-2" id="viewOrHideConfirmPassword"></i>
                    </section>                
                    <p id="warningConfirmPassword" class="form-text text-danger"></p>
                </section>
                
                <input type="submit" class="btn btn-success pt-2 pb-2 mt-3 mb-4" id="buttonRegisterUser" value="Cadastrar">
                <p class="text-center">Já possui uma conta? <a href='login.jsp'>Faça login</a>.</p>
            </div>

            <div class="d-none" id="divVerificationCode">
                <h1 class="text-center fs-2">Confirmação Necessária</h1>
                <hr class="mt-4">
                
                <p class="warning ps-4 pe-4 pt-3 pb-3" id="warningConfirmationRequired"></p>
                <p class="warning ps-4 pe-4 pt-3 pb-3">Não encontrou o e-mail? Verifique sua caixa de <b>spam</b> ou <b>lixo eletrônico</b>.</p>
                <p class="warning ps-4 pe-4 pt-3 pb-3">Se necessário, você pode solicitar o reenvio do e-mail <b>clicando no botão abaixo</b>.</p>

                <input type="submit" class="btn btn-success pt-2 pb-2 mt-3 mb-2" id="buttonResendConfirmationEmail" value="Reenviar e-mail">
                <hr>
                <button class="btn btn-primary pt-3 pb-3" id="buttonShowRegistrationForm">Deseja alterar alguma informação? Clique aqui.</button>
            </div>
        </main>
        
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
        <script src="js/register.js"></script>
    </body>
</html>