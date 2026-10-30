package com.skillsync.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.skillsync.backend.dto.MatchingResult;
import com.skillsync.backend.dto.SkillEvidence;
import com.skillsync.backend.dto.SkillMatchItem;

@Service 
public class MatchingService 
{
    // private SkillExtractionService skillser;
    // public MatchingService(SkillExtractionService s)
    // {
    //     this.skillser=s;
    // }

    // stores the related skills
    private final Map<String,List<String>> relatedskills=Map.of(
        "spring boot",List.of("spring framework"),
        "spring framework",List.of("spring boot"),
        "mysql", List.of("sql"),
        "postgresql", List.of("sql"),
        "react", List.of("javascript"),
        "javascript",List.of("react")
    );
    
   
    public MatchingResult calcuateMatch(List<SkillEvidence> resumeskills,List<SkillEvidence> jobskills)
    {

         List<SkillMatchItem> matchedSkills=new ArrayList<>();
         List<SkillMatchItem> partialSkills=new ArrayList<>();
         List<SkillMatchItem> missingSkills=new ArrayList<>();

        if(jobskills==null || jobskills.isEmpty())
        {
            return new MatchingResult(null,matchedSkills,partialSkills,missingSkills,"No recognized job requirements found");
        }
        if(resumeskills==null)
        {
            resumeskills=new ArrayList<>();
        }

        //nomalize the resume skills
        Map<String,String> resumeEvidence=new LinkedHashMap<>();
        for(SkillEvidence sk:resumeskills)
        {
            if(sk!=null && sk.getSkill()!=null)
            {
                String key=normalize(sk.getSkill());
                if(!key.isBlank())
                {
                    resumeEvidence.putIfAbsent(key,sk.getEvidence());
                }
            }
        }

        Set<String> processedRequirements=new HashSet<>();
        double earnedweight=0;
        int totalweight=0;

        for(SkillEvidence j:jobskills)
        {
            if(j==null ||j.getSkill()==null)
            continue;

            String key=normalize(j.getSkill());
            if(key.isBlank() || !processedRequirements.add(key))
            continue;

            int weight=getweight(key);
            totalweight+=weight;

            String evidence=resumeEvidence.get(key);

            if(evidence!=null)
            {
                matchedSkills.add(new SkillMatchItem(key,evidence,weight,"MATCHED",1.0));
            
            earnedweight+=weight;
            }
            else{

                String relatedEvidence=findrelatedEvidence(key, resumeEvidence);
                if(relatedEvidence!=null)
                {
                    partialSkills.add(new SkillMatchItem(key,relatedEvidence,weight,"PARTIAL",0.5));
                    earnedweight+=weight*0.5;
                }
                else{
                    missingSkills.add(new SkillMatchItem(key,null,weight,"MISSING",0.0));
                }

            }
        }

        if(totalweight==0)
        {
            return new MatchingResult(null,matchedSkills,partialSkills,missingSkills,"No valid recognized job requiremnetsfound");
        }

        BigDecimal score=BigDecimal.valueOf(earnedweight*100/totalweight).setScale(2,RoundingMode.HALF_UP);
        return new MatchingResult(score,matchedSkills,partialSkills,missingSkills,"Analysis completed");       
       
    }


     private String normalize(String skill)
        {
            return skill.trim().toLowerCase();
        }

    private int getweight(String skill)
    {
        if(skill.equals("java")||skill.equals("spring boot"))
        {
            return 3;
        }
        else if(skill.equals("sql"))
        {
            return 2;
        }
        else if(skill.equals("git")||skill.equals("communication"))
        {
            return 1;
        }
        return 1;
    }

    private String findrelatedEvidence(String requiredskill,Map<String,String>resumeEvidence)
    {
        List<String> related =relatedskills.get(requiredskill);

        if(related==null)
        {
            return null;
        }
        for(String r:related)
        {
            if(resumeEvidence.containsKey(r))
            {
                return resumeEvidence.get(r);
            }
        }
        return null;
    }



}
