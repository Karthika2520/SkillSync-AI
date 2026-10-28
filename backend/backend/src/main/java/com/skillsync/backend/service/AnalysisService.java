package com.skillsync.backend.service;


import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.skillsync.backend.dto.AnalysisRequestdto;
import com.skillsync.backend.dto.AnalysisResponsedto;
import com.skillsync.backend.entity.Analysis;
import com.skillsync.backend.entity.JobDescription;
import com.skillsync.backend.entity.Resume;
import com.skillsync.backend.entity.Analysis.Status;
import com.skillsync.backend.repository.AnalysisRepository;
import com.skillsync.backend.repository.JobDescriptionRepository;
import com.skillsync.backend.repository.ResumeRepository;

@Service 
public class AnalysisService 
{
    private final AnalysisRepository arepo;
    private final ResumeRepository rrepo;
    private final JobDescriptionRepository jrepo;
    public AnalysisService(AnalysisRepository arepo,ResumeRepository rrepo,JobDescriptionRepository jrepo)
    {
        this.arepo=arepo;
        this.rrepo=rrepo;
        this.jrepo=jrepo;
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

      Analysis a=arepo.save(ans);

      AnalysisResponsedto dt=new AnalysisResponsedto();
      dt.setId(a.getId());
      dt.setResumeId(a.getResume().getId());
      dt.setJobdescriptionId(a.getJob().getId());
      dt.setStatus(a.getStatus());
      dt.setMatchScore(a.getMatchScore());
      dt.setCreatedAt(a.getCreatedAt());

      return dt;



    }


}
