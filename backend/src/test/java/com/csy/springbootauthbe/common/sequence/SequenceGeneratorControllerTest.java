package com.csy.springbootauthbe.common.sequence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(
        controllers = SequenceGeneratorController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class SequenceGeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SequenceGeneratorService sequenceGeneratorService;

    // ✅ Mock any required security filters to avoid startup failures
    @MockBean
    private com.csy.springbootauthbe.config.JWTAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.csy.springbootauthbe.config.JWTService jwtService;

    @MockBean
    private com.csy.springbootauthbe.common.wrapper.UserDetailsServiceWrapper userDetailsServiceWrapper;

    @Test
    void getNextStudentId_returns200WithNextId() throws Exception {
        when(sequenceGeneratorService.peekNextStudentId()).thenReturn("S101");

        mockMvc.perform(get("/api/v1/seq/next-id"))
                .andExpect(status().isOk())
                .andExpect(content().string("S101"));
    }
}
