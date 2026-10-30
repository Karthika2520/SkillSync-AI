package com.skillsync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class SkillMatchItem 
{
    private String skill;
    private String evidence;
    private int weight;
    private String classification;
    private double credit;

}
