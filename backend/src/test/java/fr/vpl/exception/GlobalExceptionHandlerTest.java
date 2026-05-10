package fr.vpl.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.vpl.dto.RegisterRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for API error handling.
 * They validate that exceptions and validation failures become stable JSON payloads.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("GlobalExceptionHandler integration tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("duplicate username is returned as a localized 409 response")
    void handleUserAlreadyExists_shouldReturnConflictWithLocalizedMessage() throws Exception {
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
    @DisplayName("invalid request body is returned as grouped field errors")
    void handleValidation_shouldReturnGroupedFieldErrors() throws Exception {
        RegisterRequest request = new RegisterRequest("", "invalid-email", "weak");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.username").isArray())
                .andExpect(jsonPath("$.details.email").isArray())
                .andExpect(jsonPath("$.details.password").isArray());
    }

}
