const nameInput = document.querySelector('#nameInput');
nameInput.focus();

nameInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de nome de usuário estiver em foco
    if (event.key === "Enter") {
        event.preventDefault();
        validateRegisterForm();
    }
});

const emailInput = document.querySelector('#emailInput');
emailInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de e-mail estiver em foco
    if (event.key === "Enter") {
        event.preventDefault();
        validateRegisterForm();
    }
});

const passwordInput = document.querySelector('#passwordInput');
passwordInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de senha estiver em foco
    if (event.key === "Enter") {
        event.preventDefault();
        validateRegisterForm();
    }
});

const confirmPasswordInput = document.querySelector('#confirmPasswordInput');
confirmPasswordInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de confirmar senha estiver em foco
    if (event.key === "Enter") {
        event.preventDefault();
        validateRegisterForm();
    }
});

const viewOrHidePasswordInputIcon = document.querySelector('#viewOrHidePasswordInputIcon');
viewOrHidePasswordInputIcon.addEventListener('click', function(){
    viewOrHidePassword(passwordInput, viewOrHidePasswordInputIcon);
});

const viewOrHideConfirmPasswordInputIcon = document.querySelector('#viewOrHideConfirmPasswordInputIcon');
viewOrHideConfirmPasswordInputIcon.addEventListener('click', function(){
    viewOrHidePassword(confirmPasswordInput, viewOrHideConfirmPasswordInputIcon);
});

const sendRegisterEmailButton = document.querySelector('#sendRegisterEmailButton');
sendRegisterEmailButton.addEventListener('click', function(){
    validateRegisterForm();
});

const resendRegisterEmailButton = document.querySelector('#resendRegisterEmailButton');
resendRegisterEmailButton.addEventListener('click', function(){
    let name = nameInput.value.trim();
    let email = emailInput.value.trim();
    let password = passwordInput.value.trim();
   
    let user = {
        name: name,
        email: email,
        password: password
    }
    
    sendRegisterEmail(true, user);
});

const backToRegisterFormButton = document.querySelector('#backToRegisterFormButton');
backToRegisterFormButton.addEventListener('click', function(){
    showOrHideRegisterForm(true);
    enableOrDisableRegisterForm(true);
});

const registerContainer = document.querySelector('#registerContainer');
const registerEmailVerificationRequiredContainer = document.querySelector('#registerEmailVerificationRequiredContainer');

const nameMessage = document.querySelector('#nameMessage');
const emailMessage = document.querySelector('#emailMessage');
const passwordMessage = document.querySelector('#passwordMessage');
const confirmPasswordMessage = document.querySelector('#confirmPasswordMessage');
const registerEmailVerificationRequiredMessage = document.querySelector('#registerEmailVerificationRequiredMessage');


/*
    * Verifica se o nome do usuário informado é válido ou não.
    *
    * @function
    * @param {String} name - Nome do usuário.
    * @returns {Boolean} - Resultado da validação do nome do usuário.
    * @author isaquesv
    * @since 1.0
*/
function validateNameFormat(name) {
    if (name.length == 0) {
        nameMessage.innerHTML = "Por favor, insira um nome de usuário.";
        return false;
    } else if (name.length < 4) {
        nameMessage.innerHTML = "O nome de usuário deve ter pelo menos 4 caracteres.";
        return false;
    } else {
        nameMessage.innerHTML = "";
        return true;
    }
}

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
    } else if (!emailInput.checkValidity()) {
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
            emailMessage.innerHTML = "";
        } else {
            emailMessage.innerHTML = emailExistenceResponse.message;           
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
    if (password.length === 0) {
        passwordMessage.innerHTML = "Por favor, insira uma senha.";
        return false;
    } else if (password.length < 8) {
        passwordMessage.innerHTML = "A senha deve ter pelo menos 8 caracteres.";
        return false;
    } else {
        // Expressão regular para encontrar qualquer letra (maiúscula ou minúscula)
        const regexLetter = /[a-zA-Z]/;
        // Expressão regular para encontrar qualquer número
        const regexNumber = /[0-9]/;
        // Expressão regular para encontrar qualquer caractere especial
        const regexSpecialCharacter = /[^a-zA-Z0-9]/;
        
        if (!regexLetter.test(password)) {
            passwordMessage.innerHTML = "A senha deve ter pelo menos 1 letra.";
            return false;
        }
        if (!regexNumber.test(password)) {
            passwordMessage.innerHTML = "A senha deve ter pelo menos 1 número.";
            return false;
        }
        if (!regexSpecialCharacter.test(password)) {
            passwordMessage.innerHTML = "A senha deve ter pelo menos 1 caractere especial.";
            return false;
        }
        
        passwordMessage.innerHTML = "";
        return true;
    }
}

