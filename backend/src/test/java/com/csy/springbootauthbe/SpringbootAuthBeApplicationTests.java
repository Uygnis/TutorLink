package com.csy.springbootauthbe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = SpringbootAuthBeApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringbootAuthBeApplicationTests {

    @Test
    void contextLoads() {
    }
}