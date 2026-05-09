package fr.vpl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.vpl.dto.RegisterRequest;
import fr.vpl.entity.User;
import fr.vpl.repository.UserRepository;
import fr.vpl.support.MessageSourceTestSupport;
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
class AuthIntegrationTest extends MessageSourceTestSupport {

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
                .andExpect(content().string("User registered successfully!"));

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
                .andExpect(jsonPath("$.username").value(message("validation.user.username.exists")));
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
                .andExpect(jsonPath("$.email").value(message("validation.user.email.exists")));
    }

}
