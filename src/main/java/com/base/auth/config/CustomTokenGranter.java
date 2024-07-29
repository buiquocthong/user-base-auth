package com.base.auth.config;

import com.base.auth.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
public class CustomTokenGranter extends AbstractTokenGranter {

    private UserServiceImpl userService;
    private AuthenticationManager authenticationManager;

    protected CustomTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    public CustomTokenGranter(AuthenticationManager authenticationManager,AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType, UserServiceImpl userService) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        return super.getOAuth2Authentication(client, tokenRequest);
    }

//    @Override
//    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
//        String grantType = tokenRequest.getGrantType();
//        String username = tokenRequest.getRequestParameters().get("username");
//        String password = tokenRequest.getRequestParameters().get("password");
//
//        try {
//            if (SecurityConstant.GRANT_TYPE_USER.equalsIgnoreCase(grantType)) {
//                String phone = tokenRequest.getRequestParameters().get("phone");
//                return userService.getAccessTokenForUser(client, tokenRequest, password, phone, this.getTokenServices());
//            } else if (SecurityConstant.GRANT_TYPE_PASSWORD.equalsIgnoreCase(grantType)) {
//                String email = tokenRequest.getRequestParameters().get("email");
//                return userService.getAccessTokenForCustomTypeEmail(client, tokenRequest, email, password, this.getTokenServices());
//            } else if (SecurityConstant.GRANT_TYPE_EMAIL.equalsIgnoreCase(grantType)) {
//                String email = tokenRequest.getRequestParameters().get("email");
//                return userService.getAccessTokenForCustomTypeEmail(client, tokenRequest, email, password, this.getTokenServices());
//            }
//        } catch (GeneralSecurityException | IOException e) {
//            e.printStackTrace();
//            throw new InvalidTokenException("account or tenant invalid");
//        }
//        return null;
//    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        String grantType = tokenRequest.getGrantType();
        String email = tokenRequest.getRequestParameters().get("email");
        String password = tokenRequest.getRequestParameters().get("password");

        log.info("Grant type: {}", grantType);
        log.info("Email: {}", email);
        log.info("Password: {}", password);

        try {

            if (SecurityConstant.GRANT_TYPE_USER.equalsIgnoreCase(grantType)) {
                String phone = tokenRequest.getRequestParameters().get("phone");
                return userService.getAccessTokenForUser(client, tokenRequest, password, phone, this.getTokenServices());
            } else if (SecurityConstant.GRANT_TYPE_PASSWORD.equalsIgnoreCase(grantType)) {
                return userService.getAccessTokenForCustom(client, tokenRequest, email, password, this.getTokenServices());
            } else if (SecurityConstant.GRANT_TYPE_EMAIL.equalsIgnoreCase(grantType)) {
                return userService.getAccessTokenForCustomTypeEmail(client, tokenRequest, email, password, this.getTokenServices());
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("Exception during authentication", e);
            throw new InvalidTokenException("Account or tenant invalid");
        }
        return null;
    }

}
