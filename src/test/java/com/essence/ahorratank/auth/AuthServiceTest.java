package com.essence.ahorratank.auth;

import com.essence.ahorratank.config.JwtService;
import com.essence.ahorratank.role.RoleEntity;
import com.essence.ahorratank.role.RoleRepository;
import com.essence.ahorratank.user.UserEntity;
import com.essence.ahorratank.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — pruebas unitarias")
class AuthServiceTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock UserRepository        userRepository;
    @Mock RoleRepository        roleRepository;
    @Mock PasswordEncoder       passwordEncoder;
    @Mock JwtService            jwtService;

    @InjectMocks AuthService authService;

    // ── fixtures ─────────────────────────────────────────────
    private RoleEntity  roleUser;
    private UserEntity  userEntity;

    @BeforeEach
    void setUp() {
        roleUser = new RoleEntity();
        roleUser.setId(1L);
        roleUser.setName("USER");

        userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@test.com")
                .password("encodedPassword")
                .enabled(true)
                .role(roleUser)
                .build();
    }

    // ── LOGIN ─────────────────────────────────────────────────

    @Test
    @DisplayName("login exitoso → devuelve token JWT")
    void login_successful_returnsToken() {
        // Arrange
        LoginRequest request = new LoginRequest("juan@test.com", "password123");
        when(userRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(userEntity));
        when(jwtService.generateToken(userEntity)).thenReturn("jwt.token.mock");

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt.token.mock");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userEntity);
    }

    @Test
    @DisplayName("login con email inexistente → RuntimeException")
    void login_userNotFound_throwsException() {
        // Arrange
        LoginRequest request = new LoginRequest("noexiste@test.com", "password123");
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    @DisplayName("login con credenciales incorrectas → BadCredentialsException")
    void login_badCredentials_throwsException() {
        // Arrange
        LoginRequest request = new LoginRequest("juan@test.com", "wrongpassword");
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        // Verifica que nunca llegó a buscar el usuario
        verify(userRepository, never()).findByEmail(any());
    }

    // ── REGISTER ──────────────────────────────────────────────

    @Test
    @DisplayName("register exitoso → devuelve id y token")
    void register_successful_returnsIdAndToken() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "Juan", "Pérez", "juan@test.com", "password123"
        );
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(roleUser));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(jwtService.generateToken(userEntity)).thenReturn("jwt.token.mock");

        // Act
        RegisterResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.token()).isEqualTo("jwt.token.mock");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("register con email duplicado → RuntimeException")
    void register_duplicateEmail_throwsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "Juan", "Pérez", "juan@test.com", "password123"
        );
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe");

        // Verifica que nunca intentó guardar
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register con rol USER no encontrado → RuntimeException")
    void register_roleNotFound_throwsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "Juan", "Pérez", "juan@test.com", "password123"
        );
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rol");

        verify(userRepository, never()).save(any());
    }
}