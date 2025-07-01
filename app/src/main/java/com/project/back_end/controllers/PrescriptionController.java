package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // 1. Save Prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable String token,
                                                                @RequestBody Prescription prescription) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");

        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        try {
            // Save prescription
            prescriptionService.savePrescription(prescription);

            // Optionally, update appointment status
            appointmentService.markAppointmentAsCompleted(prescription.getAppointmentId());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Prescription saved successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error saving prescription: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Get Prescription by Appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId,
                                             @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");

        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        Prescription prescription = prescriptionService.getPrescription(appointmentId);

        if (prescription == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No prescription found for this appointment");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(prescription, HttpStatus.OK);
    }
}
