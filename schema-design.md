# Smart Clinic Management System – Schema Design

This document defines the data models used in the Smart Clinic Management System, separating structured relational data into MySQL and flexible, document-oriented data into MongoDB.

---

## MySQL Database Design

Structured data like users and appointments are stored in MySQL using JPA entities.

---

### Table: admins

- `id`: `BIGINT`, PRIMARY KEY, AUTO_INCREMENT
- `username`: `VARCHAR(100)`, NOT NULL, UNIQUE
- `password`: `VARCHAR(255)`, NOT NULL

> Used for system-level access to manage the platform securely.

---

### Table: doctors

- `id`: `BIGINT`, PRIMARY KEY, AUTO_INCREMENT
- `name`: `VARCHAR(100)`, NOT NULL
- `specialty`: `VARCHAR(50)`, NOT NULL
- `email`: `VARCHAR(100)`, NOT NULL, UNIQUE
- `password`: `VARCHAR(255)`, NOT NULL
- `phone`: `VARCHAR(15)`, NOT NULL
- `available_times`: `TEXT` or separate table using `@ElementCollection` (for flexible availability)

> Doctors can update their contact info, availability, and specialization.

---

### Table: patients

- `id`: `BIGINT`, PRIMARY KEY, AUTO_INCREMENT
- `name`: `VARCHAR(100)`, NOT NULL
- `email`: `VARCHAR(100)`, NOT NULL, UNIQUE
- `password`: `VARCHAR(255)`, NOT NULL
- `phone`: `VARCHAR(15)`, NOT NULL
- `address`: `VARCHAR(255)`, NOT NULL

> Patient info is used for login, appointments, and prescriptions.

---

### Table: appointments

- `id`: `BIGINT`, PRIMARY KEY, AUTO_INCREMENT
- `doctor_id`: `BIGINT`, FOREIGN KEY → doctors(`id`) ON DELETE CASCADE
- `patient_id`: `BIGINT`, FOREIGN KEY → patients(`id`) ON DELETE CASCADE
- `appointment_time`: `DATETIME`, NOT NULL, must be future
- `status`: `INT`, NOT NULL (0 = Scheduled, 1 = Completed)

> Represents scheduled or completed meetings between doctors and patients.

---

## MongoDB Collection Design

Flexible and unstructured data like prescriptions are stored in MongoDB using Spring Data MongoDB.

---

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take after meals. Avoid cold items."
}
