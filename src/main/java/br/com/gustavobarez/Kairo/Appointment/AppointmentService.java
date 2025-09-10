package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.gustavobarez.Kairo.User.User;
import br.com.gustavobarez.Kairo.User.UserRepository;
import br.com.gustavobarez.Kairo.User.UserResponseDTO;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AppointmentService {

    AppointmentRepository repository;

    UserRepository userRepository;

    public AppointmentService(AppointmentRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public AppointmentDTO createAppointment(AppointmentDTO dto, Long creatorId) {
        var user = userRepository.findById(creatorId);

        Appointment appointment = Appointment.builder()
                .creator(user.get())
                .title(dto.title())
                .description(dto.description())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(appointment);

        return dto;
    }

    public InviteAppointmentResponseDTO inviteUserToAppointment(Long appointmentId,
            InviteAppointmentRequestDTO inviteAppointmentDTO, Long creatorId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }

        if (inviteAppointmentDTO.usersId().isEmpty() || inviteAppointmentDTO.usersId() == null) {
            throw new IllegalArgumentException("Users ID cannot be null");
        }

        var appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with ID: " + appointmentId));
        var users = userRepository.findAllById(inviteAppointmentDTO.usersId());

        if (appointment.getCreator().getId() != creatorId) {
            throw new IllegalArgumentException("User ID needs to match with Appointment Creator ID");
        }

        for (User user : users) {
            appointment.getParticipants().add(user);
        }

        repository.save(appointment);

        UserResponseDTO creatorDTO = UserResponseDTO.builder()
                .id(appointment.getCreator().getId())
                .username(appointment.getCreator().getUsername())
                .build();

        List<UserResponseDTO> participantsDTO = appointment.getParticipants().stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .build())
                .collect(Collectors.toList());

        InviteAppointmentResponseDTO response = InviteAppointmentResponseDTO.builder()
                .creator(creatorDTO)
                .title(appointment.getTitle())
                .description(appointment.getDescription())
                .starTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .participants(participantsDTO)
                .build();

        return response;
    }

    public void deleteAppointment(Long appointmentId) {
        var appointment = repository.findById(appointmentId).get();
        appointment.setDeletedAt(LocalDateTime.now());
        repository.save(appointment);
    }

    public UpdateAppointmentResponseDTO updateAppointment(Long appointmentId, UpdateAppointmentRequestDTO dto) {

        if (dto.title() == null && dto.description() == null && dto.startTime() == null && dto.endTime() == null) {
            throw new IllegalArgumentException("Title, Description, StartTime and EndTime cannot be null");
        }

        var appointment = repository.findById(appointmentId).get();

        if (dto.title() != null) {
            appointment.setTitle(dto.title());
        }

        if (dto.description() != null) {
            appointment.setDescription(dto.description());
        }

        if (dto.startTime() != null) {
            appointment.setStartTime(dto.startTime());
        }

        if (dto.endTime() != null) {
            appointment.setEndTime(dto.endTime());
        }

        appointment.setUpdatedAt(LocalDateTime.now());

        repository.save(appointment);

        UpdateAppointmentResponseDTO response = new UpdateAppointmentResponseDTO(appointment.getId(), appointment.getTitle(), appointment.getDescription(),
                appointment.getStartTime(), appointment.getEndTime());

        return response;
    }

}
