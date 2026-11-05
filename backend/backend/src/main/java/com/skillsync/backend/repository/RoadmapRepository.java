package com.skillsync.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillsync.backend.entity.RoadmapTask;

public interface RoadmapRepository extends JpaRepository<RoadmapTask,Long>
{
     List<RoadmapTask> findByAnalysis_IdOrderByDayNumberAscIdAsc(Long analysisId);
    void deleteByAnalysis_Id(Long analysisId);
    boolean existsByAnalysis_Id(Long analysisId);


}