/*
    * Verifica se a confirmação da senha informada é válida ou não.
    *
    * @function
    * @param {String} confirmPassword - Confirmação da senha do usuário.
    * @returns {Boolean} - Resultado da validação da confirmação da senha.
    * @author isaquesv
    * @since 1.0
*/
function validateConfirmPasswordFormat(confirmPassword, password) {
    let isPasswordValid = validatePasswordFormat(password);
    
    if (isPasswordValid) {
        if (confirmPassword.length == 0 || confirmPassword != password) {
            confirmPasswordMessage.innerHTML = "Por favor, digite a mesma senha criada acima.";
            return false;
        } else {
            confirmPasswordMessage.innerHTML = "";
            return true;
        }
    } else {
        confirmPasswordMessage.innerHTML = "Por favor, insira uma senha válida no campo acima.";
        return false;
    }
}

/*
    * Valida o formulário de cadastro, seus campos.
    *
    * @function
    * @author isaquesv
    * @since 1.0
*/
async function validateRegisterForm() {
    let name = nameInput.value.trim();
    let email = emailInput.value.trim();
    let password = passwordInput.value.trim();
    let confirmPassword = confirmPasswordInput.value.trim();
    
    let isNameValid = validateNameFormat(name);
    let isEmailValid = validateEmailFormat(email);
    let isEmailAvailable = false;
    let isPasswordValid  = validatePasswordFormat(password);
    let isConfirmPasswordValid = validateConfirmPasswordFormat(confirmPassword, password);
    
    if (isEmailValid == true) {
        isEmailAvailable = await checkIfEmailExistsInDatabase(email);
    }
    
    if (isNameValid == true && isEmailAvailable == true && isPasswordValid == true && isConfirmPasswordValid == true) {
        let user = {
            name: name,
            email: email,
            password: password
        }
        
        sendRegisterEmail(false, user);
    }
}

