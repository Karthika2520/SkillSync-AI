package com.skillsync.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsync.backend.dto.AuthResponse;
import com.skillsync.backend.dto.LoginRequest;
import com.skillsync.backend.dto.RegisterRequest;
import com.skillsync.backend.service.AuthService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/auth")
@CrossOrigin ("http://localhost:5173")
public class AuthController 
{
    private final AuthService ser;
    public AuthController(AuthService s)
    {
        this.ser=s;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid  @RequestBody RegisterRequest req)
    {
        ser.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("User Registered Successfully!");
    }
    @PostMapping("/login")
public ResponseEntity<AuthResponse> login(
        @Valid @RequestBody LoginRequest request) {

    AuthResponse r= ser.login(request);
    return ResponseEntity.ok(r);

    
}


}
