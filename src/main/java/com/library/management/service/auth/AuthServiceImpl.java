package com.library.management.service.auth;

import com.library.management.dto.auth.*;
import com.library.management.entity.Role;
import com.library.management.entity.User;
import com.library.management.enums.RoleName;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.exception.InvalidRequestException;
import com.library.management.repository.RoleRepository;
import com.library.management.repository.UserRepository;
import com.library.management.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
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

    @Override
    public AuthResponseDto register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("User with this email already exists");
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(requestDto.getRole().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException("Invalid role value");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new InvalidRequestException("Role not found"));

        User user = User.builder()
                .fullName(requestDto.getFullName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
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
}