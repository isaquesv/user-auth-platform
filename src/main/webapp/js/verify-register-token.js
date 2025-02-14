const urlParameters = window.location.search;
const parameters = new URLSearchParams(urlParameters);
const registerToken = parameters.get('RT');

const invalidTokenMessage = document.querySelector('#invalidTokenMessage');

if (registerToken.length == 40 && /^[a-zA-Z0-9]+$/.test(registerToken)) {
    checkIfRegisterTokenExistsInDatabase(registerToken);
}


/*
    * Verifica se o token de cadastro de usuário informado existe ou não no banco de dados.
    *
    * @function
    * @param {String} registerToken - Token de cadastro do usuário.
    * @author isaquesv
    * @since 1.0
*/
function checkIfRegisterTokenExistsInDatabase(registerToken) {
    $.ajax({
        method: 'POST',
        url: 'CheckIfRegisterTokenExistsInDatabaseServlet',
        data: {
            registerToken: registerToken
        },
        dataType: 'json',
        success: function (registerTokenExistenceJsonResponse) {
            if (registerTokenExistenceJsonResponse.isRegisterTokenInDatabase == true) {
                window.location.href = "home.jsp";
            } else {
                invalidTokenMessage.innerHTML = registerTokenExistenceJsonResponse.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            invalidTokenMessage.innerHTML = "Houve um erro ao verificar o token. Tente novamente.";
        }
    });
}