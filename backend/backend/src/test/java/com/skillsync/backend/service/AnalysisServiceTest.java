
package com.skillsync.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.dto.*;
import com.skillsync.backend.entity.*;

import com.skillsync.backend.repository.*;
import com.skillsync.backend.entity.Analysis.Status;

import tools.jackson.databind.ObjectMapper;

class AnalysisServiceTest {

    private AnalysisRepository arepo;
    private ResumeRepository rrepo;
    private JobDescriptionRepository jrepo;
    private SkillExtractionService skillser;
    private MatchingService mser;
    private AnalysisService analysisService;

    @BeforeEach
    void setUp() {

        arepo = mock(AnalysisRepository.class);
        rrepo = mock(ResumeRepository.class);
        jrepo = mock(JobDescriptionRepository.class);
        skillser = mock(SkillExtractionService.class);
        mser = mock(MatchingService.class);

        analysisService = new AnalysisService(
                arepo, rrepo, jrepo,
                skillser, new ObjectMapper(), mser
        );
    }

    // Test 1: Create analysis successfully
    @Test
    void testCreateAnalysis() {

        Resume resume = new Resume();
        resume.setId(1L);
        resume.setExtractedText("Java and Python");

        JobDescription job = new JobDescription();
        job.setId(2L);
        job.setDescription("Java required");

        AnalysisRequestdto request =
                new AnalysisRequestdto(1L, 2L);

        when(rrepo.findById(1L))
                .thenReturn(Optional.of(resume));

        when(jrepo.findById(2L))
                .thenReturn(Optional.of(job));

        when(skillser.extractSkills(anyString()))
                .thenReturn(List.of(
                        new SkillEvidence("Java", "Java experience")
                ));

        MatchingResult matchingResult = new MatchingResult(
                null, List.of(), List.of(), List.of(),
                "Analysis completed"
        );

        when(mser.calcuateMatch(anyList(), anyList()))
                .thenReturn(matchingResult);

        when(arepo.save(any(Analysis.class)))
                .thenAnswer(invocation -> {
                    Analysis saved = invocation.getArgument(0);
                    saved.setId(10L);
                    return saved;
                });

        AnalysisResponsedto result =
                analysisService.createAnalysis(request);

        assertEquals(10L, result.getId());
        assertEquals(Status.COMPLETED, result.getStatus());

        verify(arepo, times(1)).save(any(Analysis.class));
    }

    // Test 2: Invalid resume ID
    @Test
    void testInvalidResumeId() {

        AnalysisRequestdto request =
                new AnalysisRequestdto(99L, 2L);

        when(rrepo.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> analysisService.createAnalysis(request));
    }

    // Test 3: Invalid job description ID
    @Test
    void testInvalidJobId() {

        Resume resume = new Resume();
        resume.setId(1L);

        AnalysisRequestdto request =
                new AnalysisRequestdto(1L, 99L);

        when(rrepo.findById(1L))
                .thenReturn(Optional.of(resume));

        when(jrepo.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> analysisService.createAnalysis(request));
    }
}
