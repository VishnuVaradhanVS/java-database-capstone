package com.project.back_end.services;

import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.util.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    /**
     * Book a new appointment
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Update an existing appointment
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
        if (existing.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existingAppointment = existing.get();
        if (!existingAppointment.getPatient().getId().equals(appointment.getPatient().getId())) {
            response.put("message", "Unauthorized: Only the patient can update this appointment.");
            return ResponseEntity.status(403).body(response);
        }

        // Check doctor availability
        LocalDateTime start = appointment.getAppointmentTime();
        LocalDateTime end = start.plusHours(1);
        List<Appointment> overlapping = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        appointment.getDoctor().getId(), start, end.minusSeconds(1));

        if (!overlapping.isEmpty()) {
            response.put("message", "Doctor is not available at the chosen time.");
            return ResponseEntity.badRequest().body(response);
        }

        // Save updated appointment
        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an appointment
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Long patientId = tokenService.extractIdFromToken(token);
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = optionalAppointment.get();
        if (!appointment.getPatient().getId().equals(patientId)) {
            response.put("message", "Unauthorized: You cannot cancel this appointment.");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment canceled successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Get appointments for a doctor on a specific date, filtered by patient name (optional)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        Long doctorId = tokenService.extractIdFromToken(token);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        // Filter by patient name if provided
        if (pname != null && !pname.equalsIgnoreCase("null")) {
            appointments.removeIf(app -> !app.getPatient().getName().toLowerCase().contains(pname.toLowerCase()));
        }

        response.put("appointments", appointments);
        return response;
    }

    /**
     * Change appointment status
     */
    @Transactional
    public void changeStatus(Long appointmentId, int status) {
        Optional<Appointment> optional = appointmentRepository.findById(appointmentId);
        if (optional.isPresent()) {
            Appointment appointment = optional.get();
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
        }
    }
}
