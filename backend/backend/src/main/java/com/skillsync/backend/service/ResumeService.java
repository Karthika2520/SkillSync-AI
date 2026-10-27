package com.skillsync.backend.service;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.skillsync.backend.entity.Resume;
import com.skillsync.backend.repository.ResumeRepository;

@Service 
public class ResumeService 
{
    private ResumeRepository repo;
    public ResumeService(ResumeRepository repo)
    {
        this.repo=repo;
    }
    public Resume uploadcontent(Resume res) 
    {
        return repo.save(res);
        
    }
    public List<Resume> getAllresumes() 
    {
        return repo.findAll();
        
    }
    public Optional<Resume> getid(Long id) {
        return repo.findById(id);
    }



}
