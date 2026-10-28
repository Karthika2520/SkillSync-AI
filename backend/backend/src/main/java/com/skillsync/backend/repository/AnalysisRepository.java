package com.skillsync.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsync.backend.entity.Analysis;

@Repository 
public interface AnalysisRepository extends JpaRepository<Analysis,Long>
{


}
