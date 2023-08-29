package com.shivsundar.gateway.controllers;

import com.shivsundar.gateway.models.AuthResponse.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    ResponseEntity<AuthResponse> login(
            @RegisteredOAuth2AuthorizedClient("okta")OAuth2AuthorizedClient client,
            @AuthenticationPrincipal  OidcUser user,
            Model model
            ){
        logger.info("user email id : {} ",user.getEmail());
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        AuthResponse authResponse = AuthResponse.builder()
                .userId(user.getEmail())
                .accessToken(client.getAccessToken().getTokenValue())
                .refreshToken(client.getRefreshToken().getTokenValue())
                .expireAt(user.getExpiresAt().getEpochSecond())
                .authorities(authorities)
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
