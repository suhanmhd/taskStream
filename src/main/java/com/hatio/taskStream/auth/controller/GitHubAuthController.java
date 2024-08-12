//package com.hatio.taskStream.auth.controller;
//
//
//import com.hatio.taskStream.auth.entities.User;
//import com.hatio.taskStream.auth.repositories.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Controller;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Collections;
//import java.util.Map;
//
//
//@Controller
//@RequestMapping("/auth/github")
//@RequiredArgsConstructor
//public class GitHubAuthController {
//
//    @Value("${github.client-id}")
//    private String clientId;
//
//    @Value("${github.client-secret}")
//    private String clientSecret;
//
//    @Value("${github.redirect-uri}")
//    private String redirectUri;
//
//    private final RestTemplate restTemplate;
//    private  final UserRepository userRepository;
//    private  static Logger logger = LoggerFactory.getLogger(GitHubAuthController.class);
//
//
//
//
//
//    @GetMapping("/callback")
//    public ResponseEntity<String> handleGitHubCallback(@RequestParam("code") String code,@RequestParam("state") String state) {
//        String accessToken = getGitHubAccessToken(code);
//        String username = state;
//        User user = userRepository.findByEmail(username)
//                .orElseThrow(() -> {
//                    logger.error("User not found with username: {}", username);
//                    return new UsernameNotFoundException("User not found with username: " + username);
//                });
//        user.setGithubToken(accessToken);
//        userRepository.save(user);
//
//        // Store the access token securely, associate it with the user
//        return ResponseEntity.ok("Access token saved sucessfully");
//    }
//
//    private String getGitHubAccessToken(String authorizationCode) {
//        String tokenUrl = "https://github.com/login/oauth/access_token";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("client_id", clientId);
//        body.add("client_secret", clientSecret);
//        body.add("code", authorizationCode);
//        body.add("redirect_uri", redirectUri);
//
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(
//                tokenUrl,
//                HttpMethod.POST,
//                entity,
//                Map.class
//        );
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            Map<String, String> responseBody = response.getBody();
//            System.out.println(response.getBody());
//            return responseBody.get("access_token");
//        } else {
//            throw new RuntimeException("Failed to get GitHub access token");
//        }
//    }
//}
