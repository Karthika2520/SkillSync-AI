
package com.skillsync.backend.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SkillMatchingConfig {

    private final Map<String, Integer> skillWeights = new HashMap<>();

    private final Map<String, List<String>> relatedSkills =
            new HashMap<>();

    public SkillMatchingConfig() {

        loadWeights();
        loadRelatedSkills();
    }

    private void addWeight(int weight, String... skills) {

        for (String skill : skills) {
            skillWeights.put(normalize(skill), weight);
        }
    }

    private void addRelated(String requiredSkill,
                            String... alternatives) {

        relatedSkills.put(
                normalize(requiredSkill),
                java.util.Arrays.stream(alternatives)
                        .map(this::normalize)
                        .toList()
        );
    }

    private String normalize(String skill) {
        return skill.trim().toLowerCase();
    }

    // ==================================
    // SKILL IMPORTANCE WEIGHTS
    // ==================================

    private void loadWeights() {

        // Weight 3: Core programming languages
        addWeight(3,
                "Java", "Python", "C", "C++", "C#",
                "JavaScript", "TypeScript", "Go", "Rust",
                "Kotlin", "Swift", "PHP", "Ruby",
                "Scala", "Dart");

        // Weight 3: Backend development
        addWeight(3,
                "Spring Boot", "Spring Framework",
                "Spring MVC", "Spring Security",
                "Node.js", "Express.js", "Django",
                "Flask", "FastAPI", "ASP.NET",
                "Laravel", "Ruby on Rails",
                "Hibernate", "JPA",
                "REST APIs", "GraphQL",
                "Microservices", "gRPC");

        // Weight 2: Frontend development
        addWeight(2,
                "HTML", "CSS", "React", "Angular",
                "Vue.js", "Next.js", "Bootstrap",
                "Tailwind CSS", "Material UI",
                "jQuery", "Redux", "Webpack",
                "Vite", "SASS",
                "Responsive Web Design");

        // Weight 2: Databases
        addWeight(2,
                "MySQL", "PostgreSQL", "MongoDB",
                "Oracle Database", "Microsoft SQL Server",
                "SQLite", "Redis", "Cassandra",
                "DynamoDB", "Firebase", "MariaDB",
                "Elasticsearch", "Neo4j",
                "Database Design", "Database Optimization");

        // Weight 2: Cloud platforms
        addWeight(2,
                "AWS", "Microsoft Azure",
                "Google Cloud Platform",
                "AWS EC2", "AWS S3", "AWS Lambda",
                "Azure DevOps", "Google Cloud Storage");

        // Weight 2: DevOps
        addWeight(2,
                "Docker", "Kubernetes", "Jenkins",
                "GitHub Actions", "GitLab CI/CD",
                "CI/CD", "Terraform", "Ansible",
                "Linux", "Nginx", "Maven",
                "Gradle", "Tomcat");

        // Weight 3: Computer science fundamentals
        addWeight(3,
                "Data Structures", "Algorithms",
                "OOP", "Operating Systems",
                "DBMS", "Computer Networks",
                "Software Engineering",
                "System Design", "Design Patterns",
                "Multithreading", "Distributed Systems",
                "Object-Oriented Design",
                "Problem Solving");

        // Weight 3: AI and machine learning
        addWeight(3,
                "Artificial Intelligence",
                "Machine Learning",
                "Deep Learning",
                "Natural Language Processing",
                "Generative AI",
                "Large Language Models",
                "TensorFlow", "PyTorch",
                "Scikit-learn", "Keras",
                "OpenCV", "Hugging Face",
                "LangChain", "Vector Databases",
                "RAG", "Neural Networks",
                "Computer Vision");

        // Weight 2: Data analytics
        addWeight(2,
                "Pandas", "NumPy", "Matplotlib",
                "Power BI", "Tableau",
                "Apache Spark", "Apache Kafka",
                "Apache Airflow", "ETL",
                "Data Analysis", "Data Visualization",
                "Data Mining", "Data Warehousing",
                "Statistics");

        // Weight 2: Testing
        addWeight(2,
                "JUnit", "Mockito", "Selenium",
                "Cypress", "Playwright", "Postman",
                "REST Assured", "Jest", "PyTest",
                "Unit Testing", "Integration Testing",
                "API Testing", "Test Automation");

        // Weight 1: Development tools
        addWeight(1,
                "Git", "GitHub", "GitLab",
                "Bitbucket", "Jira",
                "IntelliJ IDEA",
                "Visual Studio Code",
                "Eclipse", "Figma",
                "Jupyter Notebook",
                "Maven", "Gradle");

        // Weight 2: Security and networking
        addWeight(2,
                "Cybersecurity", "Authentication",
                "Authorization", "JWT", "OAuth",
                "Encryption", "TCP/IP", "HTTP",
                "HTTPS", "DNS", "Load Balancing");

        // Weight 1: General software development concepts
        addWeight(1,
                "Agile", "Scrum", "MVC Architecture",
                "Clean Code", "Debugging",
                "Code Review", "API Development",
                "Full Stack Development",
                "Backend Development",
                "Frontend Development",
                "Bash", "PowerShell");

        // Weight 2: General database and SQL
        addWeight(2,
                "SQL", "Database Management");
    }

    // ==================================
    // RELATED SKILLS
    // ==================================

    private void loadRelatedSkills() {

        // Java ecosystem
        addRelated("Spring Boot",
                "Spring Framework", "Spring MVC");

        addRelated("Spring Framework",
                "Spring Boot", "Spring MVC");

        addRelated("Spring MVC",
                "Spring Boot", "Spring Framework");

        addRelated("Hibernate", "JPA");
        addRelated("JPA", "Hibernate");

        // Frontend ecosystem
        addRelated("React", "JavaScript");
        addRelated("Angular", "TypeScript", "JavaScript");
        addRelated("Vue.js", "JavaScript");
        addRelated("Next.js", "React", "JavaScript");
        addRelated("Redux", "React");

        // Backend ecosystem
        addRelated("Express.js", "Node.js", "JavaScript");
        addRelated("Node.js", "JavaScript");
        addRelated("Django", "Python");
        addRelated("Flask", "Python");
        addRelated("FastAPI", "Python");
        addRelated("Laravel", "PHP");
        addRelated("Ruby on Rails", "Ruby");

        // Database ecosystem
        addRelated("MySQL", "SQL");
        addRelated("PostgreSQL", "SQL");
        addRelated("Oracle Database", "SQL");
        addRelated("Microsoft SQL Server", "SQL");
        addRelated("MariaDB", "MySQL", "SQL");
        addRelated("SQLite", "SQL");

        // Cloud ecosystem
        addRelated("AWS EC2", "AWS");
        addRelated("AWS S3", "AWS");
        addRelated("AWS Lambda", "AWS");
        addRelated("Azure DevOps", "Microsoft Azure");
        addRelated("Google Cloud Storage",
                "Google Cloud Platform");

        // DevOps ecosystem
        addRelated("Kubernetes", "Docker");
        addRelated("GitHub Actions", "CI/CD");
        addRelated("GitLab CI/CD", "CI/CD");
        addRelated("Jenkins", "CI/CD");
        addRelated("Terraform", "AWS", "Microsoft Azure");
        addRelated("Ansible", "Linux");

        // AI / ML ecosystem
        addRelated("Deep Learning", "Machine Learning");
        addRelated("TensorFlow", "Deep Learning", "Machine Learning");
        addRelated("PyTorch", "Deep Learning", "Machine Learning");
        addRelated("Keras", "TensorFlow", "Deep Learning");
        addRelated("Scikit-learn", "Python", "Machine Learning");
        addRelated("Natural Language Processing",
                "Machine Learning");
        addRelated("Computer Vision", "Deep Learning");
        addRelated("Generative AI", "Large Language Models");
        addRelated("RAG", "Large Language Models");
        addRelated("LangChain", "Large Language Models");
        addRelated("Vector Databases", "RAG");

        // Data engineering
        addRelated("Apache Spark", "Python");
        addRelated("Apache Kafka", "Distributed Systems");
        addRelated("Apache Airflow", "ETL");
        addRelated("Pandas", "Python");
        addRelated("NumPy", "Python");

        // Testing
        addRelated("Mockito", "JUnit");
        addRelated("Cypress", "JavaScript");
        addRelated("Jest", "JavaScript");
        addRelated("PyTest", "Python");
        addRelated("REST Assured", "REST APIs");
        addRelated("Postman", "API Testing");

        // Programming concepts
        addRelated("Object-Oriented Design", "OOP");
        addRelated("Design Patterns", "OOP");
        addRelated("System Design", "Distributed Systems");
        addRelated("Database Optimization", "SQL");
    }

    // ==================================
    // PUBLIC METHODS
    // ==================================

    public int getWeight(String skill) {

        return skillWeights.getOrDefault(
                normalize(skill), 1);
    }

    public List<String> getRelatedSkills(String skill) {

        return relatedSkills.getOrDefault(
                normalize(skill), List.of());
    }
}
