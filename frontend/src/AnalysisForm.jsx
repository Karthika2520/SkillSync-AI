
import { useEffect, useState } from "react";
import "./AnalysisForm.css";

const API = "http://localhost:8080/api";

function getAuthHeaders() {
  const token = sessionStorage.getItem("token");

  if (!token) {
    throw new Error("Please login first.");
  }

  return {
    Authorization: `Bearer ${token}`
  };
}

export default function AnalysisForm({ onStartPractice }) {

  const [resumes, setResumes] = useState([]);
  const [jobs, setJobs] = useState([]);

  const [resumeId, setResumeId] = useState("");
  const [jobDescriptionId, setJobDescriptionId] = useState("");

  const [result, setResult] = useState(null);
  const [message, setMessage] = useState("");

  // Previous analysis states
  const [history, setHistory] = useState([]);
  const [loadingHistory, setLoadingHistory] = useState(true);
  const [historyMessage, setHistoryMessage] = useState("");

  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  // =========================================
  // LOAD RESUMES, JOBS AND PREVIOUS ANALYSES
  // =========================================

  async function loadData() {
    setLoading(true);
    setMessage("");

    try {
      const headers = getAuthHeaders();

      const [resumeResponse, jobResponse] = await Promise.all([
        fetch(`${API}/resumes`, { headers }),
        fetch(`${API}/jobs`, { headers })
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
        throw new Error("Unable to load resumes or job descriptions.");
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

    // Load history independently
    loadAnalysisHistory();
  }

  // =========================================
  // LOAD PREVIOUS ANALYSES
  // =========================================

  async function loadAnalysisHistory() {
    setLoadingHistory(true);
    setHistoryMessage("");

    try {
      const response = await fetch(`${API}/analysis`, {
        method: "GET",
        headers: getAuthHeaders()
      });

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("Session expired. Please login again.");
        }

        throw new Error("Unable to load previous analyses.");
      }

      const data = await response.json();

      setHistory(Array.isArray(data) ? data : []);

    } catch (error) {
      setHistoryMessage(error.message);
    } finally {
      setLoadingHistory(false);
    }
  }

  // =========================================
  // VIEW PREVIOUS ANALYSIS
  // =========================================

  function viewPreviousAnalysis(analysis) {
    setResult(analysis);

    setResumeId(String(analysis.resumeId));
    setJobDescriptionId(String(analysis.jobdescriptionId));

    setMessage("Previous analysis loaded successfully.");

    // Scroll to the report
    setTimeout(() => {
      document.querySelector(".analysis-report")?.scrollIntoView({
        behavior: "smooth",
        block: "start"
      });
    }, 100);
  }

  // =========================================
  // CREATE NEW ANALYSIS
  // =========================================

  async function createAnalysis(e) {
    e.preventDefault();

    if (!resumeId || !jobDescriptionId) {
      setMessage("Please select both a resume and a job description.");
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
          data.detail || data.message || "Analysis failed."
        );
      }

      setResult(data);
      setMessage("Your resume analysis is ready.");

      // Refresh previous analyses after creating a new one
      await loadAnalysisHistory();

    } catch (error) {
      setMessage(error.message);
    } finally {
      setAnalyzing(false);
    }
  }

  // =========================================
  // RESULT DATA
  // =========================================

  const selectedJob = jobs.find(
    job => String(job.id) === String(result?.jobdescriptionId || jobDescriptionId)
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


      {/* NEW ANALYSIS FORM */}

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
          <form onSubmit={createAnalysis} className="analysis-form">

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
                onChange={e => setJobDescriptionId(e.target.value)}
                required
              >
                <option value="">Choose your target role</option>

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


      {/* =====================================
          PREVIOUS ANALYSES
      ===================================== */}

      <section className="previous-analyses">

        <div className="section-heading">
          <div>
            <h3>Previous Analyses</h3>
            <p>View and revisit your previous resume analyses.</p>
          </div>

          <span className="history-count">
            {history.length}
          </span>
        </div>

        {loadingHistory ? (
          <p className="analysis-loading">
            Loading previous analyses...
          </p>
        ) : historyMessage ? (
          <div className="analysis-message error">
            {historyMessage}
            <button type="button" onClick={loadAnalysisHistory}>
              Retry
            </button>
          </div>
        ) : history.length === 0 ? (
          <div className="analysis-empty">
            <div className="empty-icon">◎</div>
            <h3>No previous analyses</h3>
            <p>
              Your completed resume analyses will appear here.
            </p>
          </div>
        ) : (
          <div className="analysis-history-list">

            {history.map(analysis => {

              const analysisScore = Number(analysis.matchScore || 0);

              const historyJob = jobs.find(
                job => String(job.id) === String(analysis.jobdescriptionId)
              );

              const historyResume = resumes.find(
                resume => String(resume.id) === String(analysis.resumeId)
              );

              return (
                <div className="analysis-history-card" key={analysis.id}>

                  <div className="history-card-top">

                    <div>
                      <span className="analysis-eyebrow">
                        ANALYSIS #{analysis.id}
                      </span>

                      <h4>
                        {historyJob?.title || `Job Description #${analysis.jobdescriptionId}`}
                      </h4>

                      <p>
                        {historyResume?.fileName ||
                          historyResume?.filename ||
                          `Resume #${analysis.resumeId}`}
                      </p>
                    </div>

                    <div className="history-score">
                      <strong>
                        {analysisScore.toFixed(0)}%
                      </strong>
                      <span>Match</span>
                    </div>

                  </div>

                  <div className="history-card-bottom">

                    <span className="history-status">
                      {analysis.status}
                    </span>

                    <span className="history-date">
                      {analysis.createdAt
                        ? new Date(analysis.createdAt).toLocaleDateString()
                        : ""}
                    </span>

                    <button
                      type="button"
                      className="history-view-button"
                      onClick={() => viewPreviousAnalysis(analysis)}
                    >
                      View Report →
                    </button>

                  </div>

                </div>
              );
            })}

          </div>
        )}

      </section>


      {/* =====================================
          ANALYSIS REPORT
      ===================================== */}

      {result && (
        <section className="analysis-report">

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

          <button
            type="button"
            className="practice-start-button"
            onClick={() => onStartPractice(result.id)}
          >
            Practice My Weak Areas →
          </button>

        </section>
      )}

    </div>
  );
}


// =========================================
// REUSABLE SKILL CATEGORY COMPONENT
// =========================================

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
