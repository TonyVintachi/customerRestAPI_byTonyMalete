# Task Management RESTful API

## Description

This project is a simple RESTful API for basic task management. It allows users to create, update, delete, and retrieve tasks, as well as filter tasks by status and get a weekly completion percentage.

## Core Features Implemented

*   **Create a new task**: With a title, description, and status (Pending or Completed).
*   **Update an existing task**: Modify the title, description, or status of a task.
*   **Delete a task**: Remove a task by its ID.
*   **Retrieve all tasks**: Get a list of all tasks.
*   **Filter tasks by status**: Retrieve tasks based on their status (e.g., "Pending", "Completed").
*   **Return the percentage of completed tasks per week**: Calculates the percentage of completed tasks. (See Assumptions section for current implementation details).

## Tech Stack

*   **Java 17**
*   **Spring Boot 3.5.3**: For building the RESTful API.
*   **Spring Data JPA**: For data persistence.
*   **SQLite**: As the relational database.
*   **Maven**: For project build and dependency management.
*   **JUnit 5 & Mockito**: For unit testing.

## Setup Instructions

### Prerequisites

*   Java Development Kit (JDK) 17 or later.
*   Apache Maven 3.6.x or later.

### Build

1.  Clone the repository or download the source code.
2.  Navigate to the project's root directory (where `pom.xml` is located).
3.  Run the following Maven command to build the project and package it into a JAR file:
    ```bash
    ./mvnw clean package
    ```
    (or `mvnw.cmd clean package` on Windows)

### Run

After a successful build, you can run the application using:

```bash
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```

The API will typically be available at `http://localhost:8080`.

## API Endpoints

All endpoints are prefixed with `/tasks`.

| Method | Endpoint                     | Description                                      | Request Body Example                     | Response Example (Success)                 |
| :----- | :--------------------------- | :----------------------------------------------- | :--------------------------------------- | :----------------------------------------- |
| POST   | `/`                          | Create a new task                                | `{"title":"New Task", "description":"Task details", "status":"Pending"}` | `{"id":1, "title":"New Task", ...}` (201 Created) |
| PUT    | `/{id}`                      | Update an existing task                          | `{"title":"Updated Task", "description":"New details", "status":"Completed"}` | `{"id":1, "title":"Updated Task", ...}` (200 OK) |
| DELETE | `/{id}`                      | Delete a task by ID                            | N/A                                      | (204 No Content)                           |
| GET    | `/`                          | Retrieve all tasks                               | N/A                                      | `[{"id":1, ...}, {"id":2, ...}]` (200 OK)   |
| GET    | `/?status={status}`          | Retrieve tasks filtered by status                | N/A                                      | `[{"id":1, "status":"Completed", ...}]` (200 OK) |
| GET    | `/completed/percentage`      | Get percentage of completed tasks (this week)    | N/A                                      | `50.0` (200 OK)                            |

*(Note: `{id}` in endpoints should be replaced with the actual task ID, and `{status}` with the desired status string like "Pending" or "Completed".)*

## Assumptions Made

1.  **Task Status**: The status of a task is a simple string (e.g., "Pending", "Completed"). No enum or strict validation is enforced at the model level beyond what's handled in the service/controller for these two states.
2.  **`getCompletedTasksPercentageThisWeek` Logic**:
    *   The current `Task` entity does not include a `creationDate` or `completionDate` timestamp.
    *   Therefore, the "percentage of completed tasks per week" currently calculates the percentage of *all tasks ever marked as "Completed"* against the *total number of all tasks* in the database.
    *   To implement true "per week" filtering, a date field indicating task creation or completion would need to be added to the `Task` entity, and the service logic updated accordingly. The current implementation serves as a placeholder for this functionality.
3.  **Database Initialization**: The `spring.jpa.hibernate.ddl-auto=update` property is used, meaning Hibernate will attempt to update the schema based on entities. For a production environment, more robust database migration tools like Flyway or Liquibase would be recommended.
4.  **Error Handling**: Basic error handling is in place (e.g., 404 for not found tasks). More sophisticated global error handling could be added.
5.  **Security**: No security (like authentication or authorization) has been implemented as it was an optional bonus feature. API endpoints are currently open.

## Design Questions/Choices

1.  **Why SQLite?**
    *   SQLite was chosen for its simplicity and ease of setup as an in-memory or file-based database, fulfilling the requirement for an "SQLite or any in-memory database". It doesn't require a separate database server, making it convenient for local development and testing of a minimal API.

2.  **Task Entity Structure (`Task.java`)**:
    *   The `Task` entity includes `id` (auto-generated primary key), `title`, `description`, and `status`.
    *   This structure is minimal but covers the core requirements.
    *   Future enhancements could include fields like `creationDate`, `dueDate`, `completionDate`, `priority`, or `assignee` if user management were introduced.

3.  **Service Layer (`TaskService.java`)**:
    *   A dedicated service layer was implemented to encapsulate business logic, separating it from the controller (API layer) and repository (data access layer). This promotes better organization and testability.

4.  **Repository Layer (`TaskRepository.java`)**:
    *   Spring Data JPA's `JpaRepository` is used to minimize boilerplate code for CRUD operations.
    *   A custom query method `findByStatus(String status)` was added to support filtering by status, as required.

5.  **Controller Layer (`TaskController.java`)**:
    *   Standard RESTful principles are followed using `@RestController`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`.
    *   `ResponseEntity` is used to provide control over HTTP status codes and response bodies.

6.  **Trade-offs for `getCompletedTasksPercentageThisWeek`**:
    *   As mentioned in Assumptions, the lack of a timestamp in the `Task` entity means the current implementation of this feature is a placeholder. The trade-off was to meet the API endpoint requirement without significantly altering the entity structure at this stage, with the understanding that a date field would be necessary for accurate weekly calculations.

## Bonus Features (Status)

*   **Basic token-based user authentication**: Not implemented.
*   **Unit tests using JUnit**: Implemented for the service and controller layers.
*   **Deploy the solution locally using Docker**: Not implemented. A `Dockerfile` could be added for containerization.

---
