package br.com.gustavobarez.Kairo.Appointment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponseDTO<CreateAppointmentDTO>> createAppointment(
            @Valid @RequestBody CreateAppointmentDTO dto, HttpServletRequest request) {

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

}
