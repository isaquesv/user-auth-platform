const inputEmail = document.querySelector('#inputEmail');
const inputPassword = document.querySelector('#inputPassword');
inputEmail.focus();

const iViewOrHidePassword = document.querySelector('#viewOrHidePassword');
iViewOrHidePassword.addEventListener('click', function(){
    viewOrHidePassword(inputPassword, iViewOrHidePassword);
});

const buttonLoginUser = document.querySelector('#buttonLoginUser');
buttonLoginUser.addEventListener('click', function(){
    validateForm();
});

const warningEmail = document.querySelector('#warningEmail');
const warningPassword = document.querySelector('#warningPassword');

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
            warningEmail.innerHTML = emailSelectResultJSON.message;
            return true;
        } else {
            warningEmail.innerHTML = "";
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
        warningPassword.innerHTML = "";
        return true;
    }
}

/**
    * Verifica se os campos do formulário são ou não válidos.
    *
    * @function
    * @description 
    * - Caso o e-mail seja válido, verifica se o e-mail está disponível
    * - Caso o e-mail já esteja cadastrado e o campo senha esteja válido, realiza login
    * @author isaquesv
    * @since 1.0
*/
async function validateForm() {
    let email = inputEmail.value.trim();
    let password = inputPassword.value.trim();
    
    let isEmailValid = validateEmail(email);
    let isEmailAvailable = false;
    let isPasswordValid  = validatePassword(password);
    
    if (isEmailValid) {
        isEmailAvailable = await checkEmailExistence(email);
    }
    
    if (isEmailValid && !isEmailAvailable && isPasswordValid) {
        verifyLogin(email, password);
    }
}

/**
    * Verifica login do usuário.
    *
    * @function
    * @param {String} email - E-mail informado pelo usuário.
    * @param {String} password - Senha informada pelo usuário.
    * @description 
    * - Caso o login seja um sucesso, envia o usuário para a página home.
    * - Caso o login não seja um sucesso, exibe uma mensagem.
    * - Caso ocorra um erro inesperado ao tentar enviar o e-mail de confirmação de cadastro, exibe: "Houve um erro ao tentar cadastra-lo em nosso sistema. Tente novamente."
    * @author isaquesv
    * @since 1.0
*/
function verifyLogin(email, password) {
    $.ajax({
        method: 'POST',
        url: 'VerifyUserLoginServlet',
        data: {
            email: email,
            password: password
        },
        dataType: 'json',
        success: function (userSelectResultJSON) {
            if (userSelectResultJSON.isLoginVerifiedSuccessfully) {
                disableForm();
                
                window.location.href = "home.jsp";
            } else {
                warningEmail.innerHTML = userSelectResultJSON.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            warningEmail.innerHTML = "Houve um erro ao tentar realizar seu login em nosso sistema. Tente novamente.";
        }
    });
}

/**
    * Desabilita os campos e elementos presentes no formulário de login.
    *
    * @function
    * @author isaquesv
    * @since 1.0
*/
function disableForm() {
    if (!inputEmail.hasAttribute('disabled')) {
        inputEmail.setAttribute('disabled', '');
    }
    if (!inputPassword.hasAttribute('disabled')) {
        inputPassword.setAttribute('disabled', '');
    }
    if (!buttonLoginUser.hasAttribute('disabled')) {
        buttonLoginUser.setAttribute('disabled', '');
    }
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