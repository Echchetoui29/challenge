package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskStatusUpdateRequest;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.exception.TaskNotAssignedToUserException;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private User admin;
    private Task task;
    private TaskStatusUpdateRequest statusUpdateRequest;

    @BeforeEach
    public void setup() {
        // Setup test user
        user = new User();
        user.setId(1L);
        user.setNom("Test User");
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        // Setup admin user
        admin = new User();
        admin.setId(2L);
        admin.setNom("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);

        // Setup test task
        task = new Task();
        task.setId(1L);
        task.setTitre("Test Task");
        task.setDescription("Test Description");
        task.setStatut(TaskStatus.À_FAIRE);
        task.setAssignedUser(user);

        // Setup status update request
        statusUpdateRequest = new TaskStatusUpdateRequest();
        statusUpdateRequest.setStatut(TaskStatus.EN_COURS);

        // Setup repository mocks
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testUpdateTaskStatus_ByAssignedUser_Success() {
        // When assigned user updates status
        taskService.updateTaskStatus(1L, statusUpdateRequest, "user@example.com");

        // Then task should be updated
        verify(taskRepository, times(1)).save(task);
        assertEquals(TaskStatus.EN_COURS, task.getStatut());
    }

    @Test
    public void testUpdateTaskStatus_ByAdmin_Success() {
        // When admin updates status
        taskService.updateTaskStatus(1L, statusUpdateRequest, "admin@example.com");

        // Then task should be updated
        verify(taskRepository, times(1)).save(task);
        assertEquals(TaskStatus.EN_COURS, task.getStatut());
    }

    @Test
    public void testUpdateTaskStatus_ByUnauthorizedUser_Exception() {
        // Setup another user who is not assigned to the task
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setRole(Role.USER);
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));

        // When another user tries to update status
        assertThrows(TaskNotAssignedToUserException.class, () -> {
            taskService.updateTaskStatus(1L, statusUpdateRequest, "another@example.com");
        });

        // Then task should not be updated
        verify(taskRepository, never()).save(any());
    }

    @Test
    public void testUpdateTaskStatus_TaskNotFound_Exception() {
        // When task does not exist
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTaskStatus(999L, statusUpdateRequest, "user@example.com");
        });
    }

    @Test
    public void testUpdateTaskStatus_UserNotFound_Exception() {
        // When user does not exist
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTaskStatus(1L, statusUpdateRequest, "nonexistent@example.com");
        });
    }
}