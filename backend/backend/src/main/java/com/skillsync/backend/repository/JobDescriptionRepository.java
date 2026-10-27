package com.skillsync.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillsync.backend.entity.JobDescription;

public interface JobDescriptionRepository extends JpaRepository<JobDescription,Long>
{

}
