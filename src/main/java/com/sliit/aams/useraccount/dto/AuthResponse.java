package com.sliit.aams.useraccount.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String email;
    private String role;
    private String fullName;
}