function hideError(input){
    $(input).next('.error-message').hide();

    if (input.id === 'password') {
        $('.input-container.confirm-password').removeClass('move-down');
    }
}


