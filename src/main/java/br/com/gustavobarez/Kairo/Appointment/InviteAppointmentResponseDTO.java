package br.com.gustavobarez.Kairo.Appointment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.gustavobarez.Kairo.User.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteAppointmentResponseDTO {

    private UserResponseDTO creator;

    private String title;

    private String description;

    private LocalDateTime starTime;

    private LocalDateTime endTime;

    private List<UserResponseDTO> participants = new ArrayList<>();

}
