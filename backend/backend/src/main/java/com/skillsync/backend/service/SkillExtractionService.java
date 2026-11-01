
// package com.skillsync.backend.service;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.LinkedHashMap;
// import java.util.LinkedHashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// import org.springframework.stereotype.Service;


// import com.skillsync.backend.dto.SkillEvidence;

// @Service
// public class SkillExtractionService {

//     private final Map<String, List<String>> skillDictionary =
//             new LinkedHashMap<>();

//     private final GeminiSkillService geminiSkillService;
//     private final GroqSkillService groqSkillService;

//     public SkillExtractionService(GeminiSkillService geminiSkillService,GroqSkillService groqSkillService)
//         this.geminiSkillService = geminiSkillService;
//         this.groqSkillService=groqSkillService;

//         addSkill("Java", "java");
//         addSkill("Python", "python");
//         addSkill("C++", "c++");
//         addSkill("JavaScript", "javascript", "js");

//         addSkill("React", "react", "react.js");
//         addSkill("HTML", "html");
//         addSkill("CSS", "css");

//         addSkill("Spring Boot", "spring boot", "springboot");
//         addSkill("Spring Framework", "spring framework");
//         addSkill("REST APIs", "rest api", "rest apis",
//                 "restful api", "restful apis");

//         addSkill("MySQL", "mysql");
//         addSkill("SQL", "sql");
//         addSkill("PostgreSQL", "postgresql", "postgres");

//         addSkill("Data Structures", "data structures");
//         addSkill("Algorithms", "algorithms", "algorithm");
//         addSkill("OOP", "oop", "object-oriented programming",
//                 "object oriented programming");

//         addSkill("Git", "git");
//         addSkill("GitHub", "github");
//         addSkill("Docker", "docker");
//         addSkill("AWS", "aws");
//         addSkill("Operating Systems", "operating systems");
//         addSkill("DBMS", "dbms", "database management system");
//         addSkill("Computer Networks", "computer networks");
//     }

//     private void addSkill(String name, String... aliases) {
//         skillDictionary.put(name, Arrays.asList(aliases));
//     }

//     // Main method: Dictionary + Gemini extraction
//     public List<SkillEvidence> extractSkills(String text) {

//         List<SkillEvidence> dictionarySkills =
//                 extractDictionarySkills(text);

//         if (text == null || text.isBlank()) {
//             return dictionarySkills;
//         }

//         List<SkillEvidence> combinedSkills =
//                 new ArrayList<>(dictionarySkills);

//         try {
//             List<SkillEvidence> aiSkills =
//                     geminiSkillService.discoverSkills(text);

//             combinedSkills.addAll(aiSkills);

//         } catch (Exception e) {
//             // Gemini failure should not stop dictionary extraction.
//             System.err.println(
//                     "Gemini skill discovery failed: " + e.getMessage());
//         }

//         return removeDuplicates(combinedSkills);
//     }

//     // Existing dictionary-based extraction
//     private List<SkillEvidence> extractDictionarySkills(String text) {

//         List<SkillEvidence> results = new ArrayList<>();

//         if (text == null || text.isBlank()) {
//             return results;
//         }

//         String normalized =
//                 text.replaceAll("\\s+", " ").trim();

//         for (Map.Entry<String, List<String>> entry
//                 : skillDictionary.entrySet()) {

//             String name = entry.getKey();

//             for (String alias : entry.getValue()) {

//                 Pattern pattern = Pattern.compile(
//                         "(?<![A-Za-z0-9])" + Pattern.quote(alias)
//                                 + "(?![A-Za-z0-9])",
//                         Pattern.CASE_INSENSITIVE);

//                 Matcher matcher = pattern.matcher(normalized);

//                 if (matcher.find()) {

//                     String evidence =
//                             findEvidence(normalized, matcher.start());

//                     results.add(new SkillEvidence(name, evidence));
//                     break;
//                 }
//             }
//         }

//         return results;
//     }

//     // Remove duplicate skills
//     private List<SkillEvidence> removeDuplicates(
//             List<SkillEvidence> skills) {

//         Map<String, SkillEvidence> uniqueSkills =
//                 new LinkedHashMap<>();

//         for (SkillEvidence item : skills) {

//             if (item == null || item.getSkill() == null
//                     || item.getSkill().isBlank()) {
//                 continue;
//             }

//             String key = item.getSkill()
//                     .trim()
//                     .toLowerCase();

