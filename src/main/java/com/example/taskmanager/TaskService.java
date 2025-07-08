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
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(Long id, Task taskDetails) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setStatus(taskDetails.getStatus());
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public double getCompletedTasksPercentageThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Task> allTasks = taskRepository.findAll();
        if (allTasks.isEmpty()) {
            return 0.0;
        }

        long completedTasksThisWeek = allTasks.stream()
                .filter(task -> "Completed".equalsIgnoreCase(task.getStatus()))
                .filter(task -> {
                    // Assuming Task has a getCompletionDate() or similar method
                    // For now, let's assume all tasks in the DB are relevant for this calculation
                    // or that we'd add a date field to the Task entity.
                    // This part needs refinement based on actual Task entity structure.
                    // For a simple implementation, we might consider all tasks.
                    // If tasks have a creation or completion date, filter by that.
                    // Let's assume for now we consider all tasks in the DB for simplicity
                    // and that "Completed" status implies it was completed *sometime*.
                    // A more accurate implementation would require a completion timestamp.
                    return true; // Placeholder for date filtering logic
                })
                .count();

        //This calculation should ideally filter by tasks completed within the current week.
        //However, the current Task entity does not have a completion date.
        //For now, it calculates the percentage of all *ever* completed tasks against all tasks.
        //To make this accurate "per week", a completionDate field in Task is needed.

        long totalTasks = allTasks.size();
        if (totalTasks == 0) {
            return 0.0;
        }

        return (double) completedTasksThisWeek / totalTasks * 100;
    }
}
