const urlParameters = window.location.search;
const parameters = new URLSearchParams(urlParameters);
const forgotPasswordToken = parameters.get('FPT');

const invalidTokenContainer = document.querySelector('#invalidTokenContainer');
const invalidTokenMessage = document.querySelector('#invalidTokenMessage');

const changePasswordContainer = document.querySelector('#changePasswordContainer');
const passwordInput = document.querySelector('#passwordInput');

passwordInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de nova senha estiver em foco
    if (event.key === "Enter") {
        event.preventDefault();
        
        let password = passwordInput.value.trim();
        validatePasswordFormat(password);
    }
});

const viewOrHidePasswordInputIcon = document.querySelector('#viewOrHidePasswordInputIcon');
viewOrHidePasswordInputIcon.addEventListener('click', function(){
    viewOrHidePassword(passwordInput, viewOrHidePasswordInputIcon);
});

const changePasswordButton = document.querySelector('#changePasswordButton');
changePasswordButton.addEventListener('click', function(){
    let password = passwordInput.value.trim();
    validatePasswordFormat(password);
});

const passwordMessage = document.querySelector('#passwordMessage');

if (forgotPasswordToken.length == 40 && /^[a-zA-Z0-9]+$/.test(forgotPasswordToken)) {
    checkIfForgotPasswordTokenExistsInDatabase(forgotPasswordToken);
}


/*
    * Verifica se o token de esqueci minha senha informado existe ou não no banco de dados.
    *
    * @function
    * @param {String} forgotPasswordToken - Token de esqueci minha senha do usuário.
    * @author isaquesv
    * @since 1.0
*/
function checkIfForgotPasswordTokenExistsInDatabase(forgotPasswordToken) {
    $.ajax({
        method: 'POST',
        url: 'CheckIfForgotPasswordTokenExistsInDatabaseServlet',
        data: {
            forgotPasswordToken: forgotPasswordToken
        },
        dataType: 'json',
        success: function (forgotPasswordTokenExistenceResponse) {
            if (forgotPasswordTokenExistenceResponse.isForgotPasswordTokenExistsInDatabase == true) {
                showOrHideChangePasswordForm(true);
                passwordInput.focus();
            } else {
                showOrHideChangePasswordForm(false);
                invalidTokenMessage.innerHTML = forgotPasswordTokenExistenceResponse.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            showOrHideChangePasswordForm(false);
            invalidTokenMessage.innerHTML = "Houve um erro ao verificar o token. Tente novamente.";
        }
    });
}

/*
    * Verifica se a nova senha informada é válida ou não.
    *
    * @function
    * @param {String} password - Nova senha do usuário.
    * @author isaquesv
    * @since 1.0
*/
function validatePasswordFormat(password) {
    if (password.length === 0) {
        passwordMessage.innerHTML = "Por favor, insira uma senha.";
    } else if (password.length < 8) {
        passwordMessage.innerHTML = "A senha deve ter pelo menos 8 caracteres.";
    } else {
        // Expressão regular para encontrar qualquer letra (maiúscula ou minúscula)
        const regexLetter = /[a-zA-Z]/;
        // Expressão regular para encontrar qualquer número
        const regexNumber = /[0-9]/;
        // Expressão regular para encontrar qualquer caractere especial
        const regexSpecialCharacter = /[^a-zA-Z0-9]/;
        
        if (!regexLetter.test(password)) {
            passwordMessage.innerHTML = "A senha deve ter pelo menos 1 letra.";
        }
        if (!regexNumber.test(password)) {
            passwordMessage.innerHTML = "A senha deve ter pelo menos 1 número.";
        }
        if (!regexSpecialCharacter.test(password)) {
            passwordMessage.innerHTML = "A senha deve ter pelo menos 1 caractere especial.";
        }
        
        if (regexLetter.test(password) == true && regexNumber.test(password) == true && regexSpecialCharacter.test(password) == true) {
            passwordMessage.innerHTML = ""; 
            changePassword(forgotPasswordToken, password);
        }
    }
}

/*
    * Altera a senha do usuário.
    *
    * @function
    * @param {String} forgotPasswordToken - Token de esqueci minha senha do usuário.
    * @param {String} password - Nova senha do usuário.
    * @author isaquesv
    * @since 1.0
*/
function changePassword(forgotPasswordToken, password) {
    $.ajax({
        method: 'POST',
        url: 'ChangePasswordServlet',
        data: {
            forgotPasswordToken: forgotPasswordToken,
            password: password
        },
        dataType: 'json',
        success: function (changePasswordResponse) {
            if (changePasswordResponse.isPasswordChanged == true) {
                window.location.href = "home.jsp";
            } else {
                passwordMessage.innerHTML = changePasswordResponse.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            passwordMessage.innerHTML = "Houve um erro ao tentar alterar sua senha. Tente novamente.";
        }
    });
}

/*
    * Exibe ou esconde o valor do campo senha.
    *
    * @function
    * @param {Element} passwordInput - Elemento INPUT da senha.
    * @param {Element} viewOrHidePasswordInputIcon - Elemento I, olho, da senha.
    * @author isaquesv
    * @since 1.0
*/
function viewOrHidePassword(passwordInput, viewOrHidePasswordInputIcon) {
    if (passwordInput.type == 'password') {
        passwordInput.type = 'text';
        
        if (!viewOrHidePasswordInputIcon.classList.contains('bi-eye-slash')) {
            viewOrHidePasswordInputIcon.classList.add('bi-eye-slash');
        }
        if (viewOrHidePasswordInputIcon.classList.contains('bi-eye')) {
            viewOrHidePasswordInputIcon.classList.remove('bi-eye');
        }
    } else {
        passwordInput.type = 'password';
        
        if (!viewOrHidePasswordInputIcon.classList.contains('bi-eye')) {
            viewOrHidePasswordInputIcon.classList.add('bi-eye');
        }
        if (viewOrHidePasswordInputIcon.classList.contains('bi-eye-slash')) {
            viewOrHidePasswordInputIcon.classList.remove('bi-eye-slash');
        }
    }
}

/*
    * Exibe ou esconde o formulário de alteração de senha do usuário.
    *
    * @function
    * @param {Boolean} isToShow - Para saber se é para exibir o formulário de alteração de senha do usuário ou não.
    * @author isaquesv
    * @since 1.0
*/
function showOrHideChangePasswordForm(isToShow) {
    if (isToShow == true) {
        if (invalidTokenContainer.classList.contains('d-none') == false) {
            invalidTokenContainer.classList.add('d-none');
        }
        if (changePasswordContainer.classList.contains('d-none') == true) {
            changePasswordContainer.classList.remove('d-none');
        }
    } else {    
        if (invalidTokenContainer.classList.contains('d-none') == true) {
            invalidTokenContainer.classList.remove('d-none');
        }
        if (changePasswordContainer.classList.contains('d-none') == false) {
            changePasswordContainer.classList.add('d-none');
        }
    }
}