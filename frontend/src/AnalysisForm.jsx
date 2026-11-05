
import { useEffect, useState } from "react";
import "./AnalysisForm.css";

const API = "http://localhost:8080/api";

// Get JWT authentication header
function getAuthHeaders() {
  const token = sessionStorage.getItem("token");

  if (!token) {
    throw new Error("Please login first.");
  }

  return {
    Authorization: `Bearer ${token}`
  };
}

export default function AnalysisForm() {
  const [resumes, setResumes] = useState([]);
  const [jobs, setJobs] = useState([]);

  const [resumeId, setResumeId] = useState("");
  const [jobDescriptionId, setJobDescriptionId] = useState("");

  const [result, setResult] = useState(null);
  const [message, setMessage] = useState("");

  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);

  // Load resumes and job descriptions
  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    setLoading(true);
    setMessage("");

    try {
      const headers = getAuthHeaders();

      const [resumeResponse, jobResponse] = await Promise.all([
        fetch(`${API}/resumes`, {
          method: "GET",
          headers
        }),
        fetch(`${API}/jobs`, {
          method: "GET",
          headers
        })
      ]);

      if (
        resumeResponse.status === 401 ||
        resumeResponse.status === 403 ||
        jobResponse.status === 401 ||
        jobResponse.status === 403
      ) {
        throw new Error("Session expired. Please login again.");
      }

      if (!resumeResponse.ok || !jobResponse.ok) {
        throw new Error(
          "Unable to load resumes or job descriptions."
        );
      }

      const resumeData = await resumeResponse.json();
      const jobData = await jobResponse.json();

      setResumes(resumeData);
      setJobs(jobData);

    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  // Create resume analysis
  async function createAnalysis(e) {
    e.preventDefault();

    if (!resumeId || !jobDescriptionId) {
      setMessage(
        "Please select both a resume and a job description."
      );
      return;
    }

    setAnalyzing(true);
    setMessage("");
    setResult(null);

    try {
      const response = await fetch(`${API}/analysis`, {
        method: "POST",
        headers: {
          ...getAuthHeaders(),
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          resumeId: Number(resumeId),
          jobdescriptionId: Number(jobDescriptionId)
        })
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("Session expired. Please login again.");
        }

        throw new Error(
          data.detail ||
          data.message ||
          "Analysis failed."
        );
      }

      setResult(data);
      setMessage("Your resume analysis is ready.");

    } catch (error) {
      setMessage(error.message);
    } finally {
      setAnalyzing(false);
    }
  }

  const selectedJob = jobs.find(
    job => String(job.id) === String(jobDescriptionId)
  );

  const matchingResult = result?.matchingResult || {};

  const matchedSkills = matchingResult.matchedSkills || [];
  const partialSkills = matchingResult.partialSkills || [];
  const missingSkills = matchingResult.missingSkills || [];

  const score = Number(result?.matchScore ?? 0);
  const safeScore = Math.min(100, Math.max(0, score));

  return (
    <div className="analysis-page">

      {/* PAGE INTRODUCTION */}
      <div className="analysis-page-heading">
        <div>
          <span className="analysis-eyebrow">
            CAREER INTELLIGENCE
          </span>

          <p>
            Discover how well your skills match your target job.
            Identify gaps and improve your career readiness.
          </p>
        </div>

        <div className="analysis-heading-icon">✦</div>
      </div>

      {/* ANALYSIS FORM */}
      <section className="analysis-form-card">

        <div className="section-heading">
          <div>
            <h3>Start New Analysis</h3>
            <p>Select a resume and job description to compare.</p>
          </div>

          <span className="section-icon">◎</span>
        </div>

        {message && (
          <div
            className={`analysis-message ${
              result ? "success" : "error"
            }`}
            role="status"
          >
            {message}
          </div>
        )}

        {loading ? (
          <p className="analysis-loading">
            Loading your data...
          </p>
        ) : (
          <form
            onSubmit={createAnalysis}
            className="analysis-form"
          >

            <div className="analysis-input-group">
              <label htmlFor="resume-select">
                Select Resume
              </label>

              <select
                id="resume-select"
                value={resumeId}
                onChange={e => setResumeId(e.target.value)}
                required
              >
                <option value="">Choose your resume</option>

                {resumes.map(resume => (
                  <option key={resume.id} value={resume.id}>
                    {resume.fileName ||
                      resume.filename ||
                      resume.name ||
                      `Resume ${resume.id}`}
                  </option>
                ))}
              </select>
            </div>

            <div className="analysis-input-group">
              <label htmlFor="job-select">
                Select Job Description
              </label>

              <select
                id="job-select"
                value={jobDescriptionId}
                onChange={e =>
                  setJobDescriptionId(e.target.value)
                }
                required
              >
                <option value="">
                  Choose your target role
                </option>

                {jobs.map(job => (
                  <option key={job.id} value={job.id}>
                    {job.title}
                    {job.company ? ` - ${job.company}` : ""}
                  </option>
                ))}
              </select>
            </div>

            <button
              type="submit"
              className="analysis-submit"
              disabled={analyzing}
            >
              {analyzing ? "Analyzing..." : "Analyze Resume"}
              {!analyzing && <span>→</span>}
            </button>

          </form>
        )}

        {!loading && resumes.length === 0 && (
          <p className="analysis-helper">
            Upload a resume in My Resumes to get started.
          </p>
        )}

        {!loading && jobs.length === 0 && (
          <p className="analysis-helper">
            Add a job description before starting an analysis.
          </p>
        )}

      </section>

      {/* RESULTS */}
      {result && (
        <section className="analysis-report">

          {/* REPORT HEADER */}
          <div className="report-heading">
            <div>
              <span className="analysis-eyebrow">
                ANALYSIS REPORT
              </span>

              <h3>Job Match Results</h3>

              <p>
                {selectedJob?.title || "Selected Job"}
                {selectedJob?.company
                  ? ` · ${selectedJob.company}`
                  : ""}
              </p>
            </div>

            <span className="report-status">
              ✓ {result.status}
            </span>
          </div>

          {/* REPORT BODY */}
          <div className="report-body">

            {/* SCORE CARD */}
            <div className="score-card">

              <span className="score-label">
                OVERALL MATCH SCORE
              </span>

              <div
                className="score-ring"
                style={{
                  "--score": `${safeScore * 3.6}deg`
                }}
              >
                <div className="score-ring-content">
                  <strong>{safeScore.toFixed(0)}%</strong>
                  <span>Match</span>
                </div>
              </div>

              <h4>
                {safeScore >= 75
                  ? "Strong Match"
                  : safeScore >= 50
                  ? "Moderate Match"
                  : "Needs Improvement"}
              </h4>

              <p>
                {safeScore >= 75
                  ? "Your skills align well with this job."
                  : safeScore >= 50
                  ? "You have a good foundation. Improve the missing skills."
                  : "Focus on building the required skills for this role."}
              </p>

              <div className="score-breakdown">

                <div>
                  <strong>{matchedSkills.length}</strong>
                  <span>Matched</span>
                </div>

                <div>
                  <strong>{partialSkills.length}</strong>
                  <span>Partial</span>
                </div>

                <div>
                  <strong>{missingSkills.length}</strong>
                  <span>Missing</span>
                </div>

              </div>

            </div>

            {/* SKILL RESULTS */}
            <div className="skill-results">

              <SkillCategory
                title="Matched Skills"
                skills={matchedSkills}
                type="matched"
              />

              <SkillCategory
                title="Partially Matched Skills"
                skills={partialSkills}
                type="partial"
              />

              <SkillCategory
                title="Missing Skills"
                skills={missingSkills}
                type="missing"
              />

            </div>

          </div>

          {/* SUMMARY */}
          {matchingResult.summary && (
            <div className="analysis-summary">
              <div className="summary-icon">✦</div>

              <div>
                <h4>Analysis Insights</h4>
                <p>{matchingResult.summary}</p>
              </div>
            </div>
          )}

          <div className="report-footer">
            <span>Analysis ID: #{result.id}</span>
            <span>SkillSync AI</span>
          </div>

        </section>
      )}

      {/* EMPTY STATE */}
      {!result && !loading && (
        <section className="analysis-empty">
          <div className="empty-icon">◎</div>

          <h3>Your analysis report will appear here</h3>

          <p>
            Select your resume and target job above. Your match score,
            matched skills, partial skills, and missing skills will
            appear here after analysis.
          </p>
        </section>
      )}

    </div>
  );
}


