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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/manager/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'GESTOR')")
@Tag(name = "Categorias - Gestor", description = "CRUD de categorias para usuarios con rol GESTOR o ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class ManagerCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar todas las categorias (gestor)",
               description = "Devuelve todas las categorias. Accesible por GESTOR y ADMIN.")
    @ApiResponse(responseCode = "200", description = "Listado de categorias",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {"id": 1, "title": "Personal"},
                                {"id": 2, "title": "Trabajo"}
                            ]
                            """)))
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @Operation(summary = "Obtener categoria por id (gestor)")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples = @ExampleObject("""
                            {"id": 1, "title": "Personal"}
                            """)))
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @Parameter(description = "ID de la categoria") @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @Operation(summary = "Crear categoria (gestor)",
               description = "Crea una nueva categoria. Accesible por GESTOR y ADMIN.")
    @ApiResponse(responseCode = "201", description = "Categoria creada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples = @ExampleObject("""
                            {"id": 4, "title": "Salud"}
                            """)))
    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la nueva categoria", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryRequest.class),
                            examples = @ExampleObject("""
                                    {"title": "Salud"}
                                    """)))
            @Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(req));
    }

    @Operation(summary = "Actualizar categoria (gestor)",
               description = "Modifica una categoria existente. Accesible por GESTOR y ADMIN.")
    @ApiResponse(responseCode = "200", description = "Categoria actualizada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples = @ExampleObject("""
                            {"id": 1, "title": "Personal actualizado"}
                            """)))
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @Parameter(description = "ID de la categoria") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo titulo de la categoria", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryRequest.class),
                            examples = @ExampleObject("""
                                    {"title": "Personal actualizado"}
                                    """)))
            @Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.ok(categoryService.update(id, req));
    }

    @Operation(summary = "Eliminar categoria (gestor)",
               description = "Elimina una categoria. Accesible por GESTOR y ADMIN.")
    @ApiResponse(responseCode = "204", description = "Categoria eliminada",
            content = @Content(schema = @Schema(implementation = Void.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la categoria") @PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
