package br.com.gustavobarez.Kairo.User;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.gustavobarez.Kairo.Appointment.AppointmentDTO;
import br.com.gustavobarez.Kairo.util.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User management endpoints")
public class UserController {

    UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // @BasePath /api/v1
    // @Summary Create user
    // @Description Create a new user account
    // @Tags User
    // @Accept json
    // @Produce json
    // @Param request body CreateUserRequestDTO true "Request body"
    // @Success 200 {object} ApiResponseDTO[CreateUserResponseDTO]
    // @Failure 409 {object} ErrorResponse
    // @Failure 400 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /user [post]
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Função responsável por criar uma nova conta de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso", content = {
                    @Content(schema = @Schema(implementation = ApiResponseDTO.class))
            }),
            @ApiResponse(responseCode = "409", description = "Email já está em uso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ApiResponseDTO<CreateUserResponseDTO>> createUser(
            @Valid @RequestBody CreateUserRequestDTO dto) {
        var request = service.createUser(dto);
        var response = new ApiResponseDTO<>(request, "create-user");
        return ResponseEntity.ok(response);
    }

    // @BasePath /api/v1
    // @Summary Get user appointments
    // @Description Get all appointments for a specific user
    // @Tags User
    // @Produce json
    // @Param userId path int true "User ID"
    // @Success 200 {object} ApiResponseDTO[map[string][]AppointmentDTO]
    // @Failure 401 {object} ErrorResponse
    // @Failure 404 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /user/{userId}/appointments [get]
    // @Security jwt_auth
    @GetMapping("/{userId}/appointments")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Listar agendamentos do usuário", description = "Função responsável por buscar todos os agendamentos de um usuário específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos encontrada", content = {
                    @Content(schema = @Schema(implementation = ApiResponseDTO.class))
            }),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ApiResponseDTO<Map<String, List<AppointmentDTO>>>> getAppointments(
            @PathVariable Long userId) {
        var appointments = service.listAllUserAppointments(userId);
        var response = new ApiResponseDTO<>(appointments, "list-all-user-appointments");
        return ResponseEntity.ok(response);
    }

    // @BasePath /api/v1
    // @Summary Delete user
    // @Description Delete the authenticated user account
    // @Tags User
    // @Success 204 "Usuário deletado com sucesso"
    // @Failure 401 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /user [delete]
    // @Security jwt_auth
    @DeleteMapping
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Deletar usuário", description = "Função responsável por deletar a conta do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> deleteUser(HttpServletRequest request) {
        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userIdStr = userIdAttribute.toString();
        Long userId = Long.parseLong(userIdStr);

        service.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // @BasePath /api/v1
    // @Summary Update user
    // @Description Update the authenticated user's information
    // @Tags User
    // @Accept json
    // @Produce json
    // @Param request body UpdateUserRequestDTO true "Request body"
    // @Success 200 {object} ApiResponseDTO[UpdateUserResponseDTO]
    // @Failure 400 {object} ErrorResponse
    // @Failure 401 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /user [patch]
    // @Security jwt_auth
    @PatchMapping
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Atualizar usuário", description = "Função responsável por atualizar as informações do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = {
                    @Content(schema = @Schema(implementation = ApiResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ApiResponseDTO<UpdateUserResponseDTO>> updateUser(
            @Valid @RequestBody UpdateUserRequestDTO dto,
            HttpServletRequest request) {
        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userIdStr = userIdAttribute.toString();
        Long userId = Long.parseLong(userIdStr);

        var user = service.update(dto, userId);
        var response = new ApiResponseDTO<>(user, "update-user");
        return ResponseEntity.ok(response);
    }
}