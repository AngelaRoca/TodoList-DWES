package com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto;



import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class UserDTOs {

    @Data
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String fullname;
        private UserRole role;
    }

    @Data
    public static class UpdateProfileRequest {
        @Email private String email;
        private String fullname;
        private String password;
    }

    @Data
    public static class AdminUpdateUserRequest {
        @NotBlank private String username;
        @Email @NotBlank private String email;
        @NotBlank private String fullname;
    }
}
