package com.skillsync.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.skillsync.backend.dto.SkillEvidence;

@Service 
public class SkillExtractionService 
{
    private final Map<String, List<String>> skillDictionary=new LinkedHashMap<>();

      public SkillExtractionService() {
        addSkill("Java", "java");
        addSkill("Python", "python");
        addSkill("C++", "c++");
        addSkill("JavaScript", "javascript", "js");

        addSkill("React", "react", "react.js");
        addSkill("HTML", "html");
        addSkill("CSS", "css");

        addSkill("Spring Boot", "spring boot", "springboot");
        addSkill("Spring Framework", "spring framework");
        addSkill("REST APIs", "rest api", "rest apis",
                "restful api", "restful apis");

        addSkill("MySQL", "mysql");
        addSkill("SQL", "sql");
        addSkill("PostgreSQL", "postgresql", "postgres");

        addSkill("Data Structures", "data structures");
        addSkill("Algorithms", "algorithms", "algorithm");
        addSkill("OOP", "oop", "object-oriented programming",
                "object oriented programming");

        addSkill("Git", "git");
        addSkill("GitHub", "github");
        addSkill("Docker", "docker");
        addSkill("AWS", "aws");
        addSkill("Operating Systems", "operating systems");
        addSkill("DBMS", "dbms", "database management system");
        addSkill("Computer Networks", "computer networks");
    }


    private void addSkill(String name,String... aliases)
    {
        skillDictionary.put(name,Arrays.asList(aliases));
    }

    public List<SkillEvidence> extractSkills(String text)
    {
        List<SkillEvidence> results=new ArrayList<>();

        if(text==null || text.isBlank())
        {
            return results;
        }

        String normalized=text.replaceAll("\\s+"," ").trim();

        for(Map.Entry<String,List<String>> entry:skillDictionary.entrySet())
        {
            String name=entry.getKey();

            for(String alias:entry.getValue())
            {
                Pattern pattern =Pattern.compile("(?<![A-Za-z0-9])"+Pattern.quote(alias)+"(?![A-Za-z0-9])",Pattern.CASE_INSENSITIVE); //search rule for matcher
                Matcher matcher=pattern.matcher(normalized);
                if(matcher.find())
                {
                    String evidence=findEvidence(normalized,matcher.start());
                
                results.add(new SkillEvidence(name,evidence));
                break;
                }

            }
        }
        return results;

    }

    private String findEvidence(String txt,int position)
   {
    int start=txt.lastIndexOf('.',position); //finds the nearest forward fullstop
    int end=txt.indexOf('.',position); //nearest backward full stop
    start=(start==-1)?0:start+1; // if no '.' consider the starting index -> 0,else:id '.' exist start from after '.' (so +1)
    end=(end==-1)?txt.length():end+1; 
    return txt.substring(start,end).trim();

   }



    

}




// skillDictionary: stores official skill names and their aliases.



// addSkill(): lets you add new skills without rewriting the extraction algorithm.



// Pattern.CASE_INSENSITIVE: matches JAVA, Java and java.



// Pattern.quote(): treats special characters in skill names, such as C++, literally.



// The boundary checks prevent matching Java inside JavaScript and SQL inside NoSQL.



// break: prevents the same canonical skill from being added multiple times.



// findEvidence(): saves the sentence containing the first detected occurrence.

//----------------------------------------------
// I know Java and Python. I built a project using Spring Boot and MySQL.

// [
//   {
//     "skill": "Java",
//     "evidence": "I know Java and Python."
//   },
//   {
//     "skill": "Python",
//     "evidence": "I know Java and Python."
//   },
//   {
//     "skill": "Spring Boot",
//     "evidence": "I built a project using Spring Boot and MySQL."
//   },
//   {
//     "skill": "MySQL",
//     "evidence": "I built a project using Spring Boot and MySQL."
//   }
// ]


