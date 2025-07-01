package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Login;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.utils.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isEmpty()) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
            response.put("token", tokenService.generateToken("doctor", doctor.getId()));
            return ResponseEntity.ok(response);
        }
        response.put("error", "Invalid Credentials");
        return ResponseEntity.status(401).body(response);
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", filtered);
        return result;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(name);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctorByTime(doctors, amOrPm);
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctors);
        return result;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream().filter(doc -> doc.getAvailableTimes().stream().anyMatch(time -> {
            int hour = LocalTime.parse(time).getHour();
            return "AM".equalsIgnoreCase(amOrPm) ? hour < 12 : hour >= 12;
        })).collect(Collectors.toList());
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<LocalTime> bookedSlots = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                .stream()
                .map(appt -> appt.getAppointmentTime().toLocalTime())
                .collect(Collectors.toList());

        List<String> defaultSlots = List.of("09:00", "10:00", "11:00", "12:00", "14:00", "15:00");

        return defaultSlots.stream()
                .filter(time -> !bookedSlots.contains(LocalTime.parse(time)))
                .collect(Collectors.toList());
    }
}    
