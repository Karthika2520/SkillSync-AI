
import { useEffect, useState } from "react";
import "./ResumeManager.css";

const API = "http://localhost:8080/api";

export default function ResumeManager() {
  const [file, setFile] = useState(null);
  const [resumes, setResumes] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);

  async function loadResumes() {
    setLoading(true);

    try {
      const response = await fetch(`${API}/resumes`);

      if (!response.ok) {
        throw new Error("Unable to load resumes. Check the backend.");
      }

      const data = await response.json();
      setResumes(data);
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadResumes();
  }, []);

  async function uploadResume(e) {
    e.preventDefault();

    if (!file) {
      setMessage("Please select a PDF file first.");
      return;
    }

    if (
      file.type !== "application/pdf" ||
      !file.name.toLowerCase().endsWith(".pdf")
    ) {
      setMessage("Only PDF files are supported.");
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      setMessage("The maximum file size is 5 MB.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    setUploading(true);
    setMessage("");

    try {
      const response = await fetch(`${API}/resumes/upload`, {
        method: "POST",
        body: formData
      });

      const result = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          result.detail ||
          result.message ||
          "Resume upload failed."
        );
      }

      setFile(null);
      e.target.reset();
      setMessage("Resume uploaded and text extracted successfully.");

      await loadResumes();
    } catch (error) {
      setMessage(error.message);
    } finally {
      setUploading(false);
    }
  }

  return (
    <div className="resume-manager">
      <section className="rm-section">
        <div className="rm-section-heading">
          <div>
            <span className="rm-eyebrow">DOCUMENT MANAGEMENT</span>
            <h2>Upload Resume</h2>
            <p>
              Upload your PDF resume to prepare for personalized
              skill-gap analysis.
            </p>
          </div>

          <div className="rm-heading-icon">↑</div>
        </div>

        {message && (
          <div className="rm-message" role="status">
            {message}
          </div>
        )}

        <form className="rm-upload-form" onSubmit={uploadResume}>
          <label className="rm-upload-area">
            <span className="rm-upload-icon">↑</span>
            <strong>
              {file ? file.name : "Choose your resume PDF"}
            </strong>
            <span>
              {file
                ? `${(file.size / 1024).toFixed(1)} KB selected`
                : "PDF format · Maximum size 5 MB"}
            </span>

            <input
              type="file"
              accept=".pdf,application/pdf"
              onChange={e =>
                setFile(e.target.files?.[0] || null)
              }
              disabled={uploading}
            />
          </label>

          <button
            className="rm-btn"
            type="submit"
            disabled={uploading}
          >
            {uploading ? "Uploading..." : "Upload Resume"}
          </button>
        </form>
      </section>

      <section className="rm-section">
        <div className="rm-list-heading">
          <div>
            <span className="rm-eyebrow">YOUR DOCUMENTS</span>
            <h2>Uploaded Resumes</h2>
          </div>

          <span className="rm-count">{resumes.length} resumes</span>
        </div>

        {loading && (
          <p className="rm-empty">Loading resumes...</p>
        )}

        {!loading && resumes.length === 0 && (
          <div className="rm-empty">
            <div className="rm-empty-icon">▤</div>
            <h3>No resumes uploaded yet</h3>
            <p>
              Your uploaded resumes will appear here.
            </p>
          </div>
        )}

        {!loading &&
          resumes.map(resume => (
            <article className="rm-card" key={resume.id}>
              <div className="rm-file-icon">PDF</div>

              <div className="rm-file-details">
                <strong>{resume.fileName}</strong>

                <p>
                  Uploaded{" "}
                  {resume.uploadedAt
                    ? new Date(resume.uploadedAt).toLocaleString()
                    : "date unavailable"}
                </p>

                <span className="rm-file-status">
                  Text extracted
                </span>
              </div>

              <div className="rm-text-info">
                <strong>
                  {resume.extractedText?.length || 0}
                </strong>
                <span>Characters</span>
              </div>
            </article>
          ))}
      </section>
    </div>
  );
}
