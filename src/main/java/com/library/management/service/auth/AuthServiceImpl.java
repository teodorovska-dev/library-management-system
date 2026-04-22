package com.library.management.service.auth;

import com.library.management.dto.auth.AuthResponseDto;
import com.library.management.dto.auth.LoginRequestDto;
import com.library.management.dto.auth.RegisterRequestDto;
import com.library.management.entity.Role;
import com.library.management.entity.User;
import com.library.management.enums.RoleName;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.exception.InvalidRequestException;
import com.library.management.repository.RoleRepository;
import com.library.management.repository.UserRepository;
import com.library.management.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.security.admin-registration-key}")
    private String adminRegistrationKey;

    @Override
    public AuthResponseDto register(RegisterRequestDto requestDto) {
        String normalizedEmail = normalizeEmail(requestDto.getEmail());
        String normalizedUsername = normalizeUsername(requestDto.getUsername());
        String normalizedFirstName = normalizeText(requestDto.getFirstName());
        String normalizedLastName = normalizeText(requestDto.getLastName());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("User with this email already exists");
        }

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new DuplicateResourceException("User with this username already exists");
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(requestDto.getRole().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException("Invalid role value");
        }

        if (roleName == RoleName.ADMIN) {
            if (requestDto.getAdminAccessKey() == null || requestDto.getAdminAccessKey().isBlank()) {
                throw new InvalidRequestException("Admin access key is required for administrator registration");
            }

            if (!adminRegistrationKey.equals(requestDto.getAdminAccessKey().trim())) {
                throw new InvalidRequestException("Invalid admin access key");
            }
        }

        if (roleName == RoleName.USER && requestDto.getAdminAccessKey() != null && !requestDto.getAdminAccessKey().isBlank()) {
            throw new InvalidRequestException("Admin access key must not be provided for user registration");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new InvalidRequestException("Role not found"));

        String fullName = normalizedFirstName + " " + normalizedLastName;

        User user = User.builder()
                .firstName(normalizedFirstName)
                .lastName(normalizedLastName)
                .fullName(fullName)
                .username(normalizedUsername)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(requestDto.getPassword().trim()))
                .role(role)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getEmail())
                .password(savedUser.getPassword())
                .roles(savedUser.getRole().getName().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getName().name())
                .build();
    }

    @Override
    public AuthResponseDto login(LoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new InvalidRequestException("Invalid email or password"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizeUsername(String username) {
        return username.trim();
    }

    private String normalizeText(String value) {
        return value.trim();
    }
}