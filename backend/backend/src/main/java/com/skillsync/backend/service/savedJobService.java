package com.skillsync.backend.service;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.skillsync.backend.entity.savedJob;
import com.skillsync.backend.repository.SavedJobRepository;

@Service 
public class savedJobService 
{
    private final SavedJobRepository repo;
    public savedJobService(SavedJobRepository repo)
    {
        this.repo=repo;
    }
    public List<savedJob> getall() 
    {
        return repo.findAll();
        
    }
    public savedJob savejob(savedJob job) 
    {
        return repo.save(job);
        
    }
    public Optional<savedJob> deleteid(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteid'");
    }



}
