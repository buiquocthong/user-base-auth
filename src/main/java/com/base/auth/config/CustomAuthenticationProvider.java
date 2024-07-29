package com.base.auth.config;

import com.base.auth.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new AuthenticationException("Authentication object is null") {};
        }

        Object details = authentication.getDetails();
        String usernameOrEmail;
        if (details instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> detailsMap = (Map<String, Object>) details;
            usernameOrEmail = (String) detailsMap.get("email");
        } else {
            throw new AuthenticationException("Details is not of type Map") {};
        }        String password = authentication.getCredentials().toString();

        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(usernameOrEmail);
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException("Invalid username/email or password") {};
        }

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    userDetails, password, userDetails.getAuthorities());
        } else {
            throw new AuthenticationException("Invalid username/email or password") {};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
