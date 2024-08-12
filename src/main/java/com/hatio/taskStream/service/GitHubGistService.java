package com.hatio.taskStream.service;

public interface GitHubGistService {
    String createSecretGist(String title, String content, String token);
}
