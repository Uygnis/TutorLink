package com.csy.springbootauthbe;

import com.csy.springbootauthbe.config.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SpringbootAuthBeApplicationTests {

    @MockBean
    JWTService jwtService;

    @Test
    void contextLoads() { }
}
