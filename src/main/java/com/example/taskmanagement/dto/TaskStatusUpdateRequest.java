package com.example.taskmanagement.dto;

import com.example.taskmanagement.model.TaskStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskStatusUpdateRequest {
    @NotNull
    private TaskStatus statut;
}