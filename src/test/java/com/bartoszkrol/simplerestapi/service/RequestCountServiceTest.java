package com.bartoszkrol.simplerestapi.service;

import com.bartoszkrol.simplerestapi.BaseSpringTestClass;
import com.bartoszkrol.simplerestapi.domain.RequestCount;
import com.bartoszkrol.simplerestapi.repository.RequestCountRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class RequestCountServiceTest extends BaseSpringTestClass {

    @Autowired
    RequestCountRepository requestCountRepository;

    @Autowired
    RequestCountService requestCountService;

    @Test
    void incrementCountByLoginWhenUserNotExistsIsCorrect() {
        requestCountService.incrementCountByLogin("mock_user");
        RequestCount expected = new RequestCount("mock_user", 1);

        Assert.assertEquals(expected, requestCountRepository.getByLogin("mock_user").get());
    }

    @Test
    void incrementCountByLoginWhenUserExistsIsCorrect() {
        requestCountRepository.save(new RequestCount("mock_user", 2));
        requestCountService.incrementCountByLogin("mock_user");
        RequestCount expected = new RequestCount("mock_user", 3);

        Assert.assertEquals(expected, requestCountRepository.getByLogin("mock_user").get());
    }

}