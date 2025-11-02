package com.csy.springbootauthbe.config;

import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.User;
import com.csy.springbootauthbe.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void userDetailsService_whenUserExists_returnsUserDetails() {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findByEmailAndStatusNot("test@example.com", AccountStatus.DELETED))
                .thenReturn(Optional.of(mockUser));

        var service = applicationConfig.userDetailsService();
        UserDetails user = service.loadUserByUsername("test@example.com");

        assertNotNull(user);
        assertEquals("test@example.com", ((User) user).getEmail());
    }

    @Test
    void userDetailsService_whenUserMissing_throwsException() {
        when(userRepository.findByEmailAndStatusNot("missing@example.com", AccountStatus.DELETED))
                .thenReturn(Optional.empty());

        var service = applicationConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@example.com"));
    }

    @Test
    void passwordEncoder_returnsBCryptAndMatches() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        String encoded = encoder.encode("secret123");
        assertTrue(encoder.matches("secret123", encoded));
    }

    @Test
    void authProvider_returnsDaoAuthProvider_withConfiguredFields() throws Exception {
        AuthenticationProvider provider = applicationConfig.authProvider();

        assertNotNull(provider);
        assertInstanceOf(DaoAuthenticationProvider.class, provider);

        DaoAuthenticationProvider dao = (DaoAuthenticationProvider) provider;

        // Verify internal fields via reflection
        Field userDetailsField = DaoAuthenticationProvider.class.getDeclaredField("userDetailsService");
        userDetailsField.setAccessible(true);
        Object uds = userDetailsField.get(dao);
        assertNotNull(uds, "UserDetailsService should be configured");

        Field passwordEncoderField = DaoAuthenticationProvider.class.getDeclaredField("passwordEncoder");
        passwordEncoderField.setAccessible(true);
        Object encoder = passwordEncoderField.get(dao);
        assertNotNull(encoder, "PasswordEncoder should be configured");
    }
}
