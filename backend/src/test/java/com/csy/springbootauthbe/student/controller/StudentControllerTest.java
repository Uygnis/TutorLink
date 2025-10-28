package com.csy.springbootauthbe.student.controller;

import com.csy.springbootauthbe.student.dto.StudentDTO;
import com.csy.springbootauthbe.student.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(
        controllers = StudentController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    // ✅ Mock security-related beans so Spring context doesn’t fail
    @MockBean
    private com.csy.springbootauthbe.config.JWTAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.csy.springbootauthbe.config.JWTService jwtService;

    @MockBean
    private com.csy.springbootauthbe.common.wrapper.UserDetailsServiceWrapper userDetailsServiceWrapper;

    @Test
    void getStudentByUserId_ok_returns200() throws Exception {
        var dto = new StudentDTO();
        dto.setId("S1");
        dto.setUserId("U1");

        when(studentService.getStudentByUserId("U1")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/students/by-user/{userId}", "U1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void getStudentByUserId_notFound_returns404() throws Exception {
        when(studentService.getStudentByUserId("U404")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/students/by-user/{userId}", "U404"))
                .andExpect(status().isNotFound());
    }
}
