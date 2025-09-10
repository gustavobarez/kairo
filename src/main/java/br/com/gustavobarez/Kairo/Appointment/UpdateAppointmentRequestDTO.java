package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;

public record UpdateAppointmentRequestDTO(String title, String description, LocalDateTime startTime,
        LocalDateTime endTime) {

}
