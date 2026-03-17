package com.essence.ahorratank.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> postLogin(
            @RequestBody @Valid LoginRequest user
    ) {
        return ResponseEntity.ok(authService.login(user));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);

        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()   // 👈 importante
                        .path("/api/users/{id}")
                        .buildAndExpand(response.id())
                        .toUri()
        ).body(response);
    }
}