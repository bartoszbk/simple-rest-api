package com.bartoszkrol.simplerestapi.service;

import com.bartoszkrol.simplerestapi.BaseSpringTestClass;
import com.bartoszkrol.simplerestapi.enumeration.JsonFields;
import com.bartoszkrol.simplerestapi.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserServiceTest extends BaseSpringTestClass {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String localhost = "http://localhost:8100";

    private final String id = "1111";

    private static final String login = "test_user";

    private final String username = "Test User";

    private final String createdDate = "2020-02-25T18:44:36Z";

    private final String avatarUrl = "https://avatars3.githubusercontent.com/u/1111?v=4";

    public static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8100);
        mockWebServer.url(localhost + "/" + login);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getUserByLoginWithExistingUserIsCorrect() throws Exception {
        Map<String, Object> mockResponse = getMockResponseWithPositiveFollowersAndRepos();
        Map<String, Object> result = makeMockCall(mockResponse, 200);
        Map<String, Object> expectedResult = getExpectedResult(4.2);

        Assert.assertEquals(objectMapper.writeValueAsString(expectedResult), objectMapper.writeValueAsString(result));
    }

    @Test
    void getUserByLoginWithUserWithZeroFollowersIsCorrect() throws Exception {
        Map<String, Object> mockResponse = getMockResponseWithZeroFollowers();
        Map<String, Object> result = makeMockCall(mockResponse, 200);
        Map<String, Object> expectedResult = getExpectedResult(0.0);

        Assert.assertEquals(objectMapper.writeValueAsString(expectedResult), objectMapper.writeValueAsString(result));
    }

    @Test
    void getUserByLoginWithUserWithZeroPublicReposIsCorrect() throws Exception {
        Map<String, Object> mockResponse = getMockResponseWithZeroPublicRepos();
        Map<String, Object> result = makeMockCall(mockResponse, 200);
        Map<String, Object> expectedResult = getExpectedResult(0.0);

        Assert.assertEquals(objectMapper.writeValueAsString(expectedResult), objectMapper.writeValueAsString(result));
    }

    @Test
    void getUserByLoginWithUserWithZeroFollowersAndZeroPublicReposIsCorrect() throws Exception {
        Map<String, Object> mockResponse = getMockResponseWithZeroFollowersAndZeroPublicRepos();
        Map<String, Object> result = makeMockCall(mockResponse, 200);
        Map<String, Object> expectedResult = getExpectedResult(0.0);

        Assert.assertEquals(objectMapper.writeValueAsString(expectedResult), objectMapper.writeValueAsString(result));
    }

    @Test
    void getUserByLoginWithNonExistingUserIsCorrect() throws Exception {
        Map<String, Object> mockResponse = getMockResponseWithNotFoundUser();
        makeMockCall(mockResponse, 404);

        Assert.assertThrows(UserNotFoundException.class, () -> userService.getUserByLogin(login));
    }

    private Map<String, Object> makeMockCall(Map<String, Object> mockResponse, int statusCode) throws Exception {
        enqueueMockResponse(mockResponse, statusCode);
        Map<String, Object> result = userService.getUserByLogin(login);
        return result;
    }

    private void enqueueMockResponse(Map<String, Object> mockResponse, int statusCode) throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(statusCode)
                .setBody(objectMapper.writeValueAsString(mockResponse))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    private Map<String, Object> getExpectedResult(Double calculationResult) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(JsonFields.ID.getName(), id);
        result.put(JsonFields.LOGIN.getName(), login);
        result.put(JsonFields.NAME.getName(), username);
        result.put(JsonFields.TYPE.getName(), "User");
        result.put(JsonFields.AVATAR_URL.getName(), avatarUrl);
        result.put(JsonFields.CREATED_AT.getName(), createdDate);
        result.put(JsonFields.CALCULATIONS.getName(), calculationResult.toString());

        return result;
    }

    private Map<String, Object> getMockResponseWithPositiveFollowersAndRepos() {
        Map<String, Object> response = getMockResponse();
        putFollowersAndRepos(response, 10, 5);
        return response;
    }

    private Map<String, Object> getMockResponseWithZeroFollowers() {
        Map<String, Object> response = getMockResponse();
        putFollowersAndRepos(response, 0, 8);
        return response;
    }


    private Map<String, Object> getMockResponseWithZeroPublicRepos() {
        Map<String, Object> response = getMockResponse();
        putFollowersAndRepos(response, 10, 0);
        return response;
    }

    private Map<String, Object> getMockResponseWithZeroFollowersAndZeroPublicRepos() {
        Map<String, Object> response = getMockResponse();
        putFollowersAndRepos(response, 0, 0);
        return response;
    }

    private Map<String, Object> getMockResponse() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(JsonFields.ID.getName(), id);
        response.put(JsonFields.LOGIN.getName(), login);
        response.put(JsonFields.NAME.getName(), username);
        response.put(JsonFields.TYPE.getName(), "User");
        response.put(JsonFields.GITHUB_AVATAR_URL.getName(), avatarUrl);
        response.put(JsonFields.GITHUB_CREATED_AT.getName(), createdDate);

        return response;
    }

    private Map<String, Object> getMockResponseWithNotFoundUser() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Not Found");
        response.put("documentation_url", "https://developer.github.com/v3/users/#get-a-single-user");
        return response;
    }

    private Map<String, Object> putFollowersAndRepos(Map<String, Object> response, int followers, int repos) {
        response.put(JsonFields.FOLLOWERS.getName(), followers);
        response.put(JsonFields.PUBLIC_REPOS.getName(), repos);
        return response;
    }

}
