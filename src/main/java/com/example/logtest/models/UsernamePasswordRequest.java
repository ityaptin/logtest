package com.example.logtest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePasswordRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String name;
}
