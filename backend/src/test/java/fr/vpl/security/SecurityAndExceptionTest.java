package fr.vpl.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for security filters and request parsing failures.
 * Validation payload checks stay here because they prove the full MVC stack
 * still returns client-safe errors when security filters are enabled.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security and request error integration tests")
class SecurityAndExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("register returns 400 with field errors when JSON fields are invalid")
    void register_shouldReturnBadRequestWithFieldErrors_whenFieldsAreInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"\", \"email\": \"invalid-email\", \"password\": \"123\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @DisplayName("register returns 400 when JSON syntax is malformed")
    void register_shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
        String malformedJson = "{ \"username\": \"admin\", ";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("non API endpoints require authentication")
    void protectedEndpoint_shouldReturnForbidden_whenRequestIsAnonymous() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("non-authentication API endpoints require authentication")
    void protectedApiEndpoint_shouldReturnForbidden_whenRequestIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
