package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repositories.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service  // 1. Mark as Spring-managed service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    // 2. Constructor injection for repository dependency
    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // 3. Save Prescription
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            // Check if a prescription already exists for this appointment
            Prescription existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (existing != null) {
                response.put("message", "Prescription already exists for this appointment");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Save the new prescription
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();  // Optional: Use logger instead
            response.put("message", "An error occurred while saving the prescription");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Get Prescription by appointmentId
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescription == null) {
                response.put("message", "No prescription found for this appointment");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.put("prescription", prescription);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();  // Optional: Use logger instead
            response.put("message", "An error occurred while retrieving the prescription");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
