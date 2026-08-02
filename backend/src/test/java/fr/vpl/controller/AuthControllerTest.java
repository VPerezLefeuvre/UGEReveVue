package fr.vpl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.vpl.dto.LoginRequest;
import fr.vpl.dto.RegisterRequest;
import fr.vpl.entity.User;
import fr.vpl.exception.InvalidCredentialsException;
import fr.vpl.exception.UserAlreadyExistsException;
import fr.vpl.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC slice tests for {@link AuthController}.
 * The service is mocked so these tests focus on HTTP status codes, JSON validation,
 * request mapping, and exception-to-response conversion.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("AuthController MVC tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("register returns 201 when the request is valid")
    void register_shouldReturnCreated_whenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest("john_doe", "john@example.com", "Password123!");
        User user = User.builder()
                .id(1L)
                .username(request.username())
                .email(request.email())
                .password("encoded")
                .build();
        when(authService.register(request)).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(authService).register(request);
    }

    @Test
    @DisplayName("register returns 400 with field errors when the request is invalid")
    void register_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest("john_doe", "john@example.com", "weak");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    @DisplayName("register returns 409 when the username already exists")
    void register_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("existing_user", "new@test.com", "Password123!");
        doThrow(new UserAlreadyExistsException("username")).when(authService).register(request);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details.username[0]").value("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("register returns 409 when the email already exists")
    void register_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("new_user", "existing@example.com", "Password123!");
        doThrow(new UserAlreadyExistsException("email")).when(authService).register(request);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details.email[0]").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("login returns 200 with user data when credentials are valid")
    void login_shouldReturnOkWithUserData_whenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "Password123!");
        User user = User.builder()
                .id(1L)
                .username("john_doe")
                .email(request.email())
                .password("encoded")
                .role(User.Role.USER)
                .build();
        when(authService.login(request)).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).login(request);
    }

    @Test
    @DisplayName("login returns 400 with field errors when the request is invalid")
    void login_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    @DisplayName("login returns 401 when credentials are invalid")
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "Wrong123!");
        doThrow(new InvalidCredentialsException()).when(authService).login(request);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.details.auth[0]").value("INVALID_CREDENTIALS"));
    }

}
