package com.example.taskmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing tasks.
 * Exposes API endpoints for CRUD operations and other task-related functionalities.
 */
@RestController
@RequestMapping("/tasks") // Base path for all task-related endpoints
public class TaskController {

    @Autowired
    private TaskService taskService; // Service to handle business logic

    /**
     * Creates a new task.
     * Endpoint: POST /tasks
     * @param task The task details from the request body.
     * @return ResponseEntity containing the created task and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Updates an existing task.
     * Endpoint: PUT /tasks/{id}
     * @param id The ID of the task to update.
     * @param taskDetails The new details for the task from the request body.
     * @return ResponseEntity containing the updated task and HTTP status 200 (OK) if found,
     *         otherwise HTTP status 404 (Not Found).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Optional<Task> updatedTask = taskService.updateTask(id, taskDetails);
        return updatedTask.map(task -> new ResponseEntity<>(task, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Deletes a task by its ID.
     * Endpoint: DELETE /tasks/{id}
     * @param id The ID of the task to delete.
     * @return ResponseEntity with HTTP status 204 (No Content) if deleted,
     *         otherwise HTTP status 404 (Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Retrieves all tasks, optionally filtered by status.
     * Endpoint: GET /tasks
     * @param status Optional request parameter to filter tasks by status.
     * @return ResponseEntity containing a list of tasks and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false) String status) {
        List<Task> tasks;
        if (status != null && !status.isEmpty()) {
            tasks = taskService.getTasksByStatus(status); // Get tasks filtered by status
        } else {
            tasks = taskService.getAllTasks(); // Get all tasks
        }
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Retrieves the percentage of tasks completed this week.
     * Endpoint: GET /tasks/completed/percentage
     * @return ResponseEntity containing the percentage and HTTP status 200 (OK).
     */
    @GetMapping("/completed/percentage")
    public ResponseEntity<Double> getCompletedTasksPercentageThisWeek() {
        double percentage = taskService.getCompletedTasksPercentageThisWeek();
        return new ResponseEntity<>(percentage, HttpStatus.OK);
    }
}
