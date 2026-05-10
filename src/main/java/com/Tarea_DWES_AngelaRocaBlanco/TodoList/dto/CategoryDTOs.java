package com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class CategoryDTOs {

    @Data
    public static class CategoryRequest {
        @NotBlank private String title;
    }

    @Data
    public static class CategoryResponse {
        private Long id;
        private String title;
    }
}
