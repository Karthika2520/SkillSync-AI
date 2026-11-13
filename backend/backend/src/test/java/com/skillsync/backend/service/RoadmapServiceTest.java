
package com.skillsync.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.skillsync.backend.dto.*;
import com.skillsync.backend.entity.*;
import com.skillsync.backend.repository.*;

class RoadmapServiceTest {

    private AnalysisRepository arepo;
    private RoadmapRepository rrepo;
    private SkillExtractionService skillser;
    private MatchingService mser;
    private RoadmapService roadmapService;

    @BeforeEach
    void setUp() {

        arepo = mock(AnalysisRepository.class);
        rrepo = mock(RoadmapRepository.class);
        skillser = mock(SkillExtractionService.class);
        mser = mock(MatchingService.class);

        roadmapService = new RoadmapService(
                arepo, rrepo, skillser, mser
        );
    }

    // Test 1: Generate roadmap
    @Test
    void testGenerateRoadmap() {

        Analysis analysis = mock(Analysis.class);
        Resume resume = mock(Resume.class);
        JobDescription job = mock(JobDescription.class);

        when(analysis.getId()).thenReturn(1L);
        when(analysis.getResume()).thenReturn(resume);
        when(analysis.getJob()).thenReturn(job);

        when(resume.getExtractedText()).thenReturn("Java");
        when(job.getDescription()).thenReturn("Java and Docker");

        when(arepo.findById(1L))
                .thenReturn(Optional.of(analysis));

        when(rrepo.existsByAnalysis_Id(1L))
                .thenReturn(false);

        when(skillser.extractSkills(anyString()))
                .thenReturn(List.of());

        MatchingResult result = new MatchingResult(
                null,
                List.of(),
                List.of(),
                List.of(new SkillMatchItem(
                        "Docker", null, 1, "MISSING", 0.0
                )),
                "Analysis completed"
        );

        when(mser.calcuateMatch(anyList(), anyList()))
                .thenReturn(result);

        when(rrepo.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<RoadmapTaskResponse> tasks =
                roadmapService.generateRoadmap(1L, false);

        assertFalse(tasks.isEmpty());

        verify(rrepo, times(1)).saveAll(anyList());
    }

    // Test 2: Retrieve roadmap tasks
    @Test
    void testGetRoadmap() {

        RoadmapTask task = mock(RoadmapTask.class);
        Analysis analysis = mock(Analysis.class);

        when(task.getAnalysis()).thenReturn(analysis);
        when(analysis.getId()).thenReturn(1L);

        when(rrepo.findByAnalysis_IdOrderByDayNumberAscIdAsc(1L))
                .thenReturn(List.of(task));

        List<RoadmapTaskResponse> result =
                roadmapService.getRoadmap(1L);

        assertEquals(1, result.size());
    }
}
