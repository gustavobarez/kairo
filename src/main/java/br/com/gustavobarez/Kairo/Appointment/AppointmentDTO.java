package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppointmentDTO(
        @NotBlank(message = "Title cannot be null or empty") @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters") String title,

        @NotBlank(message = "Description cannot be null or empty") @Size(max = 500, message = "Description cannot exceed 500 characters") String description,

        @NotNull(message = "Start time cannot be null") LocalDateTime startTime,

        @NotNull(message = "End time cannot be null") LocalDateTime endTime) {
    public AppointmentDTO(Appointment appointment) {
        this(
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime());
    }
}
