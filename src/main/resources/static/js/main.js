$(document).ready(function() {
    // Check for password matching in real-time
    $('#password, #confirm-password').on('keyup', function () {
        if ($('#password').val() === $('#confirm-password').val()) {
            $('#password-message').html('Passwords match').css('color', 'green');
        } else {
            $('#password-message').html('Passwords do not match').css('color', 'red');
        }
    });
    // Toggle show/hide password
    $('#toggle-password').change(function() {
        if($(this).is(':checked')) {
            $('#password').attr('type', 'text');
        } else {
            $('#password').attr('type', 'password');
        }
    });

    $('#toggle-confirm-password').change(function() {
        if($(this).is(':checked')) {
            $('#confirm-password').attr('type', 'text');
        } else {
            $('#confirm-password').attr('type', 'password');
        }
    });
});

function hideError(input){
    $(input).next('.error-message').hide();
}

