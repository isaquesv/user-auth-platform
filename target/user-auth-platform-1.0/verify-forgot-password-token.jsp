<%-- 
    Document   : verify-forgot-password-token
    Created on : 29 de jan. de 2025, 01:31:44
    Author     : isaquesv
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Verificar Token de Alteração de Senha - User Auth Platform</title>
        <%@include file="WEB-INF/JSPF/html-head-libs.jspf"%>
    </head>
    <body>
        <%
            HttpSession loginSession = request.getSession(false);

            if (loginSession != null && Boolean.TRUE.equals(loginSession.getAttribute("isUserLoggedIn"))) {
                response.sendRedirect("home.jsp");
            } else {
                String forgotPasswordToken = request.getParameter("FPT");
                String errorMessage = "";

                if (forgotPasswordToken == null || forgotPasswordToken.isEmpty()) {
                    response.sendRedirect("index.jsp");
                } else if (forgotPasswordToken.length() != 40 || !forgotPasswordToken.matches("^[a-zA-Z0-9]+$")) {
                    errorMessage = "O token fornecido é inválido. Certifique-se de que o link está correto e não contém caracteres ausentes ou inválidos.";
                }
                %>
                <main class="p-4 mt-5 mb-5 ms-4 me-4 shadow">
                    <div id="invalidTokenContainer">
                        <p id="invalidTokenMessage"><%= errorMessage %></p>
                    </div>

                    <div class="d-none" id="changePasswordContainer">
                        <h1 class="text-center fs-2">Alterar Senha</h1>
                        <hr class="mt-4">

                        <section id="passwordInputSection">
                            <label for="passwordInput" id="passwordLabel">Nova senha</label>
                            <section class="mt-2 mb-2">
                                <input type="password" id="passwordInput" class="pt-2 pb-2 ps-3 pe-3" maxlength="30" placeholder="********">
                                <i class="bi bi-eye fs-4 ms-1 p-1 ps-2 pe-2" id="viewOrHidePasswordInputIcon"></i>
                            </section>
                            <p id="passwordMessage" class="form-text text-danger"></p>
                        </section>

                        <input type="submit" class="btn btn-success pt-2 pb-2 mt-3 mb-2" id="changePasswordButton" value="Confirmar">
                    </div>
                </main> 
                <%
            }
        %>
            
        <%@include file="WEB-INF/JSPF/html-body-libs.jspf"%>
        <script src="js/verify-forgot-password-token.js"></script>
    </body>
</html>