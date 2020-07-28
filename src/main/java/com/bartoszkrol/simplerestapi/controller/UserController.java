package com.bartoszkrol.simplerestapi.controller;

import com.bartoszkrol.simplerestapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping(value = "/users/{login}")
    public ResponseEntity<Map<String, Object>> getUserByLogin(@PathVariable("login") String login) {
        log.info(String.format("User request for login: %s ", login));
        return ResponseEntity.ok(userService.getUserByLogin(login));
    }
}
