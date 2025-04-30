package com.handshakr.handshakr_prototype.repository;

import com.handshakr.handshakr_prototype.security.PersistentCookieCsrfTokenRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentCookieCsrfTokenRepositoryTest {

    private final PersistentCookieCsrfTokenRepository repository = new PersistentCookieCsrfTokenRepository();

    @Test
    void generateToken_WhenNoCookie_CreatesNewToken() {
        CsrfToken token = repository.generateToken(new MockHttpServletRequest());
        assertThat(token.getToken()).isNotBlank();
    }

    @Test
    void saveToken_WhenNewToken_SetsSecureCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        CsrfToken token = repository.generateToken(new MockHttpServletRequest());

        repository.saveToken(token, new MockHttpServletRequest(), response);

        assertThat(response.getHeader("Set-Cookie"))
                .contains("Secure")
                .contains("SameSite=None");
    }

    @Test
    void loadToken_WhenCookieExists_ReturnsToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("XSRF-TOKEN", "test-token"));

        CsrfToken token = repository.loadToken(request);

        assertThat(token.getToken()).isEqualTo("test-token");
    }
}
