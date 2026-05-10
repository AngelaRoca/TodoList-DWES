package com.Tarea_DWES_AngelaRocaBlanco.TodoList.controller;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.UserDTOs.AdminUpdateUserRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.UserDTOs.UserResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.CategoryService;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.UserService;

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

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "Admin", description = "Operaciones exclusivas del administrador. Requiere rol ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;

    // ── USUARIOS ──────────────────────────────────────────────────────────

    @Operation(summary = "Listar todos los usuarios",
               description = "Devuelve la lista completa de usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {"id": 1, "username": "admin", "email": "admin@todolist.com", "fullname": "Administrador", "role": "ADMIN"},
                                {"id": 2, "username": "angela", "email": "angela@correo.com", "fullname": "Angela Roca", "role": "USER"}
                            ]
                            """)))
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obtener usuario por id")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject("""
                            {"id": 2, "username": "angela", "email": "angela@correo.com", "fullname": "Angela Roca", "role": "USER"}
                            """)))
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Actualizar usuario",
               description = "Modifica los datos de cualquier usuario")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class)))
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del usuario", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminUpdateUserRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "username": "angela2",
                                        "email": "angela2@correo.com",
                                        "fullname": "Angela Roca Blanco"
                                    }
                                    """)))
            @Valid @RequestBody AdminUpdateUserRequest req) {
        return ResponseEntity.ok(userService.updateUser(id, req));
    }

    @Operation(summary = "Eliminar usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente",
            content = @Content(schema = @Schema(implementation = Void.class)))
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Promocionar usuario a GESTOR",
               description = "Cambia el rol de un USER a GESTOR. Solo puede hacerlo un ADMIN.")
    @ApiResponse(responseCode = "200", description = "Usuario promocionado a GESTOR",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject("""
                            {"id": 2, "username": "angela", "email": "angela@correo.com", "fullname": "Angela Roca", "role": "GESTOR"}
                            """)))
    @ApiResponse(responseCode = "409", description = "El usuario no tiene rol USER",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {"status": 409, "error": "Conflict", "message": "Solo se pueden promocionar usuarios con rol USER"}
                            """)))
    @PatchMapping("/users/{id}/promote")
    public ResponseEntity<UserResponse> promote(
            @Parameter(description = "ID del usuario a promocionar") @PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToGestor(id));
    }

    @Operation(summary = "Degradar GESTOR a USER",
               description = "Cambia el rol de un GESTOR a USER. Solo puede hacerlo un ADMIN.")
    @ApiResponse(responseCode = "200", description = "Gestor degradado a USER",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject("""
                            {"id": 2, "username": "angela", "email": "angela@correo.com", "fullname": "Angela Roca", "role": "USER"}
                            """)))
    @ApiResponse(responseCode = "409", description = "El usuario no tiene rol GESTOR",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject("""
                            {"status": 409, "error": "Conflict", "message": "Solo se pueden degradar usuarios con rol GESTOR"}
                            """)))
    @PatchMapping("/users/{id}/demote")
    public ResponseEntity<UserResponse> demote(
            @Parameter(description = "ID del gestor a degradar") @PathVariable Long id) {
        return ResponseEntity.ok(userService.demoteToUser(id));
    }

    // ── CATEGORIAS ────────────────────────────────────────────────────────

    @Operation(summary = "Listar todas las categorias (admin)",
               description = "Devuelve todas las categorias. Solo ADMIN.")
    @ApiResponse(responseCode = "200", description = "Listado de categorias",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class)),
                    examples = @ExampleObject("""
                            [
                                {"id": 1, "title": "Personal"},
                                {"id": 2, "title": "Trabajo"}
                            ]
                            """)))
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @Operation(summary = "Crear categoria (admin)",
               description = "Crea una nueva categoria. Solo ADMIN.")
    @ApiResponse(responseCode = "201", description = "Categoria creada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples = @ExampleObject("""
                            {"id": 4, "title": "Salud"}
                            """)))
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(
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

    @Operation(summary = "Actualizar categoria (admin)",
               description = "Modifica una categoria existente. Solo ADMIN.")
    @ApiResponse(responseCode = "200", description = "Categoria actualizada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class),
                    examples = @ExampleObject("""
                            {"id": 1, "title": "Personal actualizado"}
                            """)))
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
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

    @Operation(summary = "Eliminar categoria (admin)",
               description = "Elimina una categoria. Solo ADMIN.")
    @ApiResponse(responseCode = "204", description = "Categoria eliminada",
            content = @Content(schema = @Schema(implementation = Void.class)))
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID de la categoria") @PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}