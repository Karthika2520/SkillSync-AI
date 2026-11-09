package com.skillsync.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillsync.backend.entity.PracticeAttempt;

public interface PracticeAttemptRepository extends JpaRepository<PracticeAttempt,Long>
{
    List<PracticeAttempt> findByAnalysis_IdOrderByCreatedAtDesc(Long analysisId);
}
