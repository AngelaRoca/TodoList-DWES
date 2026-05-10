package com.Tarea_DWES_AngelaRocaBlanco.TodoList.controller;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.UserDTOs.UpdateProfileRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.UserDTOs.UserResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Perfil de Usuario", description = "Consulta y edición del perfil del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Ver mi perfil",
               description = "Devuelve los datos del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Perfil del usuario",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject("""
                            {
                                "id": 2,
                                "username": "angela",
                                "email": "angela@correo.com",
                                "fullname": "Ángela Roca Blanco",
                                "role": "USER"
                            }
                            """)))
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getProfile(user.getUsername()));
    }

    @Operation(summary = "Actualizar mi perfil",
               description = "Permite cambiar email, nombre completo y/o contraseña")
    @ApiResponse(responseCode = "200", description = "Perfil actualizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject("""
                            {
                                "id": 2,
                                "username": "angela",
                                "email": "nuevo@correo.com",
                                "fullname": "Ángela Roca Blanco",
                                "role": "USER"
                            }
                            """)))
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos a actualizar. Todos son opcionales.", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateProfileRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "email": "nuevo@correo.com",
                                        "fullname": "Ángela Roca Blanco",
                                        "password": "nuevaPassword123"
                                    }
                                    """)))
            @RequestBody UpdateProfileRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.updateProfile(user.getUsername(), req));
    }
}