package com.example.taskmanagement.dto;

import com.example.taskmanagement.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String titre;
    
    @Size(max = 500)
    private String description;
    
    private TaskStatus statut;
    
    private Long assignedUserId;
    
    private String assignedUserName;
}