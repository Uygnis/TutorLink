package com.csy.springbootauthbe.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Mock
    private JWTAuthenticationFilter jwtAuthFilter;

    @Mock
    private AuthenticationProvider authProvider;

    @Mock
    private HttpSecurity httpSecurity;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig(jwtAuthFilter, authProvider);
    }

    @Test
    void securityFilterChain_configuresExpectedComponents() throws Exception {
        // Arrange
        SecurityFilterChain mockChain = mock(SecurityFilterChain.class);

        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        // 👇 Fix — use doReturn(...).when(...) for generic type safety
        doReturn(mockChain).when(httpSecurity).build();

        // Act
        SecurityFilterChain result = securityConfig.securityFilterChain(httpSecurity);

        // Assert
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(authProvider);
        verify(httpSecurity).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        assertNotNull(result);
    }
}
