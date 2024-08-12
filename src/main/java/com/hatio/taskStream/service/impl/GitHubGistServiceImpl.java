package com.hatio.taskStream.service.impl;

import com.hatio.taskStream.service.GitHubGistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class GitHubGistServiceImpl implements GitHubGistService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubGistServiceImpl.class);

    private final String githubApiUrl = "https://api.github.com/gists";
    private final RestTemplate restTemplate;

    @Override
    public String createSecretGist(String fileName, String content, String userGitHubToken) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "token " + userGitHubToken);
        headers.set("Accept", "application/vnd.github+json");

        Map<String, Object> gistRequest = new HashMap<>();
        gistRequest.put("description", "Project summary for " + fileName);
        gistRequest.put("public", false);

        Map<String, String> fileContent = new HashMap<>();
        fileContent.put("content", content);

        Map<String, Object> files = new HashMap<>();
        files.put(fileName + ".md", fileContent);

        gistRequest.put("files", files);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(gistRequest, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    githubApiUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                return (String) responseBody.get("html_url"); // Return the URL of the created gist
            } else {
                String errorMessage = String.format("Failed to create gist: %s - %s", response.getStatusCode(), response.getBody());
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (RestClientException e) {
            logger.error("Error occurred while creating gist: ", e);
            throw new RuntimeException("Error occurred while creating gist", e);
        }
    }
}
