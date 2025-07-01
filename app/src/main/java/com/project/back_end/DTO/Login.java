package com.project.back_end.DTO;

/**
 * A simple DTO class used to receive login credentials from the frontend.
 * This class is meant to be used in @RequestBody parameters in controller methods.
 */
public class Login {

    private String email;
    private String password;

    // Default constructor (required for deserialization)
    public Login() {
    }

    // Optional: Parameterized constructor (useful for testing or manual object creation)
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
