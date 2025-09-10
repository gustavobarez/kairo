package br.com.gustavobarez.Kairo.Appointment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavobarez.Kairo.util.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/appointment")
public class AppointmentController {

    AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping("/create")
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

    @PostMapping("/{appointmentId}/participants")
    public ResponseEntity<ApiResponseDTO<InviteAppointmentResponseDTO>> inviteToAppointment(
            @PathVariable Long appointmentId,
            @RequestBody InviteAppointmentRequestDTO inviteAppointmentDTO, HttpServletRequest request) {
        var userIdAttribute = request.getAttribute("user_id");
        if (userIdAttribute == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userIdStr = userIdAttribute.toString();
        Long creatorId = Long.parseLong(userIdStr);
        var appointment = service.inviteUserToAppointment(appointmentId, inviteAppointmentDTO, creatorId);
        var response = new ApiResponseDTO<>(appointment, "invite-to-appointment");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{appointmentId}/delete")
    public void deleteAppointment(@PathVariable Long appointmentId, HttpServletRequest request) {
        service.deleteAppointment(appointmentId);
    }

}
