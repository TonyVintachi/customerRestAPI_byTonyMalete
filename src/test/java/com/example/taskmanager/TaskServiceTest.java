package com.example.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task("Title1", "Description1", "Pending");
        task1.setId(1L);
        task2 = new Task("Title2", "Description2", "Completed");
        task2.setId(2L);
    }

    @Test
    void createTask_shouldReturnSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task1);
        Task created = taskService.createTask(new Task("Title1", "Description1", "Pending"));
        assertNotNull(created);
        assertEquals("Title1", created.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_whenTaskExists_shouldReturnUpdatedTask() {
        Task updatedDetails = new Task("Updated Title", "Updated Description", "Completed");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1); // Assume save returns the updated task1

        Optional<Task> result = taskService.updateTask(1L, updatedDetails);

        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task1);
    }

    @Test
    void updateTask_whenTaskDoesNotExist_shouldReturnEmpty() {
        Task updatedDetails = new Task("Updated Title", "Updated Description", "Completed");
        when(taskRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.updateTask(3L, updatedDetails);

        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(3L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_whenTaskExists_shouldReturnTrue() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        boolean deleted = taskService.deleteTask(1L);

        assertTrue(deleted);
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_whenTaskDoesNotExist_shouldReturnFalse() {
        when(taskRepository.existsById(3L)).thenReturn(false);

        boolean deleted = taskService.deleteTask(3L);

        assertFalse(deleted);
        verify(taskRepository, times(1)).existsById(3L);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllTasks_shouldReturnListOfTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));
        List<Task> tasks = taskService.getAllTasks();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTasksByStatus_shouldReturnFilteredTasks() {
        when(taskRepository.findByStatus("Completed")).thenReturn(Arrays.asList(task2));
        List<Task> tasks = taskService.getTasksByStatus("Completed");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Completed", tasks.get(0).getStatus());
        verify(taskRepository, times(1)).findByStatus("Completed");
    }

    @Test
    void getCompletedTasksPercentageThisWeek_noTasks_shouldReturnZero() {
        when(taskRepository.findAll()).thenReturn(List.of());
        double percentage = taskService.getCompletedTasksPercentageThisWeek();
        assertEquals(0.0, percentage);
    }

    @Test
    void getCompletedTasksPercentageThisWeek_oneCompletedOutOfTwo_shouldReturnFiftyPercent() {
        Task pendingTask = new Task("Pending Task", "Desc", "Pending");
        Task completedTask = new Task("Completed Task", "Desc", "Completed");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(pendingTask, completedTask));

        double percentage = taskService.getCompletedTasksPercentageThisWeek();
        assertEquals(50.0, percentage);
    }

     @Test
    void getCompletedTasksPercentageThisWeek_allTasksCompleted_shouldReturnHundredPercent() {
        Task completedTask1 = new Task("Completed Task 1", "Desc 1", "Completed");
        Task completedTask2 = new Task("Completed Task 2", "Desc 2", "Completed");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(completedTask1, completedTask2));

        double percentage = taskService.getCompletedTasksPercentageThisWeek();
        assertEquals(100.0, percentage);
    }

    @Test
    void getCompletedTasksPercentageThisWeek_noTasksCompleted_shouldReturnZeroPercent() {
        Task pendingTask1 = new Task("Pending Task 1", "Desc 1", "Pending");
        Task pendingTask2 = new Task("Pending Task 2", "Desc 2", "Pending");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(pendingTask1, pendingTask2));

        double percentage = taskService.getCompletedTasksPercentageThisWeek();
        assertEquals(0.0, percentage);
    }
}
