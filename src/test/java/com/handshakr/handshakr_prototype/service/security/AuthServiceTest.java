package com.handshakr.handshakr_prototype.service.security;

import com.handshakr.handshakr_prototype.exceptions.UserExceptionFactory;
import com.handshakr.handshakr_prototype.exceptions.user.UserExceptionType;
import com.handshakr.handshakr_prototype.security.auth.AuthServiceImpl;
import com.handshakr.handshakr_prototype.user.User;
import com.handshakr.handshakr_prototype.user.UserService;
import com.handshakr.handshakr_prototype.user.dto.LoginRequest;
import com.handshakr.handshakr_prototype.user.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserExceptionFactory userExceptionFactory;

    @InjectMocks
    private AuthServiceImpl authService;

    // ========== REGISTER TESTS ==========
    @Test
    void register_ValidRequest_ReturnsUser() {
        RegisterRequest request = new RegisterRequest("user", "user@test.com", "Password123!");
        User mockUser = new User("user", "user@test.com", "encodedPass");

        when(userService.usernameExists("user")).thenReturn(false);
        when(userService.emailExists("user@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPass");
        when(userService.saveUser(any(User.class))).thenReturn(mockUser);

        User result = authService.register(request);

        assertThat(result.getUsername()).isEqualTo("user");
        verify(userService).saveUser(any(User.class));
    }

    @Test
    void register_ExistingUsername_ThrowsException() {
        RegisterRequest request = new RegisterRequest("existing", "user@test.com", "pass");
        when(userService.usernameExists("existing")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void register_WeakPassword_ThrowsException() {
        RegisterRequest request = new RegisterRequest("user", "user@test.com", "short");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("at least 8 characters");
    }

    // ========== AUTHENTICATE TESTS ==========
    @Test
    void authenticate_ValidCredentials_ReturnsUserDetails() {
        LoginRequest request = new LoginRequest("user", "pass");
        UserDetails mockUserDetails = mock(UserDetails.class);
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(mockUserDetails);

        UserDetails result = authService.authenticate(request);

        assertThat(result).isEqualTo(mockUserDetails);
    }

    @Test
    void authenticate_InvalidCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest("user", "wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_LockedAccount_ThrowsException() {
        LoginRequest request = new LoginRequest("locked", "pass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new LockedException("Account locked"));

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(LockedException.class)
                .hasMessage("Account locked");
    }
}
