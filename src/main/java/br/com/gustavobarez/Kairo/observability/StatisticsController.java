package br.com.gustavobarez.Kairo.observability;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gustavobarez.Kairo.Appointment.AppointmentRepository;
import br.com.gustavobarez.Kairo.User.UserRepository;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatistics() {
        long startTime = System.currentTimeMillis();
        logger.info("Starting statistics calculation");

        try {
            Map<String, Object> stats = new HashMap<>();

            long totalUsers = userRepository.count();
            long totalAppointments = appointmentRepository.count();

            stats.put("totalUsers", totalUsers);
            stats.put("totalAppointments", totalAppointments);
            stats.put("timestamp", java.time.LocalDateTime.now());

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Statistics calculated successfully in {}ms", duration);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Error calculating statistics after {}ms: {}", duration, e.getMessage());
            throw e;
        }
    }
}