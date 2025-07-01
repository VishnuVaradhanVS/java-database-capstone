package com.project.back_end.repo;

import com.project.back_end.model.Doctor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 1. Find a doctor by their exact email
    Doctor findByEmail(String email);

    // 2. Find doctors by partial name using LIKE (case-sensitive)
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(String name);

    // 3. Find doctors by partial name (case-insensitive) and exact specialty (case-insensitive)
    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    // 4. Find doctors by specialty only, ignoring case
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
