const inputName = document.querySelector('#inputName');
const inputEmail = document.querySelector('#inputEmail');
const inputPassword = document.querySelector('#inputPassword');
const inputConfirmPassword = document.querySelector('#inputConfirmPassword');
inputName.focus();

const iViewOrHidePassword = document.querySelector('#viewOrHidePassword');
const iViewOrHideConfirmPassword = document.querySelector('#viewOrHideConfirmPassword');
iViewOrHidePassword.addEventListener('click', function(){
    viewOrHidePassword(inputPassword, iViewOrHidePassword);
});
iViewOrHideConfirmPassword.addEventListener('click', function(){
    viewOrHidePassword(inputConfirmPassword, iViewOrHideConfirmPassword);
});

const buttonRegisterUser = document.querySelector('#buttonRegisterUser');
const buttonResendConfirmationEmail = document.querySelector('#buttonResendConfirmationEmail');
const buttonShowRegistrationForm = document.querySelector('#buttonShowRegistrationForm');

buttonRegisterUser.addEventListener('click', function(){
    validateForm();
});

buttonResendConfirmationEmail.addEventListener('click', function(){
    let name = inputName.value.trim();
    let email = inputEmail.value.trim();
    let password = inputPassword.value.trim();
   
    let user = {
        name: name,
        email: email,
        password: password
    }
    
    sendEmailConfirmation(true, user);
});

buttonShowRegistrationForm.addEventListener('click', function(){
    viewOrHideForm("show");
    enableOrDisableForm("enable");
});

const divRegistrationForm = document.querySelector('#divRegistrationForm');
const divVerificationCode = document.querySelector('#divVerificationCode');

const warningName = document.querySelector('#warningName');
const warningEmail = document.querySelector('#warningEmail');
const warningPassword = document.querySelector('#warningPassword');
const warningConfirmPassword = document.querySelector('#warningConfirmPassword');
const warningConfirmationRequired = document.querySelector('#warningConfirmationRequired');

/**
    * Verifica se o nome informado é ou não válido.
    *
    * @function
    * @param {String} name - Nome informado pelo usuário.
    * @returns {Boolean} O resultado da validação indicando se o nome de usuário informado é ou não válido.
    * @description 
    * - Caso o nome esteja vazio, exibe: "Por favor, insira um nome de usuário."
    * - Caso o nome tenha menos de 4 caracteres, exibe: "O nome de usuário deve ter pelo menos 4 caracteres."
    * - Caso o nome seja válido, limpa qualquer mensagem anterior.
    * @author isaquesv
    * @since 1.0
*/
function validateName(name) {
    if (name.length === 0) {
        warningName.innerHTML = "Por favor, insira um nome de usuário.";
        return false;
    } else if (name.length < 4) {
        warningName.innerHTML = "O nome de usuário deve ter pelo menos 4 caracteres.";
        return false;
    } else {
        warningName.innerHTML = "";
        return true;
    }
}

/**
    * Verifica se o e-mail informado é ou não válido.
    *
    * @function
    * @param {String} email - E-mail informado pelo usuário.
    * @returns {Boolean} O resultado da validação indicando se o e-mail informado é ou não válido.
    * @description 
    * - Caso o e-mail esteja vazio, exibe: "Por favor, insira um e-mail."
    * - Caso o e-mail seja inválido, exibe: "Por favor, insira um e-mail válido."
    * - Caso o e-mail seja válido, limpa qualquer mensagem anterior.
    * @author isaquesv
    * @since 1.0
*/
function validateEmail(email) {
    if (email.length === 0) {
        warningEmail.innerHTML = "Por favor, insira um e-mail.";
        return false;
    } else if (!inputEmail.checkValidity()) {
        warningEmail.innerHTML = "Por favor, insira um e-mail válido.";
        return false;
    } else {
        warningEmail.innerHTML = "";
        return true;
    }
}

