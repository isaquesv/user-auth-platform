const emailInput = document.querySelector('#emailInput');
emailInput.focus();

emailInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de e-mail estiver em foco
    if (event.key === "Enter") {
        event.preventDefault();
        validateLoginForm();
    }
});

const passwordInput = document.querySelector('#passwordInput');
passwordInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de senha estiver em foco
    if (event.key === "Enter") {
        event.preventDefault();
        validateLoginForm();
    }
});

const viewOrHidePasswordInputIcon = document.querySelector('#viewOrHidePasswordInputIcon');
viewOrHidePasswordInputIcon.addEventListener('click', function(){
    viewOrHidePassword(passwordInput, viewOrHidePasswordInputIcon);
});

const loginButton = document.querySelector('#loginButton');
loginButton.addEventListener('click', function(){
    validateLoginForm();
});

const emailMessage = document.querySelector('#emailMessage');
const passwordMessage = document.querySelector('#passwordMessage');


/*
    * Verifica se o e-mail informado é válido ou não.
    *
    * @function
    * @param {String} email - E-mail do usuário.
    * @returns {Boolean} - Resultado da validação do e-mail.
    * @author isaquesv
    * @since 1.0
*/
function validateEmailFormat(email) {
    if (email.length == 0) {
        emailMessage.innerHTML = "Por favor, insira um e-mail.";
        return false;
    } else if (emailInput.checkValidity() == false) {
        emailMessage.innerHTML = "Por favor, insira um e-mail válido.";
        return false;
    } else {
        emailMessage.innerHTML = "";
        return true;
    }
}

/*
    * Verifica se o e-mail informado existe ou não no banco de dados.
    *
    * @function
    * @param {String} email - E-mail do usuário.
    * @returns {Boolean} - Resultado da existência do e-mail.
    * @author isaquesv
    * @since 1.0
*/
async function checkIfEmailExistsInDatabase(email) {
    try {
        const emailExistenceResponse = await new Promise((resolve, reject) => {
            $.ajax({
                method: 'POST',
                url: 'CheckIfEmailExistsInDatabaseServlet',
                data: {
                    email: email
                },
                dataType: 'json',
                success: function (response) {
                    resolve(response);
                },
                error: function (xhr, status, error) {
                    reject(new Error(error));
                }
            });
        });
        
        if (emailExistenceResponse.isEmailExistsInDatabase == true) {
            emailMessage.innerHTML = emailExistenceResponse.message;
        } else {
            emailMessage.innerHTML = "";
        }
        
        return emailExistenceResponse.isEmailExistsInDatabase;
    } catch (error) {
        console.log("Erro: " + error.message);
        emailMessage.innerHTML = "Houve um erro ao verificar se este e-mail já está, ou não, cadastrado em nosso sistema. Tente novamente.";
        return false;
    }
}

/*
    * Verifica se a senha informada é válida ou não.
    *
    * @function
    * @param {String} password - Senha do usuário.
    * @returns {Boolean} - Resultado da validação da senha.
    * @author isaquesv
    * @since 1.0
*/
function validatePasswordFormat(password) {
    if (password.length == 0) {
        passwordMessage.innerHTML = "Por favor, insira uma senha.";
        return false;
    } else if (password.length < 8) {
        passwordMessage.innerHTML = "A senha deve ter pelo menos 8 caracteres.";
        return false;
    } else {
        passwordMessage.innerHTML = "";
        return true;
    }
}

/*
    * Valida o formulário de login, seus campos.
    *
    * @function
    * @author isaquesv
    * @since 1.0
*/
async function validateLoginForm() {
    let email = emailInput.value.trim();
    let password = passwordInput.value.trim();
    
    let isEmailValid = validateEmailFormat(email);
    let isEmailAvailable = false;
    let isPasswordValid  = validatePasswordFormat(password);
    
    if (isEmailValid) {
        isEmailAvailable = await checkIfEmailExistsInDatabase(email);
    }
    
    if (isEmailValid && !isEmailAvailable && isPasswordValid) {
        verifyLogin(email, password);
    }
}

/*
    * Verifica se o login é válido ou não.
    *
    * @function
    * @param {String} email - E-mail do usuário.
    * @param {String} password - Senha do usuário.
    * @author isaquesv
    * @since 1.0
*/
function verifyLogin(email, password) {
    $.ajax({
        method: 'POST',
        url: 'VerifyLoginServlet',
        data: {
            email: email,
            password: password
        },
        dataType: 'json',
        success: function (loginVerificationResponse) {
            if (loginVerificationResponse.isLoginCorrect == true) {
                disableLoginInputs();
                
                window.location.href = "home.jsp";
            } else {
                emailMessage.innerHTML = loginVerificationResponse.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            emailMessage.innerHTML = "Houve um erro ao tentar realizar seu login em nosso sistema. Tente novamente.";
        }
    });
}

/*
    * Desabilita o formulário de login.
    *
    * @function
    * @author isaquesv
    * @since 1.0
*/
function disableLoginInputs() {
    if (emailInput.hasAttribute('disabled') == false) {
        emailInput.setAttribute('disabled', '');
    }
    if (passwordInput.hasAttribute('disabled') == false) {
        passwordInput.setAttribute('disabled', '');
    }
    if (loginButton.hasAttribute('disabled') == false) {
        loginButton.setAttribute('disabled', '');
    }
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
        
        if (viewOrHidePasswordInputIcon.classList.contains('bi-eye-slash') == false) {
            viewOrHidePasswordInputIcon.classList.add('bi-eye-slash');
        }
        if (viewOrHidePasswordInputIcon.classList.contains('bi-eye') == true) {
            viewOrHidePasswordInputIcon.classList.remove('bi-eye');
        }
    } else {
        passwordInput.type = 'password';
        
        if (viewOrHidePasswordInputIcon.classList.contains('bi-eye') == false) {
            viewOrHidePasswordInputIcon.classList.add('bi-eye');
        }
        if (viewOrHidePasswordInputIcon.classList.contains('bi-eye-slash') == true) {
            viewOrHidePasswordInputIcon.classList.remove('bi-eye-slash');
        }
    }
}