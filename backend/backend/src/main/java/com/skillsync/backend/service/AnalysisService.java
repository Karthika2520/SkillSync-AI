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
import com.skillsync.backend.dto.SkillEvidence;
import com.skillsync.backend.entity.Analysis;
import com.skillsync.backend.entity.JobDescription;
import com.skillsync.backend.entity.Resume;
import com.skillsync.backend.entity.Analysis.Status;
import com.skillsync.backend.repository.AnalysisRepository;
import com.skillsync.backend.repository.JobDescriptionRepository;
import com.skillsync.backend.repository.ResumeRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

@Service 
public class AnalysisService 
{
    private final AnalysisRepository arepo;
    private final ResumeRepository rrepo;
    private final JobDescriptionRepository jrepo;
    private final SkillExtractionService skillser;
    private final ObjectMapper objectMapper;
    public AnalysisService(AnalysisRepository arepo,ResumeRepository rrepo,JobDescriptionRepository jrepo,SkillExtractionService skillser,ObjectMapper objectMapper)
    {
        this.arepo=arepo;
        this.rrepo=rrepo;
        this.jrepo=jrepo;
        this.skillser=skillser;
         this.objectMapper = objectMapper;

    }
    @Transactional 
    public AnalysisResponsedto createAnalysis(AnalysisRequestdto req)
    {
        Long resumeid=req.getResumeId();
        Long jobdescid=req.getJobdescriptionId();

      Optional<Resume> r=rrepo.findById(resumeid);
      if(r.isEmpty()){
        throw new  ResponseStatusException(HttpStatus.NOT_FOUND,"Resume not found with ID: "+req.getResumeId());
      }

      Optional<JobDescription> j=jrepo.findById(jobdescid);
      if(j.isEmpty()){
        throw new  ResponseStatusException(HttpStatus.NOT_FOUND,"Job description not found with ID: "+req.getJobdescriptionId());
      }


      Analysis ans=new Analysis();
      ans.setResume(r.get());
      ans.setJob(j.get());
      ans.setStatus(Status.PENDING);

     


      List<SkillEvidence> resumeSkills=skillser.extractSkills(r.get().getExtractedText());
      List<SkillEvidence> jobSkills=skillser.extractSkills(j.get().getDescription());


      Map<String,Object> res=new LinkedHashMap<>();
      res.put("ResumeSkills",resumeSkills);
      res.put("JobSkills",jobSkills);

      try {
    ans.setResultsJson(objectMapper.writeValueAsString(res));
} catch (JacksonException e) {
    throw new IllegalStateException("Could not save analysis results", e);
}

      
       Analysis a=arepo.save(ans);
      
      AnalysisResponsedto dt=new AnalysisResponsedto();
      dt.setId(a.getId());
      dt.setResumeId(a.getResume().getId());
      dt.setJobdescriptionId(a.getJob().getId());
      dt.setStatus(a.getStatus());
      dt.setMatchScore(a.getMatchScore());
      dt.setCreatedAt(a.getCreatedAt());
      dt.setResumeSkills(resumeSkills);
      dt.setJobSkills(jobSkills);

      return dt;



    }


}
