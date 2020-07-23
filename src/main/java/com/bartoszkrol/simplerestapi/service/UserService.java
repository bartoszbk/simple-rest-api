package com.bartoszkrol.simplerestapi.service;

import com.bartoszkrol.simplerestapi.utils.WebClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {

    RequestCountService requestCountService;

    ObjectMapper objectMapper;

    public Map<String, Object> getUserByLogin(String login) {
        return getGithubUserByLogin(login);
    }

    private Map<String, Object> getGithubUserByLogin(String login) {
        String response = getGithubRequestSpec(login).bodyToMono(String.class).block();
        requestCountService.incrementCountByLogin(login);
        return parseGitHubResponse(response);
    }

    private Map<String, Object> parseGitHubResponse(String response) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            responseMap = objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return responseMap;
    }

    private WebClient.ResponseSpec getGithubRequestSpec(String login) {
        WebClient webClient = WebClientBuilder.getWebClient("https://api.github.com/users");
        return webClient.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder.path("/{login}").build(login)).retrieve();
    }
}
