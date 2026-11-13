
import { useEffect, useState } from "react";
import "./SavedJobs.css";

// const API = "http://localhost:8080/api";
const API = import.meta.env.VITE_API_BASE_URL;

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

export default function SavedJobs() {
  const [jobs, setJobs] = useState([]);
  const [title, setTitle] = useState("");
  const [company, setCompany] = useState("");
  const [jobUrl, setJobUrl] = useState("");
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Load all saved jobs
  async function loadJobs() {
    setLoading(true);

    try {
      const response = await fetch(`${API}/saved-jobs`, {
        method: "GET",
        headers: getAuthHeaders()
      });

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("Session expired. Please login again.");
        }

        throw new Error("Unable to load saved jobs.");
      }

      const data = await response.json();
      setJobs(data);

    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  // Load jobs when component opens
  useEffect(() => {
    loadJobs();
  }, []);

  // Save a new job
  async function saveJob(e) {
    e.preventDefault();

    if (!title.trim()) {
      setMessage("Job title is required.");
      return;
    }

    setSaving(true);
    setMessage("");

    try {
      const response = await fetch(`${API}/saved-jobs`, {
        method: "POST",
        headers: {
          ...getAuthHeaders(),
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          title: title.trim(),
          company: company.trim(),
          jobUrl: jobUrl.trim(),
          description: description.trim()
        })
      });

      if (!response.ok) {
        const errorText = await response.text();

        let errorMessage = "Unable to save this job.";

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
      setJobUrl("");
      setDescription("");

      setMessage("Job saved successfully.");

      // Refresh saved jobs list
      await loadJobs();

    } catch (error) {
      setMessage(error.message);
    } finally {
      setSaving(false);
    }
  }

  // Delete a saved job
  async function deleteJob(id) {
    const confirmed = window.confirm(
      "Are you sure you want to remove this saved job?"
    );

    if (!confirmed) return;

    setMessage("");

    try {
      const response = await fetch(`${API}/saved-jobs/${id}`, {
        method: "DELETE",
        headers: getAuthHeaders()
      });

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("Session expired. Please login again.");
        }

        throw new Error("Unable to remove this job.");
      }

      setJobs(currentJobs =>
        currentJobs.filter(job => job.id !== id)
      );

      setMessage("Job removed successfully.");

    } catch (error) {
      setMessage(error.message);
    }
  }

  return (
    <div className="saved-jobs-page">

      {/* Save Job Form */}
      <section className="sj-section">

        <div className="sj-heading">
          <div>
            <span className="sj-eyebrow">
              OPPORTUNITY TRACKER
            </span>

            <h2>Save a Job</h2>

            <p>
              Keep interesting opportunities in one place.
            </p>
          </div>

          <div className="sj-heading-icon">＋</div>
        </div>

        {message && (
          <div className="sj-message" role="status">
            {message}
          </div>
        )}

        <form className="sj-form" onSubmit={saveJob}>

          <div className="sj-field">
            <label htmlFor="job-title">
              Job title *
            </label>

            <input
              id="job-title"
              className="sj-input"
              required
              maxLength={200}
              placeholder="e.g. Software Development Engineer"
              value={title}
              onChange={e => setTitle(e.target.value)}
            />
          </div>

          <div className="sj-field">
            <label htmlFor="job-company">
              Company
            </label>

            <input
              id="job-company"
              className="sj-input"
              maxLength={200}
              placeholder="e.g. Amazon"
              value={company}
              onChange={e => setCompany(e.target.value)}
            />
          </div>

          <div className="sj-field sj-full-width">
            <label htmlFor="job-url">
              Application URL
            </label>

            <input
              id="job-url"
              className="sj-input"
              type="url"
              placeholder="https://company.com/careers/..."
              value={jobUrl}
              onChange={e => setJobUrl(e.target.value)}
            />
          </div>

          <div className="sj-field sj-full-width">
            <label htmlFor="job-description">
              Job description
            </label>

            <textarea
              id="job-description"
              className="sj-input sj-textarea"
              rows={4}
              maxLength={10000}
              placeholder="Paste the job description here..."
              value={description}
              onChange={e => setDescription(e.target.value)}
            />
          </div>

          <div className="sj-full-width">
            <button
              className="sj-button"
              type="submit"
              disabled={saving}
            >
              {saving ? "Saving..." : "+ Save Job"}
            </button>
          </div>

        </form>
      </section>

      {/* Saved Jobs List */}
      <section className="sj-section">

        <div className="sj-list-heading">
          <div>
            <span className="sj-eyebrow">
              YOUR OPPORTUNITIES
            </span>

            <h2>My Saved Jobs</h2>
          </div>

          <span className="sj-count">
            {jobs.length} jobs
          </span>
        </div>

        {loading && (
          <p className="sj-empty">
            Loading saved jobs...
          </p>
        )}

        {!loading && jobs.length === 0 && (
          <div className="sj-empty">
            <div className="sj-empty-icon">☆</div>

            <h3>No saved jobs yet</h3>

            <p>
              Add your first job opportunity using the form above.
            </p>
          </div>
        )}

        {!loading &&
          jobs.map(job => (
            <article className="sj-card" key={job.id}>

              <div className="sj-company-icon">
                {(job.company || job.title || "J")
                  .charAt(0)
                  .toUpperCase()}
              </div>

              <div className="sj-job-details">

                <h3>{job.title}</h3>

                <p className="sj-company">
                  {job.company || "Company not specified"}
                </p>

                {job.description && (
                  <p className="sj-description">
                    {job.description.length > 180
                      ? `${job.description.slice(0, 180)}...`
                      : job.description}
                  </p>
                )}

                <div className="sj-card-actions">

                  {job.jobUrl && (
                    <a
                      className="sj-application-link"
                      href={job.jobUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      View application ↗
                    </a>
                  )}

                  <button
                    className="sj-delete"
                    type="button"
                    onClick={() => deleteJob(job.id)}
                  >
                    Remove
                  </button>

                </div>
              </div>
            </article>
          ))}

      </section>
    </div>
  );
}
