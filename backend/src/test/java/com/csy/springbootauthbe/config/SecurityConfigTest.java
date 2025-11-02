package com.csy.springbootauthbe.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private JWTAuthenticationFilter jwtFilter;
    private AuthenticationProvider authProvider;
    private SecurityConfig config;

    @BeforeEach
    void setUp() {
        jwtFilter = mock(JWTAuthenticationFilter.class);
        authProvider = mock(AuthenticationProvider.class);
        config = new SecurityConfig(jwtFilter, authProvider);
    }

    @Test
    void testSecurityFilterChain_BuildsSuccessfully() throws Exception {
        // ✅ Create a deep mock for chaining fluent API
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        // Fake chain returned by build()
        DefaultSecurityFilterChain mockChain = mock(DefaultSecurityFilterChain.class);

        // ✅ Stub all chainable methods
        when(http.csrf(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authenticationProvider(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        when(http.build()).thenReturn(mockChain);

        // Execute the config
        var chain = config.securityFilterChain(http);

        // ✅ Verify behavior
        assertNotNull(chain);
        verify(http).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        verify(http).authenticationProvider(authProvider);
        verify(http).build();
    }
}
