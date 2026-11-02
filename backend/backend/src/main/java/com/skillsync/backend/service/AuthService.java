package com.skillsync.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillsync.backend.dto.RegisterRequest;
import com.skillsync.backend.entity.User;
import com.skillsync.backend.repository.UserRepository;

@Service 
public class AuthService 
{
    private final UserRepository userrepo;
    private final PasswordEncoder passwordEncoder;
    public AuthService(UserRepository u,PasswordEncoder p)
    {
        this.userrepo=u;
        this.passwordEncoder=p;
    }

    public User register(RegisterRequest req)
    {
        String email=req.getEmail().trim().toLowerCase();

        if(!userrepo.existsByEmail(email))
        {
            throw new IllegalArgumentException("Email aldready registered");
        }

        String hashedpasskey=passwordEncoder.encode(req.getPassword());
        User u=new User(req.getName().trim(),email,hashedpasskey);

        return userrepo.save(u);

    }


}
