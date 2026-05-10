package com.Tarea_DWES_AngelaRocaBlanco.TodoList.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TaskDTOs.DashboardResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TaskDTOs.TaskRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TaskDTOs.TaskResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Category;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Priority;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Tag;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Task;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.User;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.CategoryRepository;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.TagRepository;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    public List<TaskResponse> getMyTasks(String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthor(user).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse getById(Long id, String username) {
        return toResponse(findAndCheckOwner(id, username));
    }

    @Transactional
    public TaskResponse create(TaskRequest req, String username) {
        User user = userService.findByUsername(username);

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .completed(req.isCompleted())
                .deadline(req.getDeadline())
                .priority(req.getPriority() != null ? req.getPriority() : Priority.MEDIUM)
                .author(user)
                .build();

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            task.setCategory(cat);
        }

        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            List<Tag> tags = req.getTagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new IllegalArgumentException("Tag no encontrado: " + tagId)))
                    .collect(Collectors.toList());
            task.setTags(tags);
        }

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest req, String username) {
        Task task = findAndCheckOwner(id, username);
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setCompleted(req.isCompleted());
        task.setDeadline(req.getDeadline());
        if (req.getPriority() != null) task.setPriority(req.getPriority());

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            task.setCategory(cat);
        } else {
            task.setCategory(null);
        }

        if (req.getTagIds() != null) {
            List<Tag> tags = req.getTagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new IllegalArgumentException("Tag no encontrado: " + tagId)))
                    .collect(Collectors.toList());
            task.setTags(tags);
        }

        return toResponse(taskRepository.save(task));
    }

    public void delete(Long id, String username) {
        taskRepository.delete(findAndCheckOwner(id, username));
    }

    public List<TaskResponse> getByCompleted(boolean completed, String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthorAndCompleted(user, completed)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> searchByTitle(String title, String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthorAndTitleContainingIgnoreCase(user, title)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> searchByDescription(String description, String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthorAndDescriptionContainingIgnoreCase(user, description)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getByCategory(Long categoryId, String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthorAndCategory_Id(user, categoryId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getByTags(List<Long> tagIds, String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthorAndTagsIn(user, tagIds)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getByPriority(Priority priority, String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthorAndPriority(user, priority)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getByDeadlineBefore(LocalDate date, String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByAuthorAndDeadlineBefore(user, date)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse addTagToTask(Long taskId, Long tagId, String username) {
        Task task = findAndCheckOwner(taskId, username);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag no encontrado"));
        if (!task.getTags().contains(tag)) task.getTags().add(tag);
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse removeTagFromTask(Long taskId, Long tagId, String username) {
        Task task = findAndCheckOwner(taskId, username);
        task.getTags().removeIf(t -> t.getId().equals(tagId));
        return toResponse(taskRepository.save(task));
    }

    public DashboardResponse getDashboard(String username) {
        User user = userService.findByUsername(username);
        long total = taskRepository.countByAuthor(user);
        long completed = taskRepository.countByAuthorAndCompleted(user, true);
        long overdue = taskRepository.findOverdueTasks(user, LocalDate.now()).size();

        List<TaskResponse> recent = taskRepository.findByAuthor(user)
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(this::toResponse)
                .collect(Collectors.toList());

        DashboardResponse dash = new DashboardResponse();
        dash.setTotalTasks(total);
        dash.setCompletedTasks(completed);
        dash.setPendingTasks(total - completed);
        dash.setOverdueTasks(overdue);
        dash.setRecentTasks(recent);
        return dash;
    }

    private Task findAndCheckOwner(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarea con id " + id + " no encontrada"));
        if (!task.getAuthor().getUsername().equals(username))
            throw new SecurityException("No tienes permiso para acceder a esta tarea");
        return task;
    }

    public TaskResponse toResponse(Task task) {
        TaskResponse r = new TaskResponse();
        r.setId(task.getId());
        r.setTitle(task.getTitle());
        r.setDescription(task.getDescription());
        r.setCompleted(task.isCompleted());
        r.setCreatedAt(task.getCreatedAt());
        r.setDeadline(task.getDeadline());
        r.setPriority(task.getPriority());
        r.setAuthorUsername(task.getAuthor().getUsername());
        r.setCategoryTitle(task.getCategory() != null ? task.getCategory().getTitle() : null);
        r.setTagNames(task.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        return r;
    }
}
