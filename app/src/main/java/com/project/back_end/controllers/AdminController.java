package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController  // 1. Mark as a REST controller
@RequestMapping("${api.path}admin")  // 1. Base path using application.properties (e.g., /api/admin)
public class AdminController {

    private final Service service;

    // 2. Constructor injection of the service
    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    // 3. POST endpoint for admin login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
