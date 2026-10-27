package com.skillsync.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillsync.backend.entity.JobDescription;
import com.skillsync.backend.repository.JobDescriptionRepository;

@Service 
public class JobDescriptionService 
{
    private final JobDescriptionRepository repo;
    public JobDescriptionService(JobDescriptionRepository repo)
    {
        this.repo=repo;
    }
    public List<JobDescription> getall() {
        return repo.findAll();
    }
    public JobDescription create(JobDescription job) {
        return repo.save(job);
    }

    

}
