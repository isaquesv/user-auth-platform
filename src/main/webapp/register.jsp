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
            <div id="registerContainer">
                <h1 class="text-center fs-2">Cadastrar-se</h1>
                <hr class="mt-4">
                
                <section class="mt-3" id="nameInputSection">
                    <label for="nameInput" id="nameLabel">Nome de usuário</label>
                    <input type="text" id="nameInput" class="pt-2 pb-2 ps-3 pe-3 mt-2 mb-2" maxlength="30" placeholder="usuario_123">
                    <p id="nameMessage" class="form-text text-danger"></p>
                </section>
                
                <section id="emailInputSection">
                    <label for="emailInput" id="emailLabel">E-mail</label>
                    <input type="email" max id="emailInput" class="pt-2 pb-2 ps-3 pe-3 mt-2 mb-2" maxlength="255" placeholder="usuario@dominio.com">
                    <p id="emailMessage" class="form-text text-danger"></p>
                </section>
                
                <section id="passwordInputSection">
                    <label for="passwordInput" id="passwordLabel">Senha</label>
                    <section class="mt-2 mb-2">
                        <input type="password" id="passwordInput" class="pt-2 pb-2 ps-3 pe-3" maxlength="30" placeholder="********">
                        <i class="bi bi-eye fs-4 ms-1 p-1 ps-2 pe-2" id="viewOrHidePasswordInputIcon"></i>
                    </section>
                    <p id="passwordMessage" class="form-text text-danger"></p>
                </section>
                
                <section id="confirmPasswordInputSection">
                    <label for="confirmPasswordInput" id="confirmPasswordLabel">Confirmar senha</label>
                    <section class="mt-2 mb-2">
                        <input type="password" class="pt-2 pb-2 ps-3 pe-3" id="confirmPasswordInput" naxlength="30" placeholder="********">
                        <i class="bi bi-eye fs-4 ms-1 p-1 ps-2 pe-2" id="viewOrHideConfirmPasswordInputIcon"></i>
                    </section>                
                    <p id="confirmPasswordMessage" class="form-text text-danger"></p>
                </section>
                
                <input type="submit" class="btn btn-success pt-2 pb-2 mt-3 mb-4" id="sendRegisterEmailButton" value="Cadastrar">
                <p class="text-center">Já possui uma conta? <a href='login.jsp'>Faça login</a>.</p>
            </div>

            <div class="d-none" id="registerEmailVerificationRequiredContainer">
                <h1 class="text-center fs-2">Confirmação Necessária</h1>
                <hr class="mt-4">
                
                <p class="warning ps-4 pe-4 pt-3 pb-3" id="registerEmailVerificationRequiredMessage"></p>
                <p class="warning ps-4 pe-4 pt-3 pb-3">Não encontrou o e-mail? Verifique sua caixa de <b>spam</b> ou <b>lixo eletrônico</b>.</p>
                <p class="warning ps-4 pe-4 pt-3 pb-3">Se necessário, você pode solicitar o reenvio do e-mail <b>clicando no botão abaixo</b>.</p>

                <input type="submit" class="btn btn-success pt-2 pb-2 mt-3 mb-2" id="resendRegisterEmailButton" value="Reenviar e-mail">
                <hr>
                <button class="btn btn-primary pt-3 pb-3" id="backToRegisterFormButton">Deseja alterar alguma informação? Clique aqui.</button>
            </div>
        </main>
        
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
        <script src="js/register.js"></script>
    </body>
</html>