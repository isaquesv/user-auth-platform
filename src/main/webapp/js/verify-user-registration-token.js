const urlParameters = window.location.search;
const parameters = new URLSearchParams(urlParameters);
const token = parameters.get('token');
const errorMessage = document.querySelector('#errorMessage');

if (token.length == 40 && /^[a-zA-Z0-9]+$/.test(token)) {
    verifyUserRegistrationToken(token);
}

/**
    * Valida o token de confirmação de registro do usuário.
    *
    * @function
    * @param {String} token - Token de confirmação de registro enviado para o usuário.
    * @description
    * - Se o token for válido, redireciona o usuário para a página inicial.
    * - Se o token for inválido, exibe uma mensagem de erro com a descrição fornecida pelo servidor.
    * - Em caso de erro inesperado ao verificar o token, exibe uma mensagem informando sobre a falha na verificação.
    * @author isaquesv
    * @since 1.0
*/
function verifyUserRegistrationToken(token) {
    $.ajax({
        method: 'POST',
        url: 'VerifyUserRegistrationTokenServlet',
        data: {
            token: token
        },
        dataType: 'json',
        success: function (tokenConfirmationResponseJSON) {
            if (tokenConfirmationResponseJSON.isTokenValid) {
                window.location.href = "home.jsp";
            } else {
                errorMessage.innerHTML = tokenConfirmationResponseJSON.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            document.body.innerHTML = "Houve um erro ao verificar o token. Tente novamente.";
        }
    });
}