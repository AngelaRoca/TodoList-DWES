package com.Tarea_DWES_AngelaRocaBlanco.TodoList.controller;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TagDTOs.TagRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.TagDTOs.TagResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.TagService;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "CRUD de etiquetas para clasificar tareas")
@SecurityRequirement(name = "bearerAuth")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Listar todos los tags",
               description = "Devuelve todos los tags disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de tags",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TagResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {"id": 1, "name": "urgente"},
                                {"id": 2, "name": "casa"},
                                {"id": 3, "name": "trabajo"}
                            ]
                            """)))
    @GetMapping
    public ResponseEntity<List<TagResponse>> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }

    @Operation(summary = "Obtener tag por id")
    @ApiResponse(responseCode = "200", description = "Tag encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TagResponse.class),
                    examples = @ExampleObject("""
                            {"id": 1, "name": "urgente"}
                            """)))
    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getById(
            @Parameter(description = "ID del tag") @PathVariable Long id) {
        return ResponseEntity.ok(tagService.getById(id));
    }

    @Operation(summary = "Crear tag")
    @ApiResponse(responseCode = "201", description = "Tag creado correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TagResponse.class),
                    examples = @ExampleObject("""
                            {"id": 4, "name": "importante"}
                            """)))
    @PostMapping
    public ResponseEntity<TagResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo tag", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TagRequest.class),
                            examples = @ExampleObject("""
                                    {"name": "importante"}
                                    """)))
            @Valid @RequestBody TagRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(req));
    }

    @Operation(summary = "Actualizar tag")
    @ApiResponse(responseCode = "200", description = "Tag actualizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TagResponse.class),
                    examples = @ExampleObject("""
                            {"id": 1, "name": "muy urgente"}
                            """)))
    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> update(
            @Parameter(description = "ID del tag") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo nombre del tag", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TagRequest.class),
                            examples = @ExampleObject("""
                                    {"name": "muy urgente"}
                                    """)))
            @Valid @RequestBody TagRequest req) {
        return ResponseEntity.ok(tagService.update(id, req));
    }

    @Operation(summary = "Eliminar tag")
    @ApiResponse(responseCode = "204", description = "Tag eliminado correctamente",
            content = @Content(schema = @Schema(implementation = Void.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del tag") @PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}