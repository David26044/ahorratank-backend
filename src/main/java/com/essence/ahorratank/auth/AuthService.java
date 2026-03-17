package com.essence.ahorratank.auth;

import com.essence.ahorratank.config.JwtService;
import com.essence.ahorratank.role.RoleEntity;
import com.essence.ahorratank.role.RoleRepository;
import com.essence.ahorratank.user.UserEntity;
import com.essence.ahorratank.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest userRequest) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.email(),
                        userRequest.password()
                )
        );

        UserEntity user = this.userRepository.findByEmail(userRequest.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwtToken = this.jwtService.generateToken(user);
        return new LoginResponse(jwtToken);
    }

    public RegisterResponse register(RegisterRequest userRequest) {

        if (this.userRepository.existsByEmail(userRequest.email())) {
            throw new RuntimeException("Ya existe un usuario con ese correo");
        }

        RoleEntity userRole = this.roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));

        UserEntity user = UserEntity.builder()
                .firstName(userRequest.firstName())
                .lastName(userRequest.lastName())
                .email(userRequest.email())
                .password(this.passwordEncoder.encode(userRequest.password()))
                .enabled(true)
                .role(userRole)
                .build();

        UserEntity savedUser = this.userRepository.save(user);

        String jwtToken = this.jwtService.generateToken(savedUser);
        return new RegisterResponse(savedUser.getId(), jwtToken);
    }
}