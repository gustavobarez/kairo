package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;

public record AppointmentDTO(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
    public AppointmentDTO(Appointment appointment) {
        this(
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime());
    }
}
