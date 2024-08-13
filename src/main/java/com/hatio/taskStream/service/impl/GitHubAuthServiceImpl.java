package com.hatio.taskStream.service.impl;


import com.hatio.taskStream.auth.entities.User;
import com.hatio.taskStream.auth.repositories.UserRepository;
import com.hatio.taskStream.config.EncryptionUtil;
import com.hatio.taskStream.service.GitHubAuthTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class GitHubAuthServiceImpl implements GitHubAuthTokenService {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private  final UserRepository userRepository;
    private  static Logger logger = LoggerFactory.getLogger( GitHubAuthServiceImpl .class);

    public String buildGitHubAuthorizationUrl() {
        String state = SecurityContextHolder.getContext().getAuthentication().getName();
        return "https://github.com/login/oauth/authorize?client_id=" + clientId +
                "&redirect_uri=" + redirectUri + "&scope=gist&state=" + state;
    }


    @Override
    public void processGitHubCallback(String code, String username) {

        try {
            String accessToken = getGitHubAccessToken(code);
            String encryptedToken = EncryptionUtil.encrypt(accessToken);


            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        logger.error("User not found with username: {}", username);
                        return new UsernameNotFoundException("User not found with username: " + username);
                    });

            user.setGithubToken(encryptedToken);
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error encrypting GitHub token: {}", e.getMessage());
            throw new RuntimeException("Error processing GitHub callback", e);
        }
    }

    public String getGitHubAccessToken(String authorizationCode) {
        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", authorizationCode);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, String> responseBody = response.getBody();
            System.out.println(response.getBody());
            return responseBody.get("access_token");
        } else {
            throw new RuntimeException("Failed to get GitHub access token");
        }
    }

}
