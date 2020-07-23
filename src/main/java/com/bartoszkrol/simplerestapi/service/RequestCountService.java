package com.bartoszkrol.simplerestapi.service;

import com.bartoszkrol.simplerestapi.domain.RequestCount;
import com.bartoszkrol.simplerestapi.exception.UserNotFoundException;
import com.bartoszkrol.simplerestapi.repository.RequestCountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RequestCountService {

    RequestCountRepository requestCountRepository;

    public void incrementCountByLogin(String login) throws UserNotFoundException {
        log.info(String.format("Increment request count for login: %s", login));

        requestCountRepository.getByLogin(login).map(requestCount -> requestCountRepository.save(requestCount.incrementRequestCount())).orElseGet(() -> {
            log.info(String.format("Saving user with login: %s", login));
            return requestCountRepository.save(new RequestCount(login, 1));
        });
    }

}
