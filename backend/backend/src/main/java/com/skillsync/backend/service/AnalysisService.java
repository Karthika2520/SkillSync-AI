
package com.skillsync.backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.dto.AnalysisRequestdto;
import com.skillsync.backend.dto.AnalysisResponsedto;
import com.skillsync.backend.dto.MatchingResult;
import com.skillsync.backend.dto.SkillEvidence;
import com.skillsync.backend.entity.Analysis;
import com.skillsync.backend.entity.Analysis.Status;
import com.skillsync.backend.entity.JobDescription;
import com.skillsync.backend.entity.Resume;
import com.skillsync.backend.repository.AnalysisRepository;
import com.skillsync.backend.repository.JobDescriptionRepository;
import com.skillsync.backend.repository.ResumeRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class AnalysisService {

    private final AnalysisRepository arepo;
    private final ResumeRepository rrepo;
    private final JobDescriptionRepository jrepo;
    private final SkillExtractionService skillser;
    private final ObjectMapper objectMapper;
    private final MatchingService mser;

    public AnalysisService(
            AnalysisRepository arepo,
            ResumeRepository rrepo,
            JobDescriptionRepository jrepo,
            SkillExtractionService skillser,
            ObjectMapper objectMapper,
            MatchingService mser) {

        this.arepo = arepo;
        this.rrepo = rrepo;
        this.jrepo = jrepo;
        this.skillser = skillser;
        this.objectMapper = objectMapper;
        this.mser = mser;
    }

    // CREATE NEW ANALYSIS
    @Transactional
    public AnalysisResponsedto createAnalysis(AnalysisRequestdto req) {

        Long resumeid = req.getResumeId();
        Long jobdescid = req.getJobdescriptionId();

        Optional<Resume> r = rrepo.findById(resumeid);

        if (r.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Resume not found with ID: " + resumeid);
        }

        Optional<JobDescription> j = jrepo.findById(jobdescid);

        if (j.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Job description not found with ID: " + jobdescid);
        }

        Analysis ans = new Analysis();

        ans.setResume(r.get());
        ans.setJob(j.get());
        ans.setStatus(Status.PENDING);

        List<SkillEvidence> resumeSkills =
                skillser.extractSkills(r.get().getExtractedText());

        List<SkillEvidence> jobSkills =
                skillser.extractSkills(j.get().getDescription());

        MatchingResult rr =
                mser.calcuateMatch(resumeSkills, jobSkills);

        ans.setMatchScore(rr.getMatchScore());
        ans.setStatus(Status.COMPLETED);

        Map<String, Object> res = new LinkedHashMap<>();

        res.put("ResumeSkills", resumeSkills);
        res.put("JobSkills", jobSkills);

        try {
            ans.setResultsJson(
                    objectMapper.writeValueAsString(res));

        } catch (JacksonException e) {
            throw new IllegalStateException(
                    "Could not save analysis results", e);
        }

        Analysis a = arepo.save(ans);

        AnalysisResponsedto dt = new AnalysisResponsedto();

        dt.setId(a.getId());
        dt.setResumeId(a.getResume().getId());
        dt.setJobdescriptionId(a.getJob().getId());
        dt.setStatus(a.getStatus());
        dt.setMatchScore(a.getMatchScore());
        dt.setCreatedAt(a.getCreatedAt());

        dt.setResumeSkills(resumeSkills);
        dt.setJobSkills(jobSkills);
        dt.setMatchingResult(rr);

        return dt;
    }

    // GET ALL PREVIOUS ANALYSES
    @Transactional(readOnly = true)
    public List<AnalysisResponsedto> getAllAnalyses() {

        List<Analysis> analyses =
                arepo.findAllByOrderByCreatedAtDesc();

        return analyses.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET ONE ANALYSIS BY ID
    @Transactional(readOnly = true)
    public AnalysisResponsedto getAnalysisById(Long analysisId) {

        Analysis analysis = arepo.findById(analysisId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Analysis not found with ID: " + analysisId));

        return convertToResponse(analysis);
    }

    // CONVERT ENTITY TO RESPONSE DTO
    


private AnalysisResponsedto convertToResponse(Analysis analysis) {

    List<SkillEvidence> resumeSkills = List.of();
    List<SkillEvidence> jobSkills = List.of();

    String resultsJson = analysis.getResultsJson();

    if (resultsJson != null && !resultsJson.isBlank()) {

        try {

            Map<String, Object> data = objectMapper.readValue(
                    resultsJson,
                    new TypeReference<Map<String, Object>>() {}
            );

            List<SkillEvidence> storedResumeSkills =
                    objectMapper.convertValue(
                            data.get("ResumeSkills"),
                            new TypeReference<List<SkillEvidence>>() {}
                    );

            List<SkillEvidence> storedJobSkills =
                    objectMapper.convertValue(
                            data.get("JobSkills"),
                            new TypeReference<List<SkillEvidence>>() {}
                    );

            if (storedResumeSkills != null) {
                resumeSkills = storedResumeSkills;
            }

            if (storedJobSkills != null) {
                jobSkills = storedJobSkills;
            }

        } catch (JacksonException e) {

            System.out.println(
                    "Invalid JSON for Analysis ID: " + analysis.getId());
        }
    } else {

        System.out.println(
                "No stored skill results for Analysis ID: "
                        + analysis.getId());
    }

    MatchingResult matchingResult =
            mser.calcuateMatch(resumeSkills, jobSkills);

    AnalysisResponsedto dto = new AnalysisResponsedto();

    dto.setId(analysis.getId());
    dto.setResumeId(analysis.getResume().getId());
    dto.setJobdescriptionId(analysis.getJob().getId());
    dto.setStatus(analysis.getStatus());
    dto.setMatchScore(analysis.getMatchScore());
    dto.setCreatedAt(analysis.getCreatedAt());

    dto.setResumeSkills(resumeSkills);
    dto.setJobSkills(jobSkills);
    dto.setMatchingResult(matchingResult);

    return dto;
}



    
}
