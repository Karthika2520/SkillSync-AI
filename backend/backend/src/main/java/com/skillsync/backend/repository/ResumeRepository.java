package com.skillsync.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsync.backend.entity.Resume;
@Repository 
public interface ResumeRepository extends JpaRepository<Resume,Long>
{

}
