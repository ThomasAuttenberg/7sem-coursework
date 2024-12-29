package com.watcher.WatcherB.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String username;
    private List<String> roles;
    private String token;
}
