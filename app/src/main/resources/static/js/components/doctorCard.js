import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

// Named export so it can be used elsewhere
export function createDoctorCard(doctor) {
  // Create main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Get user role from localStorage
  const role = localStorage.getItem("userRole");

  // === Doctor Info Section ===
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialization}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available: ${doctor.availableTime?.join(", ") || "Not Available"}`;

  // Append all info elements to info container
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // === Actions Section ===
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // Admin: Show "Delete" button
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmDelete) return;

      const token = localStorage.getItem("token");
      const success = await deleteDoctor(doctor.id, token);

      if (success) {
        alert("Doctor deleted successfully.");
        card.remove(); // Remove card from DOM
      } else {
        alert("Failed to delete doctor.");
      }
    });
    actionsDiv.appendChild(removeBtn);
  }

  // Patient (not logged in): Show disabled Book button
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("Please login first to book an appointment.");
    });
    actionsDiv.appendChild(bookNow);
  }

  // Logged-in Patient: Show real booking button
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }

      const patientData = await getPatientData(token);
      showBookingOverlay(e, doctor, patientData);
    });
    actionsDiv.appendChild(bookNow);
  }

  // Final assembly
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
