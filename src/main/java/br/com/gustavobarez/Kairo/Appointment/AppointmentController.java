package br.com.gustavobarez.Kairo.Appointment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/v1/appointment")
@Tag(name = "Appointments", description = "Appointments management endpoints")
public class AppointmentController {

    AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    // @BasePath /api/v1
    // @Summary Create appointment
    // @Description Create a new appointment
    // @Tags Appointments
    // @Accept json
    // @Produce json
    // @Param request body AppointmentDTO true "Request body"
    // @Success 200 {object} ApiResponseDTO[AppointmentDTO]
    // @Failure 400 {object} ErrorResponse
    // @Failure 401 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /appointment/create [post]
    // @Security jwt_auth
    @PostMapping("/create")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Criar agendamento", description = "Função responsável por criar um novo agendamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso", content = {
                    @Content(schema = @Schema(implementation = ApiResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ApiResponseDTO<AppointmentDTO>> createAppointment(
            @Valid @RequestBody AppointmentDTO dto, HttpServletRequest request) {

        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userIdStr = userIdAttribute.toString();
        Long creatorId = Long.parseLong(userIdStr);

        var createAppointmentDTO = service.createAppointment(dto, creatorId);
        var response = new ApiResponseDTO<>(createAppointmentDTO, "create-appointment");
        return ResponseEntity.ok(response);
    }

    // @BasePath /api/v1
    // @Summary Invite to appointment
    // @Description Invite a user to an existing appointment
    // @Tags Appointments
    // @Accept json
    // @Produce json
    // @Param appointmentId path int true "Appointment ID"
    // @Param request body InviteAppointmentRequestDTO true "Request body"
    // @Success 200 {object} ApiResponseDTO[InviteAppointmentResponseDTO]
    // @Failure 400 {object} ErrorResponse
    // @Failure 401 {object} ErrorResponse
    // @Failure 404 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /appointment/{appointmentId}/participants [post]
    // @Security jwt_auth
    @PostMapping("/{appointmentId}/participants")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Convidar para agendamento", description = "Função responsável por convidar um usuário para um agendamento existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Convite enviado com sucesso", content = {
                    @Content(schema = @Schema(implementation = ApiResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ApiResponseDTO<InviteAppointmentResponseDTO>> inviteToAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody InviteAppointmentRequestDTO inviteAppointmentDTO,
            HttpServletRequest request) {

        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userIdStr = userIdAttribute.toString();
        Long creatorId = Long.parseLong(userIdStr);

        var appointment = service.inviteUserToAppointment(appointmentId, inviteAppointmentDTO, creatorId);
        var response = new ApiResponseDTO<>(appointment, "invite-to-appointment");
        return ResponseEntity.ok(response);
    }

    // @BasePath /api/v1
    // @Summary Delete appointment
    // @Description Delete an existing appointment
    // @Tags Appointments
    // @Param appointmentId path int true "Appointment ID"
    // @Success 204 "Agendamento deletado com sucesso"
    // @Failure 401 {object} ErrorResponse
    // @Failure 404 {object} ErrorResponse
    // @Failure 403 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /appointment/{appointmentId}/delete [delete]
    // @Security jwt_auth
    @DeleteMapping("/{appointmentId}/delete")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Deletar agendamento", description = "Função responsável por deletar um agendamento existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agendamento deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não autorizado a deletar este agendamento"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId,
            HttpServletRequest request) {

        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userIdStr = userIdAttribute.toString();
        Long userId = Long.parseLong(userIdStr);

        service.deleteAppointment(appointmentId, userId);
        return ResponseEntity.noContent().build();
    }

    // @BasePath /api/v1
    // @Summary Update appointment
    // @Description Update an existing appointment
    // @Tags Appointments
    // @Accept json
    // @Produce json
    // @Param appointmentId path int true "Appointment ID"
    // @Param request body UpdateAppointmentRequestDTO true "Request body"
    // @Success 200 {object} ApiResponseDTO[UpdateAppointmentResponseDTO]
    // @Failure 400 {object} ErrorResponse
    // @Failure 401 {object} ErrorResponse
    // @Failure 403 {object} ErrorResponse
    // @Failure 404 {object} ErrorResponse
    // @Failure 500 {object} ErrorResponse
    // @Router /appointment/{appointmentId}/update [patch]
    // @Security jwt_auth
    @PatchMapping("/{appointmentId}/update")
    @SecurityRequirement(name = "jwt_auth")
    @Operation(summary = "Atualizar agendamento", description = "Função responsável por atualizar um agendamento existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso", content = {
                    @Content(schema = @Schema(implementation = ApiResponseDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não autorizado a atualizar este agendamento"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ApiResponseDTO<UpdateAppointmentResponseDTO>> updateAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody UpdateAppointmentRequestDTO dto,
            HttpServletRequest request) {

        Object userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userIdStr = userIdAttribute.toString();
        Long userId = Long.parseLong(userIdStr);

        var appointmentDto = service.updateAppointment(appointmentId, dto, userId);
        var response = new ApiResponseDTO<>(appointmentDto, "update-appointment");
        return ResponseEntity.ok(response);
    }
}