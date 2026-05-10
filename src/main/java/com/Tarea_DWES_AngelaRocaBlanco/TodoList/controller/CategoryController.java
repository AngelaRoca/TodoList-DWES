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
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.CategoryService;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias - Usuario", description = "Listado de categorias disponibles para cualquier usuario autenticado.")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Listar todas las categorias disponibles",
            description = "Devuelve todas las categorias disponibles. Accesible por cualquier usuario autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Listado de categorias",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {"id": 1, "title": "Personal"},
                                {"id": 2, "title": "Trabajo"},
                                {"id": 3, "title": "Estudio"}
                            ]
                            """)))
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @Operation(
            summary = "Obtener categoria por id",
            description = "Devuelve una categoria concreta. Accesible por cualquier usuario autenticado."
    )
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
}
