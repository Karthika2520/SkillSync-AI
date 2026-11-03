package com.skillsync.backend.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillsync.backend.dto.LoginRequest;
import com.skillsync.backend.dto.RegisterRequest;
import com.skillsync.backend.entity.User;
import com.skillsync.backend.repository.UserRepository;
import com.skillsync.backend.security.JwtService;
import com.skillsync.backend.dto.AuthResponse;

@Service 
public class AuthService 
{
    private final UserRepository userrepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(UserRepository u,PasswordEncoder p, JwtService jwtService)
    {
        this.userrepo=u;
        this.passwordEncoder=p;
        this.jwtService=jwtService;
    }

    public User register(RegisterRequest req)
    {
        String email=req.getEmail().trim().toLowerCase();

        if(userrepo.existsByEmail(email))
        {
            throw new IllegalArgumentException("Email aldready registered");
        }

        String hashedpasskey=passwordEncoder.encode(req.getPassword());
        User u=new User(req.getName().trim(),email,hashedpasskey);

        return userrepo.save(u);

    }

   public AuthResponse login(LoginRequest lr) {

    String email = lr.getEmail().trim().toLowerCase();

    // Find user by email
    User user = userrepo.findByEmail(email)
            .orElseThrow(() ->
                new IllegalArgumentException("Invalid email or password"));

    // Verify password
    if (!passwordEncoder.matches(
            lr.getPassword(), user.getPassword())) {

        throw new IllegalArgumentException(
                "Invalid email or password");
    }
    String token = jwtService.generateToken(user.getEmail());
    return new AuthResponse(token);

  
}


}
