package com.skillsync.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsync.backend.entity.savedJob;
@Repository 
public interface SavedJobRepository extends JpaRepository<savedJob,Long>
{

} 
