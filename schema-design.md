# Smart Clinic Management System – Schema Design

This document outlines the database schema design for the Smart Clinic Management System using both MySQL (relational) and MongoDB (document-based) databases.

---

## MySQL Database Design

The MySQL database stores core structured data that require strong relationships, constraints, and integrity. These include Patients, Doctors, Appointments, and Admins.

---

### Table: patients
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `name`: VARCHAR(100), NOT NULL
- `email`: VARCHAR(100), NOT NULL, UNIQUE
- `phone`: VARCHAR(15), NOT NULL
- `dob`: DATE, NOT NULL
- `gender`: ENUM('Male', 'Female', 'Other'), NOT NULL
- `address`: TEXT

> Each patient has a unique email and must provide a valid contact number.

---

### Table: doctors
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `name`: VARCHAR(100), NOT NULL
- `email`: VARCHAR(100), NOT NULL, UNIQUE
- `phone`: VARCHAR(15), NOT NULL
- `specialization`: VARCHAR(100), NOT NULL
- `availability_start`: TIME, NULL
- `availability_end`: TIME, NULL

> Doctors can define their working hours, which can be used to calculate available slots.

---

### Table: appointments
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `doctor_id`: INT, FOREIGN KEY → `doctors(id)`, ON DELETE CASCADE
- `patient_id`: INT, FOREIGN KEY → `patients(id)`, ON DELETE CASCADE
- `appointment_time`: DATETIME, NOT NULL
- `status`: ENUM('Scheduled', 'Completed', 'Cancelled'), DEFAULT 'Scheduled'

> If a patient or doctor is deleted, their appointments are automatically removed.

---

### Table: admins
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `username`: VARCHAR(50), NOT NULL, UNIQUE
- `password`: VARCHAR(255), NOT NULL

> Admin login is separate for managing doctor records and system control.

---

### Table: clinic_locations _(optional enhancement)_
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `name`: VARCHAR(100), NOT NULL
- `address`: TEXT, NOT NULL
- `contact_number`: VARCHAR(15), NOT NULL

> Allows expansion to multiple clinic branches.

---

### Table: payments _(optional enhancement)_
- `id`: INT, PRIMARY KEY, AUTO_INCREMENT
- `appointment_id`: INT, FOREIGN KEY → `appointments(id)`
- `amount`: DECIMAL(10, 2), NOT NULL
- `payment_time`: DATETIME, NOT NULL
- `payment_method`: ENUM('Cash', 'Card', 'UPI')

> Tracks appointment-related billing if payment integration is included.

---

## MongoDB Collection Design

MongoDB is used for unstructured, flexible data such as prescriptions, doctor notes, feedback, logs, etc.

---

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 4,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours"
    },
    {
      "name": "Cough Syrup",
      "dosage": "10ml",
      "frequency": "Twice daily"
    }
  ],
  "notes": "Take medicines after meals. Avoid cold drinks.",
  "createdAt": "2025-07-01T09:30:00Z",
  "pharmacy": {
    "name": "City Medics",
    "location": "Anna Nagar, Chennai"
  }
}
