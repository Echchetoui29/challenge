package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.dto.TaskStatusUpdateRequest;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.exception.TaskNotAssignedToUserException;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksByUser(User user) {
        return taskRepository.findByAssignedUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return convertToDto(task);
    }

    @Transactional
    public TaskDto createTask(TaskDto taskDto, String creatorEmail) {
        Task task = new Task();
        task.setTitre(taskDto.getTitre());
        task.setDescription(taskDto.getDescription());
        task.setStatut(taskDto.getStatut());
        
        if (taskDto.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(taskDto.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDto.getAssignedUserId()));
            task.setAssignedUser(assignedUser);
        } else {
            // Assign to creator by default
            User creator = userRepository.findByEmail(creatorEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Creator not found with email: " + creatorEmail));
            task.setAssignedUser(creator);
        }
        
        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }

    @Transactional
    public TaskDto updateTaskStatus(Long taskId, TaskStatusUpdateRequest statusUpdate, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        
        // Check if the user is the assigned user or an admin
        if (!task.getAssignedUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new TaskNotAssignedToUserException("You are not authorized to update this task");
        }
        
        task.setStatut(statusUpdate.getStatut());
        Task updatedTask = taskRepository.save(task);
        
        return convertToDto(updatedTask);
    }

    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitre(task.getTitre());
        dto.setDescription(task.getDescription());
        dto.setStatut(task.getStatut());
        
        if (task.getAssignedUser() != null) {
            dto.setAssignedUserId(task.getAssignedUser().getId());
            dto.setAssignedUserName(task.getAssignedUser().getNom());
        }
        
        return dto;
    }
}