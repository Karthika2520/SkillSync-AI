
import { useEffect, useState } from "react";
import "./AnalysisForm.css";

const API = "http://localhost:8080/api";

export default function AnalysisForm() {
  const [resumes, setResumes] = useState([]);
  const [jobs, setJobs] = useState([]);

  const [resumeId, setResumeId] = useState("");
  const [jobDescriptionId, setJobDescriptionId] = useState("");

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);
  const [result, setResult] = useState(null);

  useEffect(() => {
    async function loadOptions() {
      setLoading(true);
      setError(false);
      setMessage("");

      try {
        const [resumeResponse, jobResponse] = await Promise.all([
          fetch(`${API}/resumes`),
          fetch(`${API}/jobs`)
        ]);

        if (!resumeResponse.ok || !jobResponse.ok) {
          throw new Error(
            "Unable to load resumes or job descriptions. Check the backend."
          );
        }

        const resumeData = await resumeResponse.json();
        const jobData = await jobResponse.json();

        setResumes(resumeData);
        setJobs(jobData);
      } catch (err) {
        setError(true);
        setMessage(err.message || "Unable to load the required data.");
      } finally {
        setLoading(false);
      }
    }

    loadOptions();
  }, []);

  async function createAnalysis(e) {
    e.preventDefault();

    if (!resumeId || !jobDescriptionId) {
      setError(true);
      setMessage("Please select both a resume and a job description.");
      return;
    }

    setSubmitting(true);
    setError(false);
    setMessage("");
    setResult(null);

    try {
      const response = await fetch(`${API}/analysis`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          resumeId: Number(resumeId),
          jobdescriptionId: Number(jobDescriptionId)
        })
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          data.message ||
          data.detail ||
          "Unable to create the analysis. Check your backend."
        );
      }

      setResult(data);
      setMessage("Analysis record created successfully.");
    } catch (err) {
      setError(true);
      setMessage(err.message || "Something went wrong.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="analysis-page">
      <section className="analysis-card">
        <span className="analysis-eyebrow">RESUME AND ROLE MATCHING</span>
        <h2>Analyze Your Career Fit</h2>
        <p className="analysis-subtitle">
          Choose a resume and a target job description to create an
          analysis record.
        </p>

        {message && (
          <div
            className={`analysis-message ${error ? "error" : "success"}`}
            role="status"
          >
            {message}
          </div>
        )}

        {loading ? (
          <p>Loading resumes and job descriptions...</p>
        ) : (
          <form className="analysis-form" onSubmit={createAnalysis}>
            <label htmlFor="analysis-resume">Select Resume *</label>
            <select
              id="analysis-resume"
              value={resumeId}
              onChange={e => setResumeId(e.target.value)}
              required
            >
              <option value="">Choose a resume</option>
              {resumes.map(resume => (
                <option key={resume.id} value={resume.id}>
                  {resume.filename ||
                    resume.fileName ||
                    `Resume #${resume.id}`}
                </option>
              ))}
            </select>

            {resumes.length === 0 && (
              <p className="analysis-hint">
                Upload a resume from the My Resumes page first.
              </p>
            )}

            <label htmlFor="analysis-job">Select Job Description *</label>
            <select
              id="analysis-job"
              value={jobDescriptionId}
              onChange={e => setJobDescriptionId(e.target.value)}
              required
            >
              <option value="">Choose a job description</option>
              {jobs.map(job => (
                <option key={job.id} value={job.id}>
                  {job.title}
                  {job.company ? ` — ${job.company}` : ""}
                </option>
              ))}
            </select>

            {jobs.length === 0 && (
              <p className="analysis-hint">
                Add a job description from the Job Descriptions page first.
              </p>
            )}

            <button
              type="submit"
              className="analysis-button"
              disabled={
                submitting ||
                resumes.length === 0 ||
                jobs.length === 0
              }
            >
              {submitting ? "Creating Analysis..." : "Analyze My Fit"}
            </button>
          </form>
        )}
      </section>

      {result && (
        <section className="analysis-result">
          <span className="analysis-result-icon">✓</span>
          <div>
            <h3>Analysis Created</h3>
            <p>Analysis ID: {result.id}</p>
            <p>Status: {result.status || "PENDING"}</p>
            {result.matchScore != null && (
              <p>Match Score: {result.matchScore}%</p>
            )}
            <p className="analysis-hint">
              The record is saved. Actual resume-to-job matching will be
              implemented in a later step.
            </p>
          </div>
        </section>
      )}
    </div>
  );
}
