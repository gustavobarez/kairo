package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;

public record UpdateAppointmentResponseDTO(Long id, String title, String description, LocalDateTime startTime,
        LocalDateTime endTime) {

}
