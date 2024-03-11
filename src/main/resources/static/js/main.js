document.addEventListener("DOMContentLoaded", function() {
    const menuIcon = document.querySelector('.menu-icon');
    const dropdownContent = document.querySelector('.dropdown-content');

    // Check if menuIcon and dropdownContent exist before adding event listener
    if (menuIcon && dropdownContent) {
        menuIcon.addEventListener('click', function() {
            dropdownContent.classList.toggle('show');
        });
    } else {
        console.error("menuIcon or dropdownContent not found.");
    }
});
window.onclick = function(event) {
    if (!event.target.matches('.menu-icon')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}
function hideError(input){
    $(input).next('.error-message').hide();

    if (input.id === 'password') {
        $('.input-container.confirm-password').removeClass('move-down');
    }
}