/*
    * Envia o e-mail de confirmação de cadastro do usuário.
    *
    * @function
    * @param {Boolean} isRegisterEmailResent - Para saber se é um reenvio de e-mail ou não.
    * @param {Object} user - Objeto com as informações do usuário.
    * @author isaquesv
    * @since 1.0
*/
function sendRegisterEmail(isRegisterEmailResent, user) {
    enableOrDisableRegisterForm(false);
 
    $.ajax({
        method: 'POST',
        url: 'SendRegisterEmailServlet',
        data: {
            name: user.name,
            email: user.email,
            password: user.password
        },
        dataType: 'json',
        success: function (registerEmailResponse) {
            if (registerEmailResponse.isRegisterEmailSent == true) {
                showOrHideRegisterForm(false);
                
                if (isRegisterEmailResent == true) {
                    registerEmailVerificationRequiredMessage.innerHTML = "<b>E-mail reenviado</b>. " + registerEmailResponse.message;
                } else {
                    registerEmailVerificationRequiredMessage.innerHTML = registerEmailResponse.message;
                }
                
                confirmPasswordMessage.innerHTML = "";
            } else {
                enableOrDisableRegisterForm(true);
                showOrHideRegisterForm(true);
                registerEmailVerificationRequiredMessage.innerHTML = "";
                confirmPasswordMessage.innerHTML = registerEmailResponse.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            confirmPasswordMessage.innerHTML = "Houve um erro ao tentar enviar o e-mail de confirmação de cadastro. Tente novamente.";
        }
    });
}

/*
    * Habilita ou desabilita o formulário de cadastro do usuário.
    *
    * @function
    * @param {Boolean} isToEnable - Para saber se é para habilitar o formulário de cadastro de usuário ou não.
    * @author isaquesv
    * @since 1.0
*/
function enableOrDisableRegisterForm(isToEnable) {
    if (isToEnable == true) {
        if (nameInput.hasAttribute('disabled') == true) {
            nameInput.removeAttribute('disabled');
        }
        if (emailInput.hasAttribute('disabled') == true) {
            emailInput.removeAttribute('disabled');
        }
        if (passwordInput.hasAttribute('disabled') == true) {
            passwordInput.removeAttribute('disabled');
        }
        if (confirmPasswordInput.hasAttribute('disabled') == true) {
            confirmPasswordInput.removeAttribute('disabled');
        }
        if (sendRegisterEmailButton.hasAttribute('disabled') == true) {
            sendRegisterEmailButton.removeAttribute('disabled');
        }
    } else {
        if (nameInput.hasAttribute('disabled') == false) {
            nameInput.setAttribute('disabled', '');
        }
        if (emailInput.hasAttribute('disabled') == false) {
            emailInput.setAttribute('disabled', '');
        }
        if (passwordInput.hasAttribute('disabled') == false) {
            passwordInput.setAttribute('disabled', '');
        }
        if (confirmPasswordInput.hasAttribute('disabled') == false) {
            confirmPasswordInput.setAttribute('disabled', '');
        }
        if (sendRegisterEmailButton.hasAttribute('disabled') == false) {
            sendRegisterEmailButton.setAttribute('disabled', '');
        }
    }
}

/*
    * Exibe ou esconde o formulário de cadastro de usuário.
    *
    * @function
    * @param {Boolean} isToShow - Para saber se é para exibir o formulário de cadastro de usuário ou não.
    * @author isaquesv
    * @since 1.0
*/
function showOrHideRegisterForm(isToShow) {
    if (isToShow == true) {
        if (registerContainer.classList.contains('d-none') == true) {
            registerContainer.classList.remove('d-none');
        }
        if (registerEmailVerificationRequiredContainer.classList.contains('d-none') == false) {
            registerEmailVerificationRequiredContainer.classList.add('d-none');
        }
    } else {
        if (registerContainer.classList.contains('d-none') == false) {
            registerContainer.classList.add('d-none');
        }
        if (registerEmailVerificationRequiredContainer.classList.contains('d-none') == true) {
            registerEmailVerificationRequiredContainer.classList.remove('d-none');
        }
    }
}

/*
    * Exibe ou esconde o campo senha, seja ele o primeiro ou o segundo (confirmação de senha).
    *
    * @function
    * @param {Element} anyPasswordInput - Elemento INPUT da senha.
    * @param {Element} anyViewOrHidePasswordInputIcon - Elemento I, olho, da senha.
    * @author isaquesv
    * @since 1.0
*/
function viewOrHidePassword(anyPasswordInput, anyViewOrHidePasswordInputIcon) {
    if (anyPasswordInput.type == 'password') {
        anyPasswordInput.type = 'text';
        
        if (anyViewOrHidePasswordInputIcon.classList.contains('bi-eye-slash') == false) {
            anyViewOrHidePasswordInputIcon.classList.add('bi-eye-slash');
        }
        if (anyViewOrHidePasswordInputIcon.classList.contains('bi-eye') == true) {
            anyViewOrHidePasswordInputIcon.classList.remove('bi-eye');
        }
    } else {
        anyPasswordInput.type = 'password';
        
        if (anyViewOrHidePasswordInputIcon.classList.contains('bi-eye') == false) {
            anyViewOrHidePasswordInputIcon.classList.add('bi-eye');
        }
        if (anyViewOrHidePasswordInputIcon.classList.contains('bi-eye-slash') == true) {
            anyViewOrHidePasswordInputIcon.classList.remove('bi-eye-slash');
        }
    }
}