/**
    * Verifica se o e-mail informado esta ou não disponível.
    *
    * @function
    * @param {String} email - E-mail informado pelo usuário.
    * @returns {Boolean} O resultado da validação indicando se o e-mail informado esta disponível ou não.
    * @description 
    * - Caso ocorra um erro inesperado ao tentar validar a existência do e-mail, exibe: "Houve um erro ao verificar se este e-mail já está, ou não, cadastrado em nosso sistema. Tente novamente."
    * @author isaquesv
    * @since 1.0
*/
async function checkEmailExistence(email) {
    try {
        const emailSelectResultJSON = await new Promise((resolve, reject) => {
            $.ajax({
                method: 'POST',
                url: 'CheckUserEmailExistenceServlet',
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

        if (emailSelectResultJSON.isEmailAvailable) {
            warningEmail.innerHTML = "";
            return true;
        } else {
            warningEmail.innerHTML = emailSelectResultJSON.message;
            return false;            
        }
    } catch (error) {
        console.log("Erro: " + error.message);
        warningEmail.innerHTML = "Houve um erro ao verificar se este e-mail já está, ou não, cadastrado em nosso sistema. Tente novamente.";
        return false;
    }
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
        return false;
    } else if (password.length < 8) {
        warningPassword.innerHTML = "A senha deve ter pelo menos 8 caracteres.";
        return false;
    } else {
        // Expressão regular para encontrar qualquer letra (maiúscula ou minúscula)
        const regexLetter = /[a-zA-Z]/;
        // Expressão regular para encontrar qualquer número
        const regexNumber = /[0-9]/;
        // Expressão regular para encontrar qualquer caractere especial
        const regexSpecialCharacter = /[^a-zA-Z0-9]/;
        
        if (!regexLetter.test(password)) {
            warningPassword.innerHTML = "A senha deve ter pelo menos 1 letra.";
            return false;
        }
        if (!regexNumber.test(password)) {
            warningPassword.innerHTML = "A senha deve ter pelo menos 1 número.";
            return false;
        }
        if (!regexSpecialCharacter.test(password)) {
            warningPassword.innerHTML = "A senha deve ter pelo menos 1 caractere especial.";
            return false;
        }
        
        warningPassword.innerHTML = "";
        return true;
    }
}

/**
    * Verifica se a senha de confirmação informada é ou não válida.
    *
    * @function
    * @param {String} confirmPassword - Senha de confirmação informada pelo usuário.
    * @param {String} password - Senha do campo acima informada pelo usuário.
    * @returns {Boolean} O resultado da validação indicando se a senha de confirmação informada é ou não válida.
    * @description 
    * - Caso a senha de confirmação esteja vazia ou seja diferente da senha digitada acima, exibe: "Por favor, digite a mesma senha criada acima."
    * - Caso a senha digitada acima seja inválida, exibe: "Por favor, insira uma senha válida no campo acima."
    * - Caso a senha de confirmação seja válida, limpa qualquer mensagem anterior.
    * @author isaquesv
    * @since 1.0
*/
function validateConfirmPassword(confirmPassword, password) {
    let isPasswordValid = validatePassword(password);
    
    if (isPasswordValid) {
        if (confirmPassword.length === 0 || confirmPassword !== password) {
            warningConfirmPassword.innerHTML = "Por favor, digite a mesma senha criada acima.";
            return false;
        } else {
            warningConfirmPassword.innerHTML = "";
            return true;
        }
    } else {
        warningConfirmPassword.innerHTML = "Por favor, insira uma senha válida no campo acima.";
        return false;
    }
}

/**
    * Verifica se os campos do formulário são ou não válidos.
    *
    * @function
    * @description 
    * - Caso o e-mail seja válido, verifica se o e-mail está disponível
    * - Caso o e-mail esteja disponível e os demais campos estejam válidos, envia um e-mail de confirmação de cadastro
    * @author isaquesv
    * @since 1.0
*/
async function validateForm() {
    let name = inputName.value.trim();
    let email = inputEmail.value.trim();
    let password = inputPassword.value.trim();
    let confirmPassword = inputConfirmPassword.value.trim();
    
    let isNameValid = validateName(name);
    let isEmailValid = validateEmail(email);
    let isEmailAvailable = false;
    let isPasswordValid  = validatePassword(password);
    let isConfirmPasswordValid = validateConfirmPassword(confirmPassword, password);
    
    if (isEmailValid) {
        isEmailAvailable = await checkEmailExistence(email);
    }
    
    if (isNameValid && isEmailAvailable && isPasswordValid && isConfirmPasswordValid) {
        let user = {
            name: name,
            email: email,
            password: password
        }
        
        sendEmailConfirmation(false, user);
    }
}

/**
    * Envia um e-mail de confirmação de cadastro.
    *
    * @function
    * @param {Boolean} isEmailResent - Se a chamada da função se refere a um reenvio de e-mail de confirmação de cadastro ou não.
    * @param {Object} user - Contém os dados informados pelo usuário no formulário de cadastro.
    * @description 
    * - Caso o envio do e-mail de confirmação seja um sucesso, esconde o formulário de cadastro
    * - Caso a chamada da função seja um reenvio de e-mail, exibe: "E-mail reenviado..."
    * - Caso a chamada da função não seja um reenvio de e-mail, exibe a mensagem retornada.
    * - Caso ocorra um erro inesperado ao tentar enviar o e-mail de confirmação de cadastro, exibe: "Houve um erro ao tentar cadastra-lo em nosso sistema. Tente novamente."
    * @author isaquesv
    * @since 1.0
*/
function sendEmailConfirmation(isEmailResent, user) {
    enableOrDisableForm("disable");
 
    $.ajax({
        method: 'POST',
        url: 'SendUserRegistrationEmailServlet',
        data: {
            name: user.name,
            email: user.email,
            password: user.password
        },
        dataType: 'json',
        success: function (emailSendResultJSON) {
            if (emailSendResultJSON.isEmailSentSuccessfully) {
                viewOrHideForm("hide");
            }
                
            if (isEmailResent) {
                warningConfirmationRequired.innerHTML = "<b>E-mail reenviado</b>. " + emailSendResultJSON.message;
            } else {
                warningConfirmationRequired.innerHTML = emailSendResultJSON.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            warningConfirmPassword.innerHTML = "Houve um erro ao tentar enviar o e-mail de confirmação de cadastro. Tente novamente.";
        }
    });
}

/**
    * Habilita ou não os campos e elementos presentes no formulário de cadastro.
    *
    * @function
    * @param {String} enableOrDisable - Se é para desabilitar ou não o formulário de cadastro.
    * @description 
    * - Caso seja solicitado que o formulário de cadastro seja habilitado, remove todos os atributos "disabled"
    * - Caso seja solicitado que o formulário de cadastro seja desabilitado, adiciona os atributos "disabled"
    * @author isaquesv
    * @since 1.0
*/
function enableOrDisableForm(enableOrDisable) {
    if (enableOrDisable === "enable") {
        if (inputName.hasAttribute('disabled')) {
            inputName.removeAttribute('disabled');
        }
        if (inputEmail.hasAttribute('disabled')) {
            inputEmail.removeAttribute('disabled');
        }
        if (inputPassword.hasAttribute('disabled')) {
            inputPassword.removeAttribute('disabled');
        }
        if (inputConfirmPassword.hasAttribute('disabled')) {
            inputConfirmPassword.removeAttribute('disabled');
        }
        if (buttonRegisterUser.hasAttribute('disabled')) {
            buttonRegisterUser.removeAttribute('disabled');
        }
    } else {
        if (!inputName.hasAttribute('disabled')) {
            inputName.setAttribute('disabled', '');
        }
        if (!inputEmail.hasAttribute('disabled')) {
            inputEmail.setAttribute('disabled', '');
        }
        if (!inputPassword.hasAttribute('disabled')) {
            inputPassword.setAttribute('disabled', '');
        }
        if (!inputConfirmPassword.hasAttribute('disabled')) {
            inputConfirmPassword.setAttribute('disabled', '');
        }
        if (!buttonRegisterUser.hasAttribute('disabled')) {
            buttonRegisterUser.setAttribute('disabled', '');
        }
    }
}

/**
    * Exibe ou não o formulário de cadastro.
    *
    * @function
    * @param {String} action - Se é para esconder ou não o formulário de cadastro.
    * @description
    * - Caso seja solicitado que o formulário de cadastro seja exibido, exibe o formulário de cadastro e esconde a "div" de envio do e-mail de confirmação de cadastro.
    * - Caso seja solicitado que o formulário de cadastro seja escondido, exibe a "div" de envio do e-mail de confirmação de cadastro e esconde o formulário de cadastro.
    * @author isaquesv
    * @since 1.0
*/
function viewOrHideForm(action) {
    if (action === "hide") {
        if (!divRegistrationForm.classList.contains('d-none')) {
            divRegistrationForm.classList.add('d-none');
        }
        if (divVerificationCode.classList.contains('d-none')) {
            divVerificationCode.classList.remove('d-none');
        }
    } else {
        if (divRegistrationForm.classList.contains('d-none')) {
            divRegistrationForm.classList.remove('d-none');
        }
        if (!divVerificationCode.classList.contains('d-none')) {
            divVerificationCode.classList.add('d-none');
        }
    }
}

/**
    * Exibe ou não o valor de um campo senha.
    *
    * @function
    * @param {Element} elementInputPassword - Campo senha que deve ter o seu atributo "type" alterado.
    * @param {Element} elementIconPassword - Ícone senha que deve ter a sua classe alterada.
    * @description
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