$(document).ready(function() {

    let showModal = document.querySelector('#showSuccessModal').value === 'true';

    if (showModal) {
        $('#successModal').modal('show');
    }
});

function hideError(input){
    $(input).next('.error-message').hide();
}
