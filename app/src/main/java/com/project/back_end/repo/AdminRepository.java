package com.project.back_end.repo;

import com.project.back_end.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Admin entity.
 * Provides basic CRUD operations and custom query methods via Spring Data JPA.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Custom method to find an admin by username
    Admin findByUsername(String username);
}
