package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;

public record CreateAppointmentDTO(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
    
}
