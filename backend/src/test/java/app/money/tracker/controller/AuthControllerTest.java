package app.money.tracker.controller;

import app.money.tracker.backend.controller.AuthController;
import app.money.tracker.backend.dto.auth.LoginRequest;
import app.money.tracker.backend.entity.UserEntity;
import app.money.tracker.backend.repository.UserRepository;
import app.money.tracker.backend.security.SecurityConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@ContextConfiguration(classes = app.money.tracker.backend.BackendApplication.class)
public class AuthControllerTest extends AbstractTestNGSpringContextTests {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @Description("""
            1. calls /api/auth/csrf,
            2. uses the returned token + cookie to POST /api/auth/login,
            3. and then hits /api/auth/me with the session cookie to confirm the session works.
            """)
    public void loginFlow_shouldReturnSessionUser() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String email = "user@example.com";
        String password = "secret";

        UserEntity user = UserEntity.builder()
                .id(userId)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(OffsetDateTime.now())
                .build();

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        MvcResult csrfResult = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, String> csrfBody = objectMapper.readValue(
                csrfResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        String csrfToken = csrfBody.get("token");
        String csrfHeader = csrfBody.get("headerName");
        Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
        if (csrfCookie == null) {
            csrfCookie = new Cookie("XSRF-TOKEN", csrfToken);
        }
        log.info("CSRF header={} token={}", csrfHeader, csrfToken);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(csrfHeader, csrfToken)
                        .cookie(csrfCookie)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        log.info("Session created={}", session != null);

        mockMvc.perform(get("/api/auth/me")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(email));
    }
}
