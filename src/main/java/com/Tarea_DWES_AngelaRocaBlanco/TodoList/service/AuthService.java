package com.Tarea_DWES_AngelaRocaBlanco.TodoList.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.AuthDTOs.AuthResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.AuthDTOs.LoginRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.AuthDTOs.RegisterRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.User;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.UserRole;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.UserRepository;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("El email ya está registrado");

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullname(request.getFullname())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