//             uniqueSkills.putIfAbsent(key, item);
//         }

//         return new ArrayList<>(uniqueSkills.values());
//     }

//     private String findEvidence(String text, int position) {

//         int start = text.lastIndexOf('.', position);
//         int end = text.indexOf('.', position);

//         start = (start == -1) ? 0 : start + 1;
//         end = (end == -1) ? text.length() : end + 1;

//         return text.substring(start, end).trim();
//     }
// }



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
public class SkillExtractionService {

    private final Map<String, List<String>> skillDictionary =
            new LinkedHashMap<>();

    private final GeminiSkillService geminiSkillService;
    private final GroqSkillService groqSkillService;

    public SkillExtractionService(
            GeminiSkillService geminiSkillService,
            GroqSkillService groqSkillService) {

        this.geminiSkillService = geminiSkillService;
        this.groqSkillService = groqSkillService;

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

    private void addSkill(String name, String... aliases) {
        skillDictionary.put(name, Arrays.asList(aliases));
    }

    // Main method: Dictionary + Gemini + Groq fallback
    public List<SkillEvidence> extractSkills(String text) {

        // Step 1: Dictionary-based extraction
        List<SkillEvidence> dictionarySkills =
                extractDictionarySkills(text);

        if (text == null || text.isBlank()) {
            return dictionarySkills;
        }

        // Initially keep dictionary results
        List<SkillEvidence> combinedSkills =
                new ArrayList<>(dictionarySkills);

        // Step 2: Try Gemini first
        try {

            System.out.println("Trying Gemini...");

            List<SkillEvidence> aiSkills =
                    geminiSkillService.discoverSkills(text);

            if (aiSkills == null || aiSkills.isEmpty()) {
                throw new IllegalStateException(
                        "Gemini returned no skills");
            }

            combinedSkills.addAll(aiSkills);

            System.out.println("Gemini succeeded!");

        } catch (Exception geminiException) {

            System.err.println(
                    "Gemini failed: " + geminiException.getMessage());

            // Step 3: If Gemini fails, try Groq
            try {

                System.out.println("Trying Groq...");

                List<SkillEvidence> groqSkills =
                        groqSkillService.discoverSkills(text);

                if (groqSkills != null) {
                    combinedSkills.addAll(groqSkills);
                }

                System.out.println("Groq succeeded!");

            } catch (Exception groqException) {

                System.err.println(
                        "Groq failed: " + groqException.getMessage());

                System.out.println(
                        "Both AI providers failed. Using dictionary results only.");
            }
        }

        // Step 4: Remove duplicate skills
        return removeDuplicates(combinedSkills);
    }

    // Dictionary-based extraction
    private List<SkillEvidence> extractDictionarySkills(String text) {

        List<SkillEvidence> results = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return results;
        }

        String normalized =
                text.replaceAll("\\s+", " ").trim();

        for (Map.Entry<String, List<String>> entry
                : skillDictionary.entrySet()) {

            String name = entry.getKey();

            for (String alias : entry.getValue()) {

                Pattern pattern = Pattern.compile(
                        "(?<![A-Za-z0-9])" + Pattern.quote(alias)
                                + "(?![A-Za-z0-9])",
                        Pattern.CASE_INSENSITIVE);

                Matcher matcher = pattern.matcher(normalized);

                if (matcher.find()) {

                    String evidence =
                            findEvidence(normalized, matcher.start());

                    results.add(new SkillEvidence(name, evidence));
                    break;
                }
            }
        }

        return results;
    }

    // Remove duplicate skills
    private List<SkillEvidence> removeDuplicates(
            List<SkillEvidence> skills) {

        Map<String, SkillEvidence> uniqueSkills =
                new LinkedHashMap<>();

        for (SkillEvidence item : skills) {

            if (item == null || item.getSkill() == null
                    || item.getSkill().isBlank()) {
                continue;
            }

            String key = item.getSkill()
                    .trim()
                    .toLowerCase();

            uniqueSkills.putIfAbsent(key, item);
        }

        return new ArrayList<>(uniqueSkills.values());
    }

    // Extract the sentence containing the matched skill
    private String findEvidence(String text, int position) {

        int start = text.lastIndexOf('.', position);
        int end = text.indexOf('.', position);

        start = (start == -1) ? 0 : start + 1;
        end = (end == -1) ? text.length() : end + 1;

        return text.substring(start, end).trim();
    }
}
