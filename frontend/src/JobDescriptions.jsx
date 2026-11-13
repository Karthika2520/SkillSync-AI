
import { useEffect, useState } from "react";
import "./JobDescriptions.css";

// const API = "http://localhost:8080/api/jobs";
const API = `${import.meta.env.VITE_API_BASE_URL}/jobs`;

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

export default function JobDescriptions() {
  const [jobs, setJobs] = useState([]);
  const [title, setTitle] = useState("");
  const [company, setCompany] = useState("");
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Load all job descriptions
  async function loadJobs() {
    setLoading(true);

    try {
      const response = await fetch(API, {
        method: "GET",
        headers: getAuthHeaders()
      });

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("Session expired. Please login again.");
        }

        throw new Error("Unable to load job descriptions.");
      }

      const data = await response.json();
      setJobs(data);

    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  // Load job descriptions when component opens
  useEffect(() => {
    loadJobs();
  }, []);

  // Add a new job description
  async function addJob(e) {
    e.preventDefault();

    if (!title.trim() || !description.trim()) {
      setMessage("Job title and description are required.");
      return;
    }

    setSaving(true);
    setMessage("");

    try {
      const response = await fetch(API, {
        method: "POST",
        headers: {
          ...getAuthHeaders(),
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          title: title.trim(),
          company: company.trim(),
          description: description.trim()
        })
      });

      if (!response.ok) {
        const errorText = await response.text();

        let errorMessage = "Unable to save the job description.";

        try {
          const result = JSON.parse(errorText);

          errorMessage =
            result.detail ||
            result.message ||
            errorMessage;
        } catch {
          if (errorText) {
            errorMessage = errorText;
          }
        }

        if (response.status === 401 || response.status === 403) {
          errorMessage = "Session expired. Please login again.";
        }

        throw new Error(errorMessage);
      }

      // Clear form after successful save
      setTitle("");
      setCompany("");
      setDescription("");

      setMessage("Job description saved successfully.");

      // Refresh job descriptions
      await loadJobs();

    } catch (error) {
      setMessage(error.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="job-descriptions-page">

      {/* Add Job Description Form */}
      <section className="jd-section">

        <h2>Add Job Description</h2>

        <p className="jd-subtitle">
          Add a target role so SkillSync AI can help analyze its required skills.
        </p>

        {message && (
          <p className="jd-message" role="status">
            {message}
          </p>
        )}

        <form onSubmit={addJob} className="jd-form">

          <label htmlFor="jd-title">
            Job Title *
          </label>

          <input
            id="jd-title"
            required
            value={title}
            onChange={e => setTitle(e.target.value)}
            placeholder="Software Development Engineer"
          />

          <label htmlFor="jd-company">
            Company
          </label>

          <input
            id="jd-company"
            value={company}
            onChange={e => setCompany(e.target.value)}
            placeholder="Amazon, Microsoft, etc."
          />

          <label htmlFor="jd-description">
            Job Description *
          </label>

          <textarea
            id="jd-description"
            required
            rows={6}
            value={description}
            onChange={e => setDescription(e.target.value)}
            placeholder="Paste the complete job description here..."
          />

          <button type="submit" disabled={saving}>
            {saving ? "Saving..." : "Save Job Description"}
          </button>

        </form>
      </section>

      {/* Available Job Descriptions */}
      <section className="jd-section">

        <h2>
          Available Job Descriptions ({jobs.length})
        </h2>

        {loading && (
          <p>Loading job descriptions...</p>
        )}

        {!loading && jobs.length === 0 && (
          <p className="jd-empty">
            No job descriptions added yet.
          </p>
        )}

        {!loading && jobs.map((job, index) => (
          <article className="jd-card" key={job.id ?? index}>

            <h3>{job.title}</h3>

            <p className="jd-company">
              {job.company || "Company not specified"}
            </p>

            <p className="jd-description">
              {job.description}
            </p>

          </article>
        ))}

      </section>
    </div>
  );
}
