package com.Tarea_DWES_AngelaRocaBlanco.TodoList.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TaskDTOs.DashboardResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TaskDTOs.TaskRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TaskDTOs.TaskResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Priority;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.TaskService;



@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "CRUD y búsquedas de tareas del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Listar todas mis tareas",
            description = "Devuelve todas las tareas del usuario autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Listado de tareas",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {
                                    "id": 1,
                                    "title": "Comprar alimentos",
                                    "description": "Hacer la lista del supermercado",
                                    "completed": false,
                                    "createdAt": "2025-01-13T10:00:00",
                                    "deadline": "2025-01-20",
                                    "priority": "HIGH",
                                    "authorUsername": "angela",
                                    "categoryTitle": "Personal",
                                    "tagNames": ["urgente", "casa"]
                                }
                            ]
                            """)))
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getMyTasks(user.getUsername()));
    }

    @Operation(
            summary = "Obtener tarea por id",
            description = "Devuelve una tarea concreta del usuario autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Tarea encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskResponse.class),
                    examples = @ExampleObject("""
                            {
                                "id": 1,
                                "title": "Comprar alimentos",
                                "description": "Hacer la lista del supermercado",
                                "completed": false,
                                "createdAt": "2025-01-13T10:00:00",
                                "deadline": "2025-01-20",
                                "priority": "HIGH",
                                "authorUsername": "angela",
                                "categoryTitle": "Personal",
                                "tagNames": ["urgente", "casa"]
                            }
                            """)))
    @ApiResponse(responseCode = "404", description = "Tarea no encontrada",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {
                                "status": 404,
                                "error": "Not Found",
                                "message": "Tarea con id 99 no encontrada"
                            }
                            """)))
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(
            @Parameter(description = "ID de la tarea") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getById(id, user.getUsername()));
    }

    @Operation(
            summary = "Crear nueva tarea",
            description = "Crea una nueva tarea asociada al usuario autenticado"
    )
    @ApiResponse(responseCode = "201", description = "Tarea creada correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskResponse.class),
                    examples = @ExampleObject("""
                            {
                                "id": 2,
                                "title": "Estudiar Spring Boot",
                                "description": "Repasar seguridad con JWT",
                                "completed": false,
                                "createdAt": "2025-01-13T10:00:00",
                                "deadline": "2025-06-01",
                                "priority": "MEDIUM",
                                "authorUsername": "angela",
                                "categoryTitle": "Estudio",
                                "tagNames": ["java", "backend"]
                            }
                            """)))
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la nueva tarea", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "title": "Estudiar Spring Boot",
                                        "description": "Repasar seguridad con JWT",
                                        "completed": false,
                                        "deadline": "2025-06-01",
                                        "priority": "MEDIUM",
                                        "categoryId": 1,
                                        "tagIds": [1, 2]
                                    }
                                    """)))
            @Valid @RequestBody TaskRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(req, user.getUsername()));
    }

    @Operation(
            summary = "Actualizar tarea existente",
            description = "Modifica una tarea del usuario autenticado dado su ID"
    )
    @ApiResponse(responseCode = "200", description = "Tarea actualizada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskResponse.class),
                    examples = @ExampleObject("""
                            {
                                "id": 1,
                                "title": "Comprar alimentos editado",
                                "description": "Lista actualizada",
                                "completed": true,
                                "createdAt": "2025-01-13T10:00:00",
                                "deadline": "2025-02-01",
                                "priority": "LOW",
                                "authorUsername": "angela",
                                "categoryTitle": "Personal",
                                "tagNames": ["casa"]
                            }
                            """)))
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @Parameter(description = "ID de la tarea") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la tarea", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "title": "Comprar alimentos editado",
                                        "description": "Lista actualizada",
                                        "completed": true,
                                        "deadline": "2025-02-01",
                                        "priority": "LOW",
                                        "categoryId": 1,
                                        "tagIds": [1]
                                    }
                                    """)))
            @Valid @RequestBody TaskRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.update(id, req, user.getUsername()));
    }

    @Operation(
            summary = "Eliminar tarea",
            description = "Elimina una tarea del usuario autenticado dado su ID"
    )
    @ApiResponse(responseCode = "204", description = "Tarea eliminada correctamente",
            content = @Content(schema = @Schema(implementation = Void.class)))
    @ApiResponse(responseCode = "403", description = "No tienes permiso para eliminar esta tarea",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {
                                "status": 403,
                                "error": "Forbidden",
                                "message": "No tienes permiso para acceder a esta tarea"
                            }
                            """)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la tarea") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        taskService.delete(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Filtrar por estado completado",
            description = "Devuelve las tareas completadas o pendientes según el parámetro"
    )
    @ApiResponse(responseCode = "200", description = "Listado filtrado",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {
                                    "id": 3,
                                    "title": "Tarea completada",
                                    "completed": true,
                                    "priority": "LOW",
                                    "authorUsername": "angela",
                                    "tagNames": []
                                }
                            ]
                            """)))
    @GetMapping("/filter/completed")
    public ResponseEntity<List<TaskResponse>> filterByCompleted(
            @Parameter(description = "true = completadas, false = pendientes") @RequestParam boolean completed,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getByCompleted(completed, user.getUsername()));
    }

    @Operation(
            summary = "Buscar por título",
            description = "Devuelve las tareas cuyo título contenga el texto indicado"
    )
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))
    @GetMapping("/search/title")
    public ResponseEntity<List<TaskResponse>> searchByTitle(
            @Parameter(description = "Texto a buscar en el título") @RequestParam String q,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.searchByTitle(q, user.getUsername()));
    }

    @Operation(
            summary = "Buscar por descripción",
            description = "Devuelve las tareas cuya descripción contenga el texto indicado"
    )
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))
    @GetMapping("/search/description")
    public ResponseEntity<List<TaskResponse>> searchByDescription(
            @Parameter(description = "Texto a buscar en la descripción") @RequestParam String q,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.searchByDescription(q, user.getUsername()));
    }

    @Operation(
            summary = "Filtrar por categoría",
            description = "Devuelve las tareas que pertenecen a la categoría indicada"
    )
    @ApiResponse(responseCode = "200", description = "Tareas de la categoría",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))
    @GetMapping("/filter/category/{categoryId}")
    public ResponseEntity<List<TaskResponse>> filterByCategory(
            @Parameter(description = "ID de la categoría") @PathVariable Long categoryId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getByCategory(categoryId, user.getUsername()));
    }

    @Operation(
            summary = "Filtrar por tags",
            description = "Devuelve las tareas que tengan al menos uno de los tags indicados"
    )
    @ApiResponse(responseCode = "200", description = "Tareas con los tags indicados",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))
    @GetMapping("/filter/tags")
    public ResponseEntity<List<TaskResponse>> filterByTags(
            @Parameter(description = "IDs de tags separados por coma. Ejemplo: 1,2,3") @RequestParam List<Long> tagIds,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getByTags(tagIds, user.getUsername()));
    }

    @Operation(
            summary = "Filtrar por prioridad",
            description = "Devuelve las tareas con la prioridad indicada: LOW, MEDIUM o HIGH"
    )
    @ApiResponse(responseCode = "200", description = "Tareas con la prioridad indicada",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {
                                    "id": 5,
                                    "title": "Tarea urgente",
                                    "priority": "HIGH",
                                    "completed": false,
                                    "authorUsername": "angela",
                                    "tagNames": ["urgente"]
                                }
                            ]
                            """)))
    @GetMapping("/filter/priority")
    public ResponseEntity<List<TaskResponse>> filterByPriority(
            @Parameter(description = "Nivel de prioridad: LOW, MEDIUM o HIGH") @RequestParam Priority priority,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getByPriority(priority, user.getUsername()));
    }

    @Operation(
            summary = "Filtrar por deadline anterior a una fecha",
            description = "Devuelve las tareas con fecha límite anterior a la indicada. Útil para ver tareas vencidas."
    )
    @ApiResponse(responseCode = "200", description = "Tareas con deadline anterior a la fecha",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))
    @GetMapping("/filter/deadline")
    public ResponseEntity<List<TaskResponse>> filterByDeadlineBefore(
            @Parameter(description = "Fecha en formato yyyy-MM-dd. Ejemplo: 2025-06-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getByDeadlineBefore(date, user.getUsername()));
    }

    @Operation(
            summary = "Asignar tag a una tarea",
            description = "Añade un tag existente a una tarea del usuario autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Tag asignado correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskResponse.class)))
    @PostMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponse> addTag(
            @Parameter(description = "ID de la tarea") @PathVariable Long taskId,
            @Parameter(description = "ID del tag") @PathVariable Long tagId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.addTagToTask(taskId, tagId, user.getUsername()));
    }

    @Operation(
            summary = "Eliminar tag de una tarea",
            description = "Elimina un tag de una tarea del usuario autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Tag eliminado correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskResponse.class)))
    @DeleteMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<TaskResponse> removeTag(
            @Parameter(description = "ID de la tarea") @PathVariable Long taskId,
            @Parameter(description = "ID del tag") @PathVariable Long tagId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.removeTagFromTask(taskId, tagId, user.getUsername()));
    }

    @Operation(
            summary = "Dashboard del usuario",
            description = "Devuelve estadísticas: total de tareas, completadas, pendientes, vencidas y las 5 más recientes"
    )
    @ApiResponse(responseCode = "200", description = "Estadísticas del usuario",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DashboardResponse.class),
                    examples = @ExampleObject("""
                            {
                                "totalTasks": 10,
                                "completedTasks": 4,
                                "pendingTasks": 6,
                                "overdueTasks": 2,
                                "recentTasks": [
                                    {
                                        "id": 10,
                                        "title": "Última tarea creada",
                                        "completed": false,
                                        "priority": "HIGH",
                                        "authorUsername": "angela",
                                        "tagNames": []
                                    }
                                ]
                            }
                            """)))
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(taskService.getDashboard(user.getUsername()));
    }
}