/* REUSABLE SKILL CATEGORY COMPONENT */

function SkillCategory({ title, skills, type }) {

  function getSkillName(item) {
    if (typeof item === "string") return item;

    return item?.skill ||
      item?.skillName ||
      item?.name ||
      "Unknown Skill";
  }

  function getEvidence(item) {
    if (typeof item === "string") return "";

    return item?.evidence ||
      item?.explanation ||
      item?.reason ||
      "";
  }

  const symbol = {
    matched: "✓",
    partial: "◐",
    missing: "!"
  };

  return (
    <div className={`skill-category ${type}`}>

      <div className="skill-category-heading">
        <div>
          <span className="category-symbol">
            {symbol[type]}
          </span>

          <h4>{title}</h4>
        </div>

        <span className="category-count">
          {skills.length}
        </span>
      </div>

      {skills.length === 0 ? (
        <p className="no-skills">
          No skills in this category.
        </p>
      ) : (
        <div className="skill-list">

          {skills.map((item, index) => (
            <div className="skill-item" key={index}>

              <div className="skill-item-name">
                <span>{getSkillName(item)}</span>

                <span className="skill-check">
                  {symbol[type]}
                </span>
              </div>

              {getEvidence(item) && (
                <p className="skill-evidence">
                  {getEvidence(item)}
                </p>
              )}

            </div>
          ))}

        </div>
      )}

    </div>
  );
}
