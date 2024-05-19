function getQueryParam(param) {
    var urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}
//TODO onfocus удаление ошибки
var uuid = getQueryParam('uuid');

window.addEventListener('DOMContentLoaded', function () {
    var inputs = document.querySelectorAll('.form-control');
    inputs.forEach(function (input) {
        input.setAttribute('data-original-value', input.value);
    });
});

function toggleInput(inputId) {
    var input = document.getElementById(inputId);
    var inputs = document.querySelectorAll('.form-control');

    if (!input.readOnly) {
        cancelChanges(inputId);
    } else {
        inputs.forEach(function (otherInput) {
            if (otherInput !== input && !otherInput.readOnly) {
                cancelChanges(otherInput.id);
            }
        });

        input.readOnly = !input.readOnly;
        input.classList.toggle("translucent");

        var parentContainer = input.parentElement;
        var tickBtn = parentContainer.querySelector('.tick-btn');
        var crossBtn = parentContainer.querySelector('.cross-btn');
        tickBtn.style.display = 'inline-block';
        crossBtn.style.display = 'inline-block';

        var pencilBtn = parentContainer.querySelector('.pencil-btn');
        pencilBtn.style.display = 'none';
    }
}

function cancelChanges(inputId) {
    var input = document.getElementById(inputId);
    var originalValue = input.getAttribute('data-original-value');
    input.value = originalValue;

    input.readOnly = true;
    input.classList.add("translucent");

    var parentContainer = input.parentElement;
    var tickBtn = parentContainer.querySelector('.tick-btn');
    var crossBtn = parentContainer.querySelector('.cross-btn');
    tickBtn.style.display = 'none';
    crossBtn.style.display = 'none';

    var pencilBtn = parentContainer.querySelector('.pencil-btn');
    pencilBtn.style.display = 'inline-block';

    usernameError.innerText = '';

    emailError.innerText = '';
}
function confirmUsernameChanges(inputId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    var input = document.getElementById(inputId);
    var usernameError = document.getElementById('usernameError');
    var usernameSuccess = document.getElementById('usernameSuccess');
    usernameError.innerText = '';
    usernameSuccess.innerText = '';

    if (inputId === 'userNameInput') {
        var newUsername = input.value;
        var originalUsername = input.getAttribute('data-original-value');

        // Check if the new username is empty
        if (newUsername.trim() === '') {
            usernameError.innerText = 'Имя пользователя обязательно для ввода';
            return;
        }

        if (newUsername !== originalUsername) {
            fetch('/user/profile/username', {
                method: 'POST',
                headers: {
                    [header]: token,
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    newUsername: newUsername
                })
            })
            .then(response => response.json().then(json => ({ ok: response.ok, json })))
            .then(({ ok, json }) => {
                if (ok) {
                    usernameSuccess.innerText = json.message;
                    // Update the original value attribute if the username is successfully updated
                    input.setAttribute('data-original-value', newUsername);
                } else {
                    usernameError.innerText = json.username || 'Ошибка при обновлении имени пользователя.';
                    cancelChanges(inputId);
                }
            })
            .catch(error => {
                usernameError.innerText = 'Ошибка при обновлении имени пользователя.';
                cancelChanges(inputId);
            });
            return;
        }
    }
    toggleInput(inputId);
}
function confirmEmailChanges(inputId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    var input = document.getElementById(inputId);
    var emailError = document.getElementById('emailError');
    var emailSuccess = document.getElementById('emailSuccess');
    emailError.innerText = '';
    emailSuccess.innerText = '';

    if (inputId === 'emailInput') {
        var newEmail = input.value;
        var originalEmail = input.getAttribute('data-original-value');
        // Check if the new email is empty
        if (newEmail.trim() === '') {
            emailError.innerText = 'Почту нужно ввести обязательно';
            return;
        }
        
        // Check if the email format is incorrect
        if (!isValidEmailFormat(newEmail)) {
            emailError.innerText = 'Неверный формат почты';
            return;
        }
        if (newEmail !== originalEmail) {
            fetch('/user/profile/email', {
                method: 'POST',
                headers: {
                    [header]: token,
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    newEmail: newEmail
                })
            })
            .then(response => response.json().then(json => ({ ok: response.ok, json })))
            .then(({ ok, json }) => {
                if (ok) {
                    emailSuccess.innerText = 'Проверьте свою почту для подтверждения.';
                    var uuid = json.uuid;
                    if (uuid) {
                        connectWebSocket(uuid);
                    }
                } else {
                    emailError.innerText = json.email || 'Ошибка при обновлении почты.';
                    cancelChanges(inputId);
                }
            })
            .catch(error => {
                emailError.innerText = 'Ошибка при обновлении почты.';
                cancelChanges(inputId);
            });
            return;
        }
    }
    toggleInput(inputId);
}

function isValidEmailFormat(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}
function connectWebSocket(uuid) {
    var socket = new SockJS('/auth/stompEndpoint');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/statusUpdate/' + uuid, function(message) {
            var status = message.body;

            if (status === "verified") {
                document.getElementById('emailSuccess').innerText = 'Почта подтверждена успешно.';
                var input = document.getElementById('emailInput');
                input.setAttribute('data-original-value', input.value);
                cancelChanges('emailInput');
            }
            if (status === "expired") {
                window.location.href = "/auth/verificationExpired";
            }
            if (status === "token_not_found") {
                window.location.href = "/bad_request";
            }
        });

        stompClient.subscribe('/topic/checkVerificationStatus/' + uuid, function(message) {
            stompClient.send("/app/checkVerification", {}, uuid);
        });
    });
}