package fr.vpl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.vpl.dto.LoginRequest;
import fr.vpl.dto.RegisterRequest;
import fr.vpl.entity.User;
import fr.vpl.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end registration tests using the real Spring context and H2 database.
 * They verify the collaboration between MVC, validation, service, password hashing,
 * exception handling, and persistence.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Authentication workflow integration tests")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("registration creates a user with a BCrypt password and default USER role")
    void register_shouldPersistUserWithEncodedPasswordAndDefaultRole() throws Exception {
        RegisterRequest request = new RegisterRequest("expert_java", "expert@vpl.fr", "Complex123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("expert_java"))
                .andExpect(jsonPath("$.email").value("expert@vpl.fr"));

        User savedUser = userRepository.findByUsername("expert_java").orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo("expert@vpl.fr");
        assertThat(savedUser.getPassword()).isNotEqualTo("Complex123!");
        assertThat(savedUser.getPassword()).startsWith("$2");
        assertThat(savedUser.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    @DisplayName("registration returns a localized conflict when username already exists")
    void register_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "john@vpl.fr", "Pass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details.username[0]").value("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("registration returns a localized conflict when email already exists")
    void register_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        RegisterRequest firstRequest = new RegisterRequest("john", "same@vpl.fr", "Pass123!");
        RegisterRequest duplicateEmailRequest = new RegisterRequest("jane", "same@vpl.fr", "Pass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateEmailRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.details.email[0]").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("login returns the authenticated user when credentials are valid")
    void login_shouldReturnAuthenticatedUser_whenCredentialsAreValid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("reviewer", "reviewer@vpl.fr", "Pass123!");
        LoginRequest loginRequest = new LoginRequest("reviewer@vpl.fr", "Pass123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("reviewer"))
                .andExpect(jsonPath("$.email").value("reviewer@vpl.fr"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("login returns a generic unauthorized response when password is invalid")
    void login_shouldReturnUnauthorized_whenPasswordIsInvalid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("reviewer", "reviewer@vpl.fr", "Pass123!");
        LoginRequest loginRequest = new LoginRequest("reviewer@vpl.fr", "Wrong123!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.details.auth[0]").value("INVALID_CREDENTIALS"));
    }

}
