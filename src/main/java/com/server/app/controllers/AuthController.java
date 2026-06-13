package com.server.app.controllers;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.auth.LoginRequest;
import com.server.app.dto.auth.UpdatePasswordRequest;
import com.server.app.dto.response.AuthResponse;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.User;
import com.server.app.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JsonWebToken jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody UserCreateDto request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/profile")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody UserUpdateDto request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User updatedUser = userService.updateUser(currentUser.getId(), request);
        String token = jwtUtil.createToken(updatedUser);
        return ResponseEntity.ok(new AuthResponse(token, updatedUser));
    }

    @PutMapping("/update/password")
    public ResponseEntity<User> updatePassword(@RequestBody UpdatePasswordRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userService.updatePassword(currentUser.getId(), request);

        return ResponseEntity.ok(currentUser);
    }
}