package com.example.manufacturing_flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthRequest {
    @Schema(example = "admin")
    private String username;
    
    @Schema(example = "admin123")
    private String password;
}
