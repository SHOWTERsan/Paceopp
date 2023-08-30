$(document).ready(function() {
    // Check for password matching in real-time
    $('#password, #confirm-password').on('keyup', function () {
        const password = $('#password').val();
        const confirmPassword = $('#confirm-password').val();

        // Clears the message if either field is empty
        if(!password || !confirmPassword) {
            $('#password-message').html('').css('color', 'black');
            return;
        }

        // Comparison only occurs when both fields are filled
        if(password && confirmPassword) {
            $('#password-message').html(`${(password === confirmPassword) ? 'Passwords match' : 'Passwords do not match'}`)
                .css('color', `${(password === confirmPassword) ? 'green' : 'red'}`);
        }
    });
    // Prevent form submission if passwords do not match
    $('#signup-form').on('submit', function(e) {
        if ($('#password').val() !== $('#confirm-password').val()) {
            e.preventDefault();
            alert('Passwords do not match. Please, make sure you enter the same password in both fields.');
        }
    });
    // Check if there's a password error message
    if ($("#password-error").text().trim().length > 0) {
        $('.input-container.confirm-password').addClass('move-down');
    }
});
$(".toggle-password").click(function() {
    $(this).toggleClass("fa-eye fa-eye-slash");
    var input = $($(this).attr("toggle"));
    if (input.attr("type") === "password") {
        input.attr("type", "text");
    } else {
        input.attr("type", "password");
    }
});