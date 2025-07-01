package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController  // 1. Declare as REST controller
@RequestMapping("/appointments")  // 1. Base path for all appointment-related endpoints
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    // 2. Constructor injection for required services
    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Get Appointments - only for doctors
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        if (!service.validateToken(token, "doctor").getStatusCode().is2xxSuccessful()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Invalid or expired token");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        List<Appointment> appointments = appointmentService.getAppointments(date, patientName);
        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 4. Book Appointment - only for patients
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int status = service.validateAppointment(appointment);
        if (status == -1) {
            response.put("message", "Invalid doctor ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (status == 0) {
            response.put("message", "Appointment time not available");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        boolean booked = appointmentService.bookAppointment(appointment);
        if (booked) {
            response.put("message", "Appointment booked successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("message", "Failed to book appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Update Appointment - only for patients
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        boolean updated = appointmentService.updateAppointment(appointment);
        if (updated) {
            response.put("message", "Appointment updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Failed to update appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 6. Cancel Appointment - only for patients
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient").getStatusCode().is2xxSuccessful()) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        boolean canceled = appointmentService.cancelAppointment(id);
        if (canceled) {
            response.put("message", "Appointment cancelled successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Failed to cancel appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
