package com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Priority;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class TaskDTOs {

    @Data
    public static class TaskRequest {
        @NotBlank private String title;
        private String description;
        private boolean completed;
        private LocalDate deadline;
        private Priority priority;
        private Long categoryId;
        private List<Long> tagIds;
    }

    @Data
    public static class TaskResponse {
        private Long id;
        private String title;
        private String description;
        private boolean completed;
        private LocalDateTime createdAt;
        private LocalDate deadline;
        private Priority priority;
        private String authorUsername;
        private String categoryTitle;
        private List<String> tagNames;
    }

    @Data
    public static class DashboardResponse {
        private long totalTasks;
        private long completedTasks;
        private long pendingTasks;
        private long overdueTasks;
        private List<TaskResponse> recentTasks;
    }
}
