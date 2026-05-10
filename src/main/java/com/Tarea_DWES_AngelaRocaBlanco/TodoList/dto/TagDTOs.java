package com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class TagDTOs {

    @Data
    public static class TagRequest {
        @NotBlank private String name;
    }

    @Data
    public static class TagResponse {
        private Long id;
        private String name;
    }
}