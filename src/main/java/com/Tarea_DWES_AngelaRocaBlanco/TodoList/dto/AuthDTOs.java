package com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDTOs {

    @Data
    public static class RegisterRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        @Email @NotBlank private String email;
        @NotBlank private String fullname;
    }

    @Data
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String username;
        private String role;

        public AuthResponse(String token, String username, String role) {
            this.token = token;
            this.username = username;
            this.role = role;
        }
    }
}
