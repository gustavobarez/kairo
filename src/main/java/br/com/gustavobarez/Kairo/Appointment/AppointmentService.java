package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.gustavobarez.Kairo.User.User;
import br.com.gustavobarez.Kairo.User.UserRepository;
import br.com.gustavobarez.Kairo.User.UserResponseDTO;
import br.com.gustavobarez.Kairo.exceptions.OperationNotAllowedException;
import br.com.gustavobarez.Kairo.exceptions.ResourceNotFoundException;

@Service
public class AppointmentService {

    AppointmentRepository repository;
    UserRepository userRepository;

    public AppointmentService(AppointmentRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public AppointmentDTO createAppointment(AppointmentDTO dto, Long creatorId) {
        var user = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + creatorId));

        Appointment appointment = Appointment.builder()
                .creator(user)
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

        if (inviteAppointmentDTO.usersId() == null || inviteAppointmentDTO.usersId().isEmpty()) {
            throw new IllegalArgumentException("Users ID list cannot be null or empty");
        }

        var appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getCreator().getId().equals(creatorId)) {
            throw new OperationNotAllowedException("Only the appointment creator can invite participants");
        }

        var users = userRepository.findAllById(inviteAppointmentDTO.usersId());

        if (users.size() != inviteAppointmentDTO.usersId().size()) {
            throw new ResourceNotFoundException("One or more users not found");
        }

        for (User user : users) {
            if (!appointment.getParticipants().contains(user)) {
                appointment.getParticipants().add(user);
            }
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

    public void deleteAppointment(Long appointmentId, Long userId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }

        var appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getCreator().getId().equals(userId)) {
            throw new OperationNotAllowedException("Only the appointment creator can delete it");
        }

        appointment.setDeletedAt(LocalDateTime.now());
        repository.save(appointment);
    }

    public UpdateAppointmentResponseDTO updateAppointment(Long appointmentId,
            UpdateAppointmentRequestDTO dto, Long userId) {

        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }

        if (dto.title() == null && dto.description() == null &&
                dto.startTime() == null && dto.endTime() == null) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        var appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getCreator().getId().equals(userId)) {
            throw new OperationNotAllowedException("Only the appointment creator can update it");
        }

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

        UpdateAppointmentResponseDTO response = new UpdateAppointmentResponseDTO(
                appointment.getId(),
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getStartTime(),
                appointment.getEndTime());

        return response;
    }
}