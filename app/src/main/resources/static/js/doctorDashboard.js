// doctorDashboard.js

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// 🌐 DOM Elements
const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayButton = document.getElementById("todayButton");
const datePicker = document.getElementById("datePicker");

// 📅 Global State
let selectedDate = new Date().toISOString().split("T")[0]; // Format: YYYY-MM-DD
let patientName = null;
const token = localStorage.getItem("token");

// 🔍 Search Bar Event
searchBar.addEventListener("input", () => {
  const input = searchBar.value.trim();
  patientName = input ? input : "null";
  loadAppointments();
});

// 📆 Today Button Click Event
todayButton.addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  datePicker.value = selectedDate;
  loadAppointments();
});

// 📆 Date Picker Change Event
datePicker.addEventListener("change", () => {
  selectedDate = datePicker.value;
  loadAppointments();
});

// 📄 Load Appointments Based on Selected Date and Filter
async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);
    tableBody.innerHTML = ""; // Clear previous results

    if (!appointments || appointments.length === 0) {
      const noRow = document.createElement("tr");
      noRow.innerHTML = `<td colspan="5" style="text-align:center;">No Appointments found for today.</td>`;
      tableBody.appendChild(noRow);
      return;
    }

    appointments.forEach((appointment) => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.phone,
        email: appointment.email,
        prescription: appointment.prescription,
      };

      const row = createPatientRow(patient);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error fetching appointments:", error);
    const errorRow = document.createElement("tr");
    errorRow.innerHTML = `<td colspan="5" style="text-align:center; color: red;">Error loading appointments. Try again later.</td>`;
    tableBody.appendChild(errorRow);
  }
}

// 🚀 Initial Load
document.addEventListener("DOMContentLoaded", () => {
  if (typeof renderContent === "function") renderContent(); // Optional layout initializer
  datePicker.value = selectedDate; // Set initial date in picker
  loadAppointments(); // Load today's appointments
});
