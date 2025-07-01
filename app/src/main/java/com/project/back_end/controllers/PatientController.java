package com.project.back_end.controllers;

import com.project.back_end.models.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/patient")  // Base path for all patient-related endpoints
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    // Constructor injection
    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 1. Get Patient Details by Token
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");

        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        String email = validationResponse.getBody().get("email");
        Patient patient = patientService.getPatientDetails(email);
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    // 2. Create a New Patient
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();

        boolean isValid = service.validatePatient(patient);
        if (!isValid) {
            response.put("message", "Patient with email id or phone no already exist");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {
            response.put("message", "Signup successful");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("message", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long id, @PathVariable String token) {
        if (!service.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Unauthorized or invalid token");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(patientService.getPatientAppointment(id), HttpStatus.OK);
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        if (!service.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Unauthorized or invalid token");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        return service.filterPatient(condition, name, token);
    }
}
