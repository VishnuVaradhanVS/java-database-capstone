package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.service.Service;

import java.util.Map;

@Controller
public class DashboardController {

    // 2. Autowire the Service that contains the token validation logic
    @Autowired
    private Service service;

    // 3. Admin Dashboard Route
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, String> result = service.validateToken(token, "admin");

        // If token is valid (empty error map), return the admin dashboard view
        if (result.isEmpty()) {
            return "admin/adminDashboard";
        }

        // If token is invalid, redirect to login page
        return "redirect:http://localhost:8080";
    }

    // 4. Doctor Dashboard Route
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, String> result = service.validateToken(token, "doctor");

        if (result.isEmpty()) {
            return "doctor/doctorDashboard";
        }

        return "redirect:http://localhost:8080";
    }
}
