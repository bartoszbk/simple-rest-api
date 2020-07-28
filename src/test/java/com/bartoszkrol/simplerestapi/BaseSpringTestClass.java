package com.bartoszkrol.simplerestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SimpleRestApiApplication.class})
@ActiveProfiles({"test"})
public class BaseSpringTestClass {

    @Autowired
    private ObjectMapper objectMapper;

}
