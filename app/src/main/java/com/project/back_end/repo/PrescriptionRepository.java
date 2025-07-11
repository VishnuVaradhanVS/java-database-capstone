package com.project.back_end.repo;

import com.project.back_end.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // Find all prescriptions by appointment ID
    List<Prescription> findByAppointmentId(Long appointmentId);
}
