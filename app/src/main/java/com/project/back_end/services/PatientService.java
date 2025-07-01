package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Login;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.security.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> map = new HashMap<>();
        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !patient.getId().equals(id)) {
            map.put("message", "Unauthorized Access");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        List<AppointmentDTO> dtos = appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
        map.put("appointments", dtos);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> map = new HashMap<>();
        try {
            int status = condition.equalsIgnoreCase("past") ? 1 : 0;
            List<Appointment> appointments = appointmentRepository.findByStatusAndPatientId(status, id);
            List<AppointmentDTO> dtos = appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
            map.put("appointments", dtos);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            map.put("message", "Invalid condition or server error");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.findByDoctorNameContainingIgnoreCaseAndPatientId(name, patientId);
            List<AppointmentDTO> dtos = appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
            map.put("appointments", dtos);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            map.put("message", "Error filtering by doctor");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> map = new HashMap<>();
        try {
            int status = condition.equalsIgnoreCase("past") ? 1 : 0;
            List<Appointment> appointments = appointmentRepository.findByStatusAndDoctorNameContainingIgnoreCaseAndPatientId(status, name, patientId);
            List<AppointmentDTO> dtos = appointments.stream().map(AppointmentDTO::new).collect(Collectors.toList());
            map.put("appointments", dtos);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            map.put("message", "Error filtering by doctor and condition");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> map = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient != null) {
                map.put("patient", patient);
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                map.put("message", "Patient not found");
                return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            map.put("message", "Error retrieving patient details");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}        
