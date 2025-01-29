const inputEmail = document.querySelector('#inputEmail');
inputEmail.focus();

const buttonSendForgotPasswordEmail = document.querySelector('#buttonSendForgotPasswordEmail');
const buttonResendForgotPasswordEmail = document.querySelector('#buttonResendForgotPasswordEmail');
buttonSendForgotPasswordEmail.addEventListener('click', function(){
    let email = inputEmail.value.trim();
    validateEmail(email);
});
buttonResendForgotPasswordEmail.addEventListener('click', function(){
    let email = inputEmail.value.trim();
    sendUserForgotPasswordEmail(true, email);
});

const sectionInputEmail = document.querySelector('#sectionInputEmail');

const warningEmail = document.querySelector('#warningEmail');
const warningConfirmationRequired = document.querySelector('#warningConfirmationRequired');

/**
    * Verifica se o e-mail informado é ou não válido.
    *
    * @function
    * @param {String} email - E-mail informado pelo usuário.
    * @returns {Boolean} O resultado da validação indicando se o e-mail informado é ou não válido.
    * @description 
    * - Caso o e-mail esteja vazio, exibe: "Por favor, insira um e-mail."
    * - Caso o e-mail seja inválido, exibe: "Por favor, insira um e-mail válido."
    * - Caso o e-mail seja válido, limpa qualquer mensagem anterior e verifica se o e-mail esta em uso.
    * @author isaquesv
    * @since 1.0
*/
async function validateEmail(email) {
    if (email.length === 0) {
        warningEmail.innerHTML = "Por favor, insira um e-mail.";
    } else if (!inputEmail.checkValidity()) {
        warningEmail.innerHTML = "Por favor, insira um e-mail válido.";
    } else {
        warningEmail.innerHTML = "";
        let isEmailAvailable = await checkEmailExistence(email);

        if (!isEmailAvailable) {
            sendUserForgotPasswordEmail(false, email);
        }
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
    * Envia um e-mail de alteração de senha.
    *
    * @function
    * @param {Boolean} isEmailResent - Se a chamada da função se refere a um reenvio de e-mail de alteração de senha ou não.
    * @param {String} email - E-mail fornecido pelo usuário.
    * @description 
    * - Caso a chamada da função seja um reenvio de e-mail, exibe: "E-mail reenviado..."
    * - Caso a chamada da função não seja um reenvio de e-mail, exibe a mensagem retornada.
    * - Caso ocorra um erro inesperado ao tentar enviar o e-mail de alteração de senha, exibe: "Houve um erro ao tentar enviar o e-mail de recuperação de senha. Tente novamente."
    * @author isaquesv
    * @since 1.0
*/
function sendUserForgotPasswordEmail(isEmailResent, email) {
    $.ajax({
        method: 'POST',
        url: 'SendUserForgotPasswordEmailServlet',
        data: {
            email: email
        },
        dataType: 'json',
        success: function (emailSendResultJSON) {
            if (emailSendResultJSON.isEmailSentSuccessfully) {
                if (!sectionInputEmail.classList.contains('d-none')) {
                    sectionInputEmail.classList.add('d-none');
                }
                if (!buttonSendForgotPasswordEmail.classList.contains('d-none')) {
                    buttonSendForgotPasswordEmail.classList.add('d-none');
                }
                if (buttonResendForgotPasswordEmail.classList.contains('d-none')) {
                    buttonResendForgotPasswordEmail.classList.remove('d-none');
                }
            }
                
            if (isEmailResent) {
                warningConfirmationRequired.innerHTML = "<b>E-mail reenviado</b>. " + emailSendResultJSON.message;
            } else {
                warningConfirmationRequired.innerHTML = emailSendResultJSON.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            warningEmail.innerHTML = "Houve um erro ao tentar enviar o e-mail de alteração de senha. Tente novamente.";
        }
    });
}