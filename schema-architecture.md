1. User accesses AdminDashboard, DoctorDashboard, or REST-based modules like Appointment.

2. Request is routed to the corresponding Thymeleaf or REST controller.

3. Controller validates input and delegates logic to the service layer.

4. Service layer applies business rules and coordinates operations.

5. Service calls the appropriate MySQL or MongoDB repository.

6. Repository interacts with the database to fetch or persist data.

7. Retrieved data is bound to model classes (@Entity or @Document).

8. Models are returned to the service, then passed back to the controller.

9. Controller returns a rendered HTML page (Thymeleaf) or JSON response (REST).

10. Client receives and displays the page or processes the JSON result.
