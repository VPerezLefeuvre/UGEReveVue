package fr.vpl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.vpl.dto.RegisterRequest;
import fr.vpl.exception.UserAlreadyExistsException;
import fr.vpl.service.AuthService;
import fr.vpl.support.MessageSourceTestSupport;
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
class AuthControllerTest extends MessageSourceTestSupport {

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

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully!"));

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
                .andExpect(jsonPath("$.password").exists());
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
                .andExpect(jsonPath("$.username").value(message("validation.user.username.exists")));
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
                .andExpect(jsonPath("$.email").value(message("validation.user.email.exists")));
    }

}
