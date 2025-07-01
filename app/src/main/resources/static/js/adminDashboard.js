// adminDashboard.js

import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// 🔘 Bind "Add Doctor" button to open modal
document.getElementById("addDocBtn").addEventListener("click", () => {
  openModal("addDoctor");
});

// 🟢 Load doctor cards after page load
window.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

// 📦 Load and render all doctors
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
  }
}

// 📌 Search and Filter bindings
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

// 🔍 Filter doctors based on input
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar").value.trim() || null;
  const time = document.getElementById("filterTime").value || null;
  const specialty = document.getElementById("filterSpecialty").value || null;

  try {
    const doctors = await filterDoctors(name, time, specialty);
    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    alert("Error filtering doctors");
    console.error(error);
  }
}

// 🧩 Reusable rendering function
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = ""; // Clear previous content

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// ➕ Add new doctor
window.adminAddDoctor = async function () {
  const name = document.getElementById("doctorName").value.trim();
  const email = document.getElementById("doctorEmail").value.trim();
  const phone = document.getElementById("doctorPhone").value.trim();
  const password = document.getElementById("doctorPassword").value.trim();
  const specialty = document.getElementById("doctorSpecialty").value.trim();

  // Collect checkbox values for availability
  const availability = Array.from(document.querySelectorAll(".available-time input:checked"))
    .map((checkbox) => checkbox.value);

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Login required to add a doctor.");
    return;
  }

  const doctor = { name, email, mobile: phone, password, specialization: specialty, availability };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert("Doctor added successfully!");
      document.getElementById("addDoctorModal").classList.remove("active"); // Close modal
      loadDoctorCards(); // Reload list
    } else {
      alert(result.message || "Failed to add doctor.");
    }
  } catch (error) {
    alert("An error occurred while adding the doctor.");
    console.error(error);
  }
};
