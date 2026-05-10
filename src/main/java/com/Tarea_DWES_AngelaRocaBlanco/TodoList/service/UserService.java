package com.Tarea_DWES_AngelaRocaBlanco.TodoList.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.UserDTOs.*;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.User;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.UserRole;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public UserResponse updateUser(Long id, AdminUpdateUserRequest req) {
        User user = findOrThrow(id);
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setFullname(req.getFullname());
        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.delete(findOrThrow(id));
    }

    public UserResponse promoteToGestor(Long id) {
        User user = findOrThrow(id);
        if (user.getRole() != UserRole.USER)
            throw new IllegalStateException("Solo se pueden promocionar usuarios con rol USER");
        user.setRole(UserRole.GESTOR);
        return toResponse(userRepository.save(user));
    }

    public UserResponse demoteToUser(Long id) {
        User user = findOrThrow(id);
        if (user.getRole() != UserRole.GESTOR)
            throw new IllegalStateException("Solo se pueden degradar usuarios con rol GESTOR");
        user.setRole(UserRole.USER);
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateProfile(String username, UpdateProfileRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getFullname() != null) user.setFullname(req.getFullname());
        if (req.getPassword() != null && !req.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        return toResponse(userRepository.save(user));
    }

    public UserResponse getProfile(String username) {
        return toResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado")));
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con id " + id + " no encontrado"));
    }

    public UserResponse toResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setEmail(user.getEmail());
        r.setFullname(user.getFullname());
        r.setRole(user.getRole());
        return r;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
    }
}
