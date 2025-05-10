package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.MessageResponse;
import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.dto.TaskStatusUpdateRequest;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.security.service.UserDetailsImpl;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Get tasks", description = "Get all tasks for ADMIN or own tasks for USER")
    public ResponseEntity<List<TaskDto>> getTasks() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(userDetails.getEmail());
        
        List<TaskDto> tasks;
        if (user.getRole().name().equals("ADMIN")) {
            tasks = taskService.getAllTasks();
        } else {
            tasks = taskService.getTasksByUser(user);
        }
        
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Get a specific task by ID if authorized")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(userDetails.getEmail());
        
        TaskDto task = taskService.getTaskById(id);
        
        // Check if user is admin or the assigned user
        if (user.getRole().name().equals("ADMIN") || task.getAssignedUserId().equals(user.getId())) {
            return ResponseEntity.ok(task);
        }
        
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskDto createdTask = taskService.createTask(taskDto, userDetails.getEmail());
        return ResponseEntity.ok(createdTask);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Update the status of a task (only by assigned user)")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest statusUpdate) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        try {
            TaskDto updatedTask = taskService.updateTaskStatus(id, statusUpdate, userDetails.getEmail());
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}