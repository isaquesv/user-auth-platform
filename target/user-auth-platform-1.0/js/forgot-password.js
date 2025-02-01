const emailInput = document.querySelector('#emailInput');
emailInput.focus();

emailInput.addEventListener('keypress', function (event) {
    // Quando a tecla "Enter" for pressionada enquanto o input de e-mail estiver em foco
    if (event.key === "Enter") {
        let email = emailInput.value.trim();
        
        event.preventDefault();
        validateEmailFormat(email);
    }
});

const sendForgotPasswordEmailButton = document.querySelector('#sendForgotPasswordEmailButton');
sendForgotPasswordEmailButton.addEventListener('click', function(){
    let email = emailInput.value.trim();
    validateEmailFormat(email);
});

const resendForgotPasswordEmailButton = document.querySelector('#resendForgotPasswordEmailButton');
resendForgotPasswordEmailButton.addEventListener('click', function(){
    let email = emailInput.value.trim();
    sendForgotPasswordEmail(true, email);
});

const forgotPasswordContainer = document.querySelector('#forgotPasswordContainer');
const forgotPasswordEmailVerificationRequiredContainer = document.querySelector('#forgotPasswordEmailVerificationRequiredContainer');
const emailInputSection = document.querySelector('#emailInputSection');
const emailMessage = document.querySelector('#emailMessage');
const forgotPasswordResponseMessage = document.querySelector('#forgotPasswordResponseMessage');


/*
    * Verifica se o e-mail informado é válido ou não.
    *
    * @function
    * @param {String} email - E-mail do usuário.
    * @returns {Boolean} - Resultado da validação do e-mail.
    * @author isaquesv
    * @since 1.0
*/
async function validateEmailFormat(email) {
    if (email.length == 0) {
        emailMessage.innerHTML = "Por favor, insira um e-mail.";
    } else if (emailInput.checkValidity() == false) {
        emailMessage.innerHTML = "Por favor, insira um e-mail válido.";
    } else {
        emailMessage.innerHTML = "";
        let isEmailExistsInDatabase = await checkIfEmailExistsInDatabase(email);

        if (isEmailExistsInDatabase == false) {
            sendForgotPasswordEmail(false, email);
        }
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
    * Envia o e-mail de solicitação de alteração da senha do usuário.
    *
    * @function
    * @param {Boolean} isForgotPasswordEmailResent - Para saber se é um reenvio de e-mail ou não.
    * @param {String} email - E-mail do usuário.
    * @author isaquesv
    * @since 1.0
*/
function sendForgotPasswordEmail(isForgotPasswordEmailResent, email) {
    $.ajax({
        method: 'POST',
        url: 'SendForgotPasswordEmailServlet',
        data: {
            email: email
        },
        dataType: 'json',
        success: function (forgotPasswordEmailResponse) {
            if (forgotPasswordEmailResponse.isForgotPasswordEmailSent == true) {
                showOrHideForgotPasswordForm(true);
                emailMessage.innerHTML = "";
                
                if (isForgotPasswordEmailResent == true) {
                    forgotPasswordResponseMessage.innerHTML = "<b>E-mail reenviado</b>. " + forgotPasswordEmailResponse.message;
                } else {
                    forgotPasswordResponseMessage.innerHTML = forgotPasswordEmailResponse.message;
                }
            } else {
                showOrHideForgotPasswordForm(false);
                
                forgotPasswordResponseMessage.innerHTML = "";
                emailMessage.innerHTML = forgotPasswordEmailResponse.message;
            }
        },
        error: function (xhr, status, error) {
            console.log("Erro: " + error);
            emailMessage.innerHTML = "Houve um erro ao tentar enviar o e-mail de alteração de senha. Tente novamente.";
        }
    });
}

/*
    * Exibe ou esconde o formulário de esqueci minha senha.
    *
    * @function
    * @param {Boolean} isToShow - Para saber se é para exibir o formulário de esqueci minha senha ou não.
    * @author isaquesv
    * @since 1.0
*/
function showOrHideForgotPasswordForm(isToShow) {
    if (isToShow == true) {
        if (forgotPasswordContainer.classList.contains('d-none') == false) {
            forgotPasswordContainer.classList.add('d-none');
        }
        if (forgotPasswordEmailVerificationRequiredContainer.classList.contains('d-none') == true) {
            forgotPasswordEmailVerificationRequiredContainer.classList.remove('d-none');
        }
    } else {
        if (forgotPasswordContainer.classList.contains('d-none') == true) {
            forgotPasswordContainer.classList.remove('d-none');
        }
        if (forgotPasswordEmailVerificationRequiredContainer.classList.contains('d-none') == false) {
            forgotPasswordEmailVerificationRequiredContainer.classList.add('d-none');
        }
    }
}