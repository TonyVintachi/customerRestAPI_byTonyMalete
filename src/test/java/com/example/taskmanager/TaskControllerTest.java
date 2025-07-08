package com.example.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task task1;
    private Task task2;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        task1 = new Task("Title1", "Description1", "Pending");
        task1.setId(1L);
        task2 = new Task("Title2", "Description2", "Completed");
        task2.setId(2L);
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(task1);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title1"));
    }

    @Test
    void updateTask_whenTaskExists_shouldReturnUpdatedTask() throws Exception {
        Task updatedDetails = new Task("Updated Title", "Updated Desc", "Completed");
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(Optional.of(new Task("Updated Title", "Updated Desc", "Completed")));

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void updateTask_whenTaskDoesNotExist_shouldReturnNotFound() throws Exception {
        Task updatedDetails = new Task("Updated Title", "Updated Desc", "Completed");
        when(taskService.updateTask(eq(3L), any(Task.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/tasks/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_whenTaskExists_shouldReturnNoContent() throws Exception {
        when(taskService.deleteTask(1L)).thenReturn(true);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_whenTaskDoesNotExist_shouldReturnNotFound() throws Exception {
        when(taskService.deleteTask(3L)).thenReturn(false);

        mockMvc.perform(delete("/tasks/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_noStatus_shouldReturnAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getAllTasks_withStatus_shouldReturnFilteredTasks() throws Exception {
        when(taskService.getTasksByStatus("Completed")).thenReturn(List.of(task2));

        mockMvc.perform(get("/tasks").param("status", "Completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].status").value("Completed"));
    }

    @Test
    void getCompletedTasksPercentageThisWeek_shouldReturnPercentage() throws Exception {
        when(taskService.getCompletedTasksPercentageThisWeek()).thenReturn(50.0);

        mockMvc.perform(get("/tasks/completed/percentage"))
                .andExpect(status().isOk())
                .andExpect(content().string("50.0"));
    }
}
