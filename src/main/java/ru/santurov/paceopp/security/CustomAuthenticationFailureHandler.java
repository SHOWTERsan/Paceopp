package ru.santurov.paceopp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        request.getSession().setAttribute("error", "Ваш никнейм или пароль не верны.");
        request.getSession().setAttribute("username", request.getParameter("username"));

        response.sendRedirect("/auth/signin");
    }
}
