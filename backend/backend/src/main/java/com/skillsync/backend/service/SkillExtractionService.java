
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

    loadSkillDictionary();
}

private void loadSkillDictionary() {

    // 1. Programming Languages
    addSkill("Java", "java", "core java");
    addSkill("Python", "python", "python3");
    addSkill("C", "c language", "programming in c");
    addSkill("C++", "c++", "cpp");
    addSkill("C#", "c#", "c sharp", "csharp");
    addSkill("JavaScript", "javascript", "js", "ecmascript");
    addSkill("TypeScript", "typescript", "ts");
    addSkill("Go", "golang", "go language");
    addSkill("Rust", "rust language");
    addSkill("Kotlin", "kotlin");
    addSkill("Swift", "swift language");
    addSkill("PHP", "php");
    addSkill("Ruby", "ruby");
    addSkill("R", "r programming", "r language");
    addSkill("Scala", "scala");
    addSkill("Dart", "dart");
    addSkill("SQL", "sql", "structured query language");
    addSkill("Bash", "bash scripting", "shell scripting");
    addSkill("PowerShell", "powershell scripting");

    // 2. Frontend Development
    addSkill("HTML", "html", "html5");
    addSkill("CSS", "css", "css3");
    addSkill("React", "react", "react.js", "reactjs");
    addSkill("Angular", "angular", "angular.js", "angularjs");
    addSkill("Vue.js", "vue", "vue.js", "vuejs");
    addSkill("Next.js", "next.js", "nextjs");
    addSkill("Bootstrap", "bootstrap");
    addSkill("Tailwind CSS", "tailwind", "tailwind css");
    addSkill("Material UI", "material ui", "mui");
    addSkill("jQuery", "jquery");
    addSkill("Redux", "redux", "redux toolkit");
    addSkill("Webpack", "webpack");
    addSkill("Vite", "vite");
    addSkill("SASS", "sass", "scss");
    addSkill("Responsive Web Design", "responsive design",
            "responsive web design");

    // 3. Backend Development
    addSkill("Spring Boot", "spring boot", "springboot");
    addSkill("Spring Framework", "spring framework");
    addSkill("Spring MVC", "spring mvc");
    addSkill("Spring Security", "spring security");
    addSkill("Hibernate", "hibernate");
    addSkill("JPA", "jpa", "java persistence api");
    addSkill("Node.js", "node.js", "nodejs", "node js");
    addSkill("Express.js", "express.js", "expressjs", "express");
    addSkill("Django", "django");
    addSkill("Flask", "flask");
    addSkill("FastAPI", "fastapi", "fast api");
    addSkill("ASP.NET", "asp.net", "aspnet");
    addSkill("Laravel", "laravel");
    addSkill("Ruby on Rails", "ruby on rails", "rails");
    addSkill("REST APIs", "rest api", "rest apis",
            "restful api", "restful apis",
            "restful web services");
    addSkill("GraphQL", "graphql");
    addSkill("Microservices", "microservices",
            "microservice architecture");
    addSkill("WebSockets", "websocket", "websockets");
    addSkill("gRPC", "grpc");

    // 4. Databases
    addSkill("MySQL", "mysql");
    addSkill("PostgreSQL", "postgresql", "postgres");
    addSkill("MongoDB", "mongodb", "mongo db");
    addSkill("Oracle Database", "oracle database",
            "oracle db", "oracle sql");
    addSkill("Microsoft SQL Server", "sql server",
            "microsoft sql server", "mssql");
    addSkill("SQLite", "sqlite");
    addSkill("Redis", "redis");
    addSkill("Cassandra", "cassandra", "apache cassandra");
    addSkill("DynamoDB", "dynamodb", "amazon dynamodb");
    addSkill("Firebase", "firebase", "google firebase");
    addSkill("MariaDB", "mariadb");
    addSkill("Elasticsearch", "elasticsearch",
            "elastic search");
    addSkill("Neo4j", "neo4j");
    addSkill("Database Management", "database management");
    addSkill("Database Design", "database design");
    addSkill("Database Optimization", "database optimization",
            "query optimization");

    // 5. Cloud Platforms
    addSkill("AWS", "aws", "amazon web services");
    addSkill("Microsoft Azure", "azure", "microsoft azure");
    addSkill("Google Cloud Platform", "gcp",
            "google cloud", "google cloud platform");
    addSkill("AWS EC2", "ec2", "amazon ec2");
    addSkill("AWS S3", "s3", "amazon s3");
    addSkill("AWS Lambda", "aws lambda", "lambda functions");
    addSkill("Azure DevOps", "azure devops");
    addSkill("Google Cloud Storage", "google cloud storage");

    // 6. DevOps and Deployment
    addSkill("Docker", "docker", "containerization");
    addSkill("Kubernetes", "kubernetes", "k8s");
    addSkill("Jenkins", "jenkins");
    addSkill("GitHub Actions", "github actions");
    addSkill("GitLab CI/CD", "gitlab ci", "gitlab ci/cd");
    addSkill("CI/CD", "ci/cd", "continuous integration",
            "continuous delivery", "continuous deployment");
    addSkill("Terraform", "terraform");
    addSkill("Ansible", "ansible");
    addSkill("Linux", "linux", "linux administration");
    addSkill("Nginx", "nginx");
    addSkill("Apache", "apache server");
    addSkill("Maven", "maven", "apache maven");
    addSkill("Gradle", "gradle");
    addSkill("Tomcat", "apache tomcat", "tomcat");

    // 7. Computer Science Fundamentals
    addSkill("Data Structures", "data structures",
            "data structure");
    addSkill("Algorithms", "algorithms", "algorithm");
    addSkill("OOP", "oop", "object-oriented programming",
            "object oriented programming");
    addSkill("Operating Systems", "operating systems",
            "operating system", "os concepts");
    addSkill("DBMS", "dbms", "database management system",
            "database management systems");
    addSkill("Computer Networks", "computer networks",
            "networking concepts");
    addSkill("Software Engineering", "software engineering");
    addSkill("System Design", "system design",
            "low level design", "high level design",
            "lld", "hld");
    addSkill("Design Patterns", "design patterns");
    addSkill("Multithreading", "multithreading",
            "multi threading", "concurrency");
    addSkill("Distributed Systems", "distributed systems",
            "distributed computing");
    addSkill("Object-Oriented Design", "object oriented design",
            "ood");
    addSkill("Problem Solving", "problem solving",
            "analytical problem solving");

    // 8. AI and Machine Learning
    addSkill("Artificial Intelligence", "artificial intelligence");
    addSkill("Machine Learning", "machine learning", "ml");
    addSkill("Deep Learning", "deep learning");
    addSkill("Natural Language Processing", "natural language processing",
            "nlp");
    addSkill("Generative AI", "generative ai", "genai");
    addSkill("Large Language Models", "large language models",
            "llm", "llms");
    addSkill("Prompt Engineering", "prompt engineering");
    addSkill("TensorFlow", "tensorflow");
    addSkill("PyTorch", "pytorch");
    addSkill("Scikit-learn", "scikit-learn", "sklearn");
    addSkill("Keras", "keras");
    addSkill("OpenCV", "opencv");
    addSkill("Hugging Face", "hugging face");
    addSkill("LangChain", "langchain");
    addSkill("Vector Databases", "vector database",
            "vector databases");
    addSkill("RAG", "retrieval augmented generation",
            "retrieval-augmented generation");
    addSkill("Neural Networks", "neural networks",
            "neural network");
    addSkill("Computer Vision", "computer vision");

    // 9. Data Analytics and Data Engineering
    addSkill("Pandas", "pandas");
    addSkill("NumPy", "numpy");
    addSkill("Matplotlib", "matplotlib");
    addSkill("Power BI", "power bi");
    addSkill("Tableau", "tableau");
    addSkill("Apache Spark", "apache spark", "spark");
    addSkill("Apache Kafka", "apache kafka", "kafka");
    addSkill("Apache Airflow", "apache airflow", "airflow");
    addSkill("ETL", "etl", "extract transform load");
    addSkill("Data Analysis", "data analysis");
    addSkill("Data Visualization", "data visualization");
    addSkill("Data Mining", "data mining");
    addSkill("Data Warehousing", "data warehousing");
    addSkill("Statistics", "statistics", "statistical analysis");

    // 10. Testing
    addSkill("JUnit", "junit", "junit 5");
    addSkill("Mockito", "mockito");
    addSkill("Selenium", "selenium");
    addSkill("Cypress", "cypress");
    addSkill("Playwright", "playwright");
    addSkill("Postman", "postman");
    addSkill("REST Assured", "rest assured");
    addSkill("Jest", "jest");
    addSkill("PyTest", "pytest", "py test");
    addSkill("Unit Testing", "unit testing");
    addSkill("Integration Testing", "integration testing");
    addSkill("API Testing", "api testing");
    addSkill("Test Automation", "test automation",
            "automated testing");

    // 11. Development Tools and Version Control
    addSkill("Git", "git", "version control");
    addSkill("GitHub", "github");
    addSkill("GitLab", "gitlab");
    addSkill("Bitbucket", "bitbucket");
    addSkill("Jira", "jira");
    addSkill("IntelliJ IDEA", "intellij", "intellij idea");
    addSkill("Visual Studio Code", "visual studio code",
            "vs code", "vscode");
    addSkill("Eclipse", "eclipse ide");
    addSkill("Figma", "figma");
    addSkill("Jupyter Notebook", "jupyter", "jupyter notebook");

    // 12. Security and Networking
    addSkill("Cybersecurity", "cybersecurity", "cyber security");
    addSkill("Authentication", "authentication");
    addSkill("Authorization", "authorization");
    addSkill("JWT", "jwt", "json web token",
            "json web tokens");
    addSkill("OAuth", "oauth", "oauth 2.0");
    addSkill("Spring Security", "spring security");
    addSkill("Encryption", "encryption");
    addSkill("TCP/IP", "tcp/ip", "tcp ip");
    addSkill("HTTP", "http", "hypertext transfer protocol");
    addSkill("HTTPS", "https");
    addSkill("DNS", "dns", "domain name system");
    addSkill("Load Balancing", "load balancing",
            "load balancer");

    // 13. Software Development Concepts
    addSkill("Agile", "agile methodology", "agile");
    addSkill("Scrum", "scrum");
    addSkill("Object-Oriented Programming", "object oriented programming",
            "object-oriented programming");
    addSkill("MVC Architecture", "mvc", "model view controller");
    addSkill("Clean Code", "clean code");
    addSkill("Debugging", "debugging");
    addSkill("Code Review", "code review", "code reviews");
    addSkill("API Development", "api development");
    addSkill("Full Stack Development", "full stack development",
            "full-stack development");
    addSkill("Backend Development", "backend development",
            "back-end development");
    addSkill("Frontend Development", "frontend development",
            "front-end development");
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
