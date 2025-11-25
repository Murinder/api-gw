package ru.uni.ecop.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    
    @Override
    public String toString() {
        return "{\"status\":" + status + ",\"error\":\"" + error + "\",\"message\":\"" + message + "\"}";
    }
}