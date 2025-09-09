package br.com.gustavobarez.Kairo.Appointment;

import org.springframework.stereotype.Service;

import br.com.gustavobarez.Kairo.User.UserRepository;

@Service
public class AppointmentService {
    
    AppointmentRepository repository;

    UserRepository userRepository;

    public AppointmentService(AppointmentRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public CreateAppointmentDTO createAppointment(CreateAppointmentDTO dto, Long creatorId) {

        var user = userRepository.findById(creatorId);

        Appointment appointment = Appointment.builder()
        .creator(user.get())
        .title(dto.title())
        .description(dto.description())
        .startTime(dto.startTime())
        .endTime(dto.endTime())
        .build();

        repository.save(appointment);

        return dto;

    }

}
