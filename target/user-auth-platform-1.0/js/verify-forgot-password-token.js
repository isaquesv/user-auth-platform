const urlParameters = window.location.search;
const parameters = new URLSearchParams(urlParameters);
const token = parameters.get('token');
let email;

const divErrorMessage = document.querySelector('#divErrorMessage');
const errorMessage = document.querySelector('#errorMessage');

const divChangePassword = document.querySelector('#divChangePassword');
const inputPassword = document.querySelector('#inputPassword');

const iViewOrHidePassword = document.querySelector('#viewOrHidePassword');
iViewOrHidePassword.addEventListener('click', function(){
    viewOrHidePassword(inputPassword, iViewOrHidePassword);
});

const buttonChangePassword = document.querySelector('#buttonChangePassword');
buttonChangePassword.addEventListener('click', function(){
    let password = inputPassword.value.trim();
    validatePassword(password);
});

const warningPassword = document.querySelector('#warningPassword');


if (token.length == 40 && /^[a-zA-Z0-9]+$/.test(token)) {
    verifyUserForgotPasswordToken(token);
}

/**
    * Verifica o token de alteração de senha do usuário.
    *
    * @function
    * @param {String} tokne - Token de recuperação de senha enviado para o usuário.
    * @description 
    * - Se o token for válido, exibe o formulário de alteração de senha.
    * - Se o token for inválido, exibe uma mensagem de erro.
    * - Caso ocorra um erro durante o processo de validação, exibe uma mensagem informando sobre o erro.
    * @author isaquesv
    * @since 1.0
*/
function verifyUserForgotPasswordToken(token) {
    $.ajax({
        method: 'POST',
        url: 'VerifyUserForgotPasswordTokenServlet',
        data: {
            token: token
        },
        dataType: 'json',
        success: function (tokenForgotPasswordResponseJSON) {
            if (tokenForgotPasswordResponseJSON.isTokenValid) {
                divErrorMessage.classList.add('d-none');
                divChangePassword.classList.remove('d-none');
                inputPassword.focus();
            } else {
                errorMessage.innerHTML = tokenForgotPasswordResponseJSON.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            document.body.innerHTML = "Houve um erro ao verificar o token. Tente novamente.";
        }
    });
}

/**
    * Verifica se a senha informada é ou não válida.
    *
    * @function
    * @param {String} password - Senha informada pelo usuário.
    * @returns {Boolean} O resultado da validação indicando se a senha informada é ou não válida.
    * @description 
    * - Caso a senha esteja vazia, exibe: "Por favor, insira uma senha."
    * - Caso a senha tenha menos de 8 caracteres, exibe: "A senha deve ter pelo menos 8 caracteres."
    * - Caso a senha não tenha ao menos 1 letra, exibe: "A senha deve ter pelo menos 1 letra."
    * - Caso a senha não tenha ao menos 1 número, exibe: "A senha deve ter pelo menos 1 número."
    * - Caso a senha não tenha ao menos 1 número, exibe: "A senha deve ter pelo menos 1 caractere especial."
    * - Caso a senha seja válida, limpa qualquer mensagem anterior.
    * @author isaquesv
    * @since 1.0
*/
function validatePassword(password) {
    if (password.length === 0) {
        warningPassword.innerHTML = "Por favor, insira uma senha.";
    } else if (password.length < 8) {
        warningPassword.innerHTML = "A senha deve ter pelo menos 8 caracteres.";
    } else {
        // Expressão regular para encontrar qualquer letra (maiúscula ou minúscula)
        const regexLetter = /[a-zA-Z]/;
        // Expressão regular para encontrar qualquer número
        const regexNumber = /[0-9]/;
        // Expressão regular para encontrar qualquer caractere especial
        const regexSpecialCharacter = /[^a-zA-Z0-9]/;
        
        if (!regexLetter.test(password)) {
            warningPassword.innerHTML = "A senha deve ter pelo menos 1 letra.";
        }
        if (!regexNumber.test(password)) {
            warningPassword.innerHTML = "A senha deve ter pelo menos 1 número.";
        }
        if (!regexSpecialCharacter.test(password)) {
            warningPassword.innerHTML = "A senha deve ter pelo menos 1 caractere especial.";
        }
        
        if (regexLetter.test(password) && regexNumber.test(password) && regexSpecialCharacter.test(password)) {
            warningPassword.innerHTML = ""; 
            changeUserPassword(token, password);
        }
    }
}

/**
    * Altera a senha do usuário com base no token fornecido.
    *
    * @function
    * @param {String} token - Token de recuperação de senha enviado para o usuário.
    * @param {String} password - Nova senha fornecida pelo usuário.
    * @description 
    * - Se a alteração da senha for bem-sucedida, redireciona o usuário para a página inicial.
    * - Se a alteração falhar, exibe uma mensagem de erro com a descrição do problema.
    * - Em caso de erro inesperado durante o processo, exibe uma mensagem informando sobre a falha na alteração da senha.
    * @author isaquesv
    * @since 1.0
*/
function changeUserPassword(token, password) {
    $.ajax({
        method: 'POST',
        url: 'ChangeUserPassword',
        data: {
            token: token,
            password: password
        },
        dataType: 'json',
        success: function (passwordChangeResultJSON) {
            if (passwordChangeResultJSON.isPasswordChanged) {
                window.location.href = "home.jsp";
            } else {
                warningPassword.innerHTML = passwordChangeResultJSON.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            warningPassword.innerHTML = "Houve um erro ao tentar alterar sua senha. Tente novamente.";
        }
    });
}

/**
    * Exibe ou não o valor do campo senha.
    *
    * @function
    * @param {Element} elementInputPassword - Campo senha que deve ter o seu atributo "type" alterado.
    * @param {Element} elementIconPassword - Ícone senha que deve ter a sua classe alterada.
    * @author isaquesv
    * @since 1.0
*/
function viewOrHidePassword(elementInputPassword, elementIconPassword) {
    if (elementInputPassword.type == 'password') {
        elementInputPassword.type = 'text';
        
        if (!elementIconPassword.classList.contains('bi-eye-slash')) {
            elementIconPassword.classList.add('bi-eye-slash');
        }
        if (elementIconPassword.classList.contains('bi-eye')) {
            elementIconPassword.classList.remove('bi-eye');
        }
    } else {
        elementInputPassword.type = 'password';
        
        if (!elementIconPassword.classList.contains('bi-eye')) {
            elementIconPassword.classList.add('bi-eye');
        }
        if (elementIconPassword.classList.contains('bi-eye-slash')) {
            elementIconPassword.classList.remove('bi-eye-slash');
        }
    }
}