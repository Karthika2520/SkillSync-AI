package com.skillsync.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsync.backend.entity.JobDescription;
@Repository 
public interface JobDescriptionRepository extends JpaRepository<JobDescription,Long>
{

}
