package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController  // 1. Mark as REST controller
@RequestMapping("${api.path}doctor")  // 1. Base path for doctor-related endpoints
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    // 2. Constructor injection
    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 3. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        if (!service.validateToken(token, user).getStatusCode().is2xxSuccessful()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Unauthorized or invalid token");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> availability = doctorService.getDoctorAvailability(doctorId, date);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }

    // 4. Get all doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 5. Add new doctor (admin only)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin").getStatusCode().is2xxSuccessful()) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == -1) {
            response.put("message", "Doctor already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } else if (result == 1) {
            response.put("message", "Doctor added to db");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("message", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 6. Doctor login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 7. Update doctor (admin only)
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin").getStatusCode().is2xxSuccessful()) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == -1) {
            response.put("message", "Doctor not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else if (result == 1) {
            response.put("message", "Doctor updated");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 8. Delete doctor (admin only)
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin").getStatusCode().is2xxSuccessful()) {
            response.put("message", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        int result = doctorService.deleteDoctor(id);

        if (result == -1) {
            response.put("message", "Doctor not found with id");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Some internal error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 9. Filter doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        Map<String, Object> result = service.filterDoctor(name, speciality, time);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
