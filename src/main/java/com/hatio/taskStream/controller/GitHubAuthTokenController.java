package com.hatio.taskStream.controller;

import com.hatio.taskStream.service.GitHubAuthTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth/github")
@RequiredArgsConstructor
public class GitHubAuthTokenController {

    private final GitHubAuthTokenService gitHubAuthTokenService;
    private static final Logger logger = LoggerFactory.getLogger(GitHubAuthTokenController.class);

    @GetMapping("/callback")
    public ResponseEntity<String> handleGitHubCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        try {
            gitHubAuthTokenService.processGitHubCallback(code, state);
            return ResponseEntity.ok("Access token saved successfully");

        } catch (Exception e) {
            logger.error("Error during GitHub authentication process", e);
            return ResponseEntity.status(500).body("An error occurred during GitHub authentication");
        }
    }


}
