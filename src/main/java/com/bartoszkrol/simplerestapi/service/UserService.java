package com.bartoszkrol.simplerestapi.service;

import com.bartoszkrol.simplerestapi.enumeration.JsonFields;
import com.bartoszkrol.simplerestapi.exception.ExternalApiException;
import com.bartoszkrol.simplerestapi.exception.InvalidRequestException;
import com.bartoszkrol.simplerestapi.exception.UserNotFoundException;
import com.bartoszkrol.simplerestapi.utils.WebClientProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RequestCountService requestCountService;

    private final ObjectMapper objectMapper;

    @Value("${github.users_url}")
    private String githubUsers;

    public Map<String, Object> getUserByLogin(String login) {
        return getOutput(login);
    }

    private Map<String, Object> getOutput(String login) {
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
        String login = (String) githubResponse.getOrDefault(JsonFields.ID.getName(), "null");
        int followersNum = (Integer) githubResponse.getOrDefault(JsonFields.FOLLOWERS.getName(), 0);
        int reposNum = (Integer) githubResponse.getOrDefault(JsonFields.PUBLIC_REPOS.getName(), 0);

        if (followersNum == 0 || reposNum == 0) {
            log.error(String.format("Invalid division by zero for user with login: %s", login));
            return 0.0;
        }

        return 6.0 / followersNum * (2.0 + reposNum);
    }

    private Map<String, Object> getGithubUserByLogin(String login) {
        String response = retrieveGithubUser(login);
        requestCountService.incrementCountByLogin(login);
        return parseGitHubResponse(response);
    }

    private String retrieveGithubUser(String login) {
        return getGithubRequestSpec(login).onStatus(HttpStatus.NOT_FOUND::equals, clientNFResponse -> {
            log.error(String.format("User '%s' not found", login));
            return Mono.error(new UserNotFoundException(String.format("User with login: '%s' not found", login)));
        }).onStatus(HttpStatus::is4xxClientError, clientErrResponse -> {
            log.error(String.format("Invalid request login: '%s'", login));
            return Mono.error(new InvalidRequestException(String.format("Invalid request for user with login: '%s'", login)));
        }).onStatus(HttpStatus::is5xxServerError, serverErrResponse -> {
            log.error(String.format("External API Server Error for user login: '%s'", login));
            return Mono.error(new ExternalApiException(String.format("External API Error for user: '%s'", login)));
        }).bodyToMono(String.class).block();
    }

    private Map<String, Object> parseGitHubResponse(String response) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            responseMap = objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            log.error(String.format("Error parsing Github response for login: '%s'", e.getMessage()));
        }

        return responseMap;
    }

    private WebClient.ResponseSpec getGithubRequestSpec(String login) {
        WebClient webClient = WebClientProvider.getWebClient(githubUsers);
        return webClient.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder.path("/{login}").build(login)).retrieve();
    }
}
