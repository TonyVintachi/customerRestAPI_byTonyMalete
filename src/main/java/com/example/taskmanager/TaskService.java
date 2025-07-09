package com.example.taskmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository; // Repository for task data access

    /**
     * Creates a new task.
     * @param task The task to create.
     * @return The created task.
     */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Updates an existing task.
     * @param id The ID of the task to update.
     * @param taskDetails The new details for the task.
     * @return An Optional containing the updated task if found, otherwise empty.
     */
    public Optional<Task> updateTask(Long id, Task taskDetails) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setStatus(taskDetails.getStatus());
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty(); // Task not found
    }

    /**
     * Deletes a task by its ID.
     * @param id The ID of the task to delete.
     * @return true if the task was deleted, false otherwise.
     */
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false; // Task not found
    }

    /**
     * Retrieves all tasks.
     * @return A list of all tasks.
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Retrieves tasks filtered by their status.
     * @param status The status to filter by.
     * @return A list of tasks matching the status.
     */
    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Calculates the percentage of tasks completed this week.
     * Note: This implementation currently calculates the percentage of all *ever* completed tasks
     * against all tasks, as the Task entity does not have a completion date.
     * To make this accurate "per week", a completionDate field in Task is needed.
     * This method uses placeholder logic for date filtering.
     * @return The percentage of completed tasks.
     */
    public double getCompletedTasksPercentageThisWeek() {
        // Placeholder: Simulate fetching tasks for the current week.
        // In a real scenario, this would involve a query with date filtering
        // if the Task entity had a creationDate or completionDate field.
        List<Task> allTasks = taskRepository.findAll(); // Fetches all tasks as a stand-in.

        if (allTasks.isEmpty()) {
            return 0.0;
        }

        // Placeholder: Filter for completed tasks.
        // This part is correct based on the 'status' field.
        long completedTasksThisWeek = allTasks.stream()
                .filter(task -> "Completed".equalsIgnoreCase(task.getStatus()))
                // Placeholder: No actual date filtering for "this week" is applied here
                // due to the absence of a relevant date field in the Task entity.
                // All completed tasks are counted.
                .count();

        long totalTasksConsidered = allTasks.size(); // Using the count of all tasks as total for this placeholder.

        if (totalTasksConsidered == 0) {
            return 0.0;
        }

        // Calculate percentage based on the placeholder data.
        return (double) completedTasksThisWeek / totalTasksConsidered * 100;
    }
}
