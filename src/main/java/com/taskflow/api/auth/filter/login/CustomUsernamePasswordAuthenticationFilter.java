package com.taskflow.api.auth.filter.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class CustomUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public CustomUsernamePasswordAuthenticationFilter(AntPathRequestMatcher antPathRequestMatcher) {
        super(antPathRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainUsername(HttpServletRequest request) {
        return request.getParameter("email");
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter("password");
    }

    private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}