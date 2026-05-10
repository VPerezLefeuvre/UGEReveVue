package fr.vpl.controller;

import fr.vpl.dto.RegisterRequest;
import fr.vpl.dto.RegisterResponse;
import fr.vpl.entity.User;
import fr.vpl.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        User user = authService.register(request);
        var response = new RegisterResponse(user.getId(), user.getUsername(), user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
