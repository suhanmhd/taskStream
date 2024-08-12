package com.hatio.taskStream.service;

public interface GitHubAuthTokenService {
    void processGitHubCallback(String code, String state);
     String buildGitHubAuthorizationUrl();
    String getGitHubAccessToken(String authorizationCode);
}
