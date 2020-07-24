package com.bartoszkrol.simplerestapi.service;

import com.bartoszkrol.simplerestapi.enumeration.JsonFields;
import com.bartoszkrol.simplerestapi.exception.UserNotFoundException;
import com.bartoszkrol.simplerestapi.utils.WebClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RequestCountService requestCountService;

    private final ObjectMapper objectMapper;

    @Value("${github.url}")
    private String githubUrl;

    @Value("${github.users_endpoint}")
    private String githubUsers;

    public Map<String, Object> getUserByLogin(String login) throws UserNotFoundException {
        return getOutput(login);
    }

    private Map<String, Object> getOutput(String login) throws UserNotFoundException {
        Map<String, Object> gitHubResponse = getGithubUserByLogin(login);

        Map<String, Object> output = new LinkedHashMap<>();
        output.put(JsonFields.ID.getName(), gitHubResponse.getOrDefault(JsonFields.ID.getName(), ""));
        output.put(JsonFields.LOGIN.getName(), gitHubResponse.getOrDefault(JsonFields.LOGIN.getName(), ""));
        output.put(JsonFields.NAME.getName(), gitHubResponse.getOrDefault(JsonFields.NAME.getName(), ""));
        output.put(JsonFields.TYPE.getName(), gitHubResponse.getOrDefault(JsonFields.TYPE.getName(), ""));
        output.put(JsonFields.AVATAR_URL.getName(), gitHubResponse.getOrDefault(JsonFields.GITHUB_AVATAR_URL.getName(), ""));
        output.put(JsonFields.CREATED_AT.getName(), gitHubResponse.getOrDefault(JsonFields.GITHUB_CREATED_AT.getName(), ""));
        output.put(JsonFields.CALCULATIONS.getName(), getCalculation(gitHubResponse).toString());

        return output;
    }

    private Double getCalculation(Map<String, Object> githubResponse) {
        return 6.0 / ((Integer) githubResponse.get(JsonFields.FOLLOWERS.getName())) * (2 + ((Integer) githubResponse.get(JsonFields.PUBLIC_REPOS.getName())));
    }

    private Map<String, Object> getGithubUserByLogin(String login) throws UserNotFoundException {
        String response = retrieveGithubUser(login);

        requestCountService.incrementCountByLogin(login);
        return parseGitHubResponse(response);
    }

    private String retrieveGithubUser(String login) throws UserNotFoundException {

        String response;

        try {
            response = getGithubRequestSpec(login).bodyToMono(String.class).block();
        } catch (Exception e) {
            log.error(String.format("Invalid request for user login: %s", login));
            throw new UserNotFoundException(String.format("User with login: %s not found", login));
        }

        return response;
    }

    private Map<String, Object> parseGitHubResponse(String response) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            responseMap = objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            log.error(String.format("Error parsing Github response: %s", e.getMessage()));
        }

        return responseMap;
    }

    private WebClient.ResponseSpec getGithubRequestSpec(String login) {
        WebClient webClient = WebClientBuilder.getWebClient(githubUrl + githubUsers);
        return webClient.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder.path("/{login}").build(login)).retrieve();
    }
